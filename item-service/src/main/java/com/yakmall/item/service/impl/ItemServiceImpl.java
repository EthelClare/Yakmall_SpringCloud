package com.yakmall.item.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.item.domain.dto.ItemCreateDTO;
import com.yakmall.item.domain.dto.ItemQueryDTO;
import com.yakmall.item.domain.dto.ItemUpdateDTO;
import com.yakmall.item.domain.po.Item;
import com.yakmall.item.mapper.ItemMapper;
import com.yakmall.item.service.IItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yakmall.item.utils.ItemRedisConstants.CACHE_EXPIRE_MINUTES;
import static com.yakmall.item.utils.ItemRedisConstants.ITEM_CACHE_PREFIX;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    private final HttpServletRequest request;

    private final ItemMapper itemMapper;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Result<Void> createItem(ItemCreateDTO itemDTO) {
        //添加校验
        // 商品名字中不能包含特殊字符
        if (containsInvalidChars(itemDTO.getName())) {
            return Result.error(402,"不能包含特殊字符");
        }
        //添加价格判断
        if(itemDTO.getPrice() <= 0) {
            return Result.error(402,"价格不合法");
        }
        //判断库存
        if(itemDTO.getStock() <= 0) {
            return Result.error(402,"库存不能小于0");
        }


        Item item = new Item();
        BeanUtil.copyProperties(itemDTO, item);
        //防止前端传入ID
        item.setId(null);
        //默认状态正常
        item.setStatus(1);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        item.setCreateUser(userId);
        item.setUpdateUser(userId);

        // TODO再下面这段中需要加入防止用户重复提交相同商品的代码【利用redis短暂保存】
        try {
            save(item);
            return Result.success().msg("创建商品成功" + item.getName());
        } catch (Exception e) {
            log.error("上传商品失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id来修改商品
     * @param itemUpdateDTO
     * @return
     */
    @Override
    public Result<Void> updateByID(ItemUpdateDTO itemUpdateDTO) {
        //首先需要进行的是校验
        // 商品名字中不能包含特殊字符
        if (containsInvalidChars(itemUpdateDTO.getName())) {
            return Result.error(402,"不能包含特殊字符");
        }

        //添加价格判断
        if(itemUpdateDTO.getPrice() <= 0) {
            return Result.error(402,"价格不合法");
        }

        //判断库存
        if(itemUpdateDTO.getStock() <= 0) {
            return Result.error(402,"库存不能小于0");
        }

        Item item = new Item();
        BeanUtils.copyProperties(itemUpdateDTO, item);
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        item.setUpdateUser(userId);

        updateById(item);
        log.info("[更新操作] 操作人： {}, 更新ID ：{}", userId, item.getId());

        return Result.success().msg("更新成功");
    }

    /**
     * 批量查询
     * @param ids
     * @return
     */
    @Override
    public Result<List<ItemQueryDTO>> queryItemByIds(Collection<Long> ids) {
        //optimize优化成redis缓存
        //TODO 这里数据里放的是Item，但是我最后返回的结果只能是ItemQueryDTO
        //尝试从redis中去获取
        List<Item> cachedItems = getItemsFromCache(ids);

        //找出未命中的商品
        Set<Long> missedIds = findMissedIds(ids, cachedItems);

        //从数据库中去获取未命中的商品
        List<Item> dbItems = Collections.emptyList();
        if(!missedIds.isEmpty()) {
            dbItems = itemMapper.selectBatchIds(missedIds);
            //同时将数据库的查询结果存入缓存
            cacheItems(dbItems);
        }

        //合并缓存和数据库返回结果
        List<Item> allItems = new ArrayList<>(cachedItems);
        allItems.addAll(dbItems);

        // 转换为DTO并返回
        List<ItemQueryDTO> resultList = BeanUtils.copyList(allItems, ItemQueryDTO.class);

        return Result.success(resultList);
    }

    @Override
    public Result<Void> deductStock(Map<Long, Integer> itemNumMap) {


        itemMapper.batchDeductByMap(itemNumMap);
        return Result.success().msg("扣减库存成功");
    }


    private boolean containsInvalidChars(String name) {
        // 实现名称特殊字符校验逻辑
        return name.matches(".*[\\\\/<>].*");
    }

    /**
     * 从缓存中获取商品信息
     * @return
     */
    private List<Item> getItemsFromCache(Collection<Long> ids){
        //构建缓存key列表
        Collection<Object> keys = ids.stream()
                .map(id -> (Object)( ITEM_CACHE_PREFIX + id))
                .collect(Collectors.toList());

        //批量获取缓存
        List<Object> cachedItems = redisTemplate.opsForValue().multiGet(keys);
        //断言处理
        assert cachedItems != null;
        //过滤有效结果
        return cachedItems.stream()
                .filter(Objects::nonNull)
                .map(obj ->(Item) obj)
                .collect(Collectors.toList());
    }

    /**
     * 找到未命中的商品id
     *
     * @param ids
     * @param cacheItems
     * @return
     */
    private Set<Long> findMissedIds(Collection<Long> ids, List<Item> cacheItems) {
        Set<Long> cacheIds = cacheItems.stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        return ids.stream()
                .filter(id -> !cacheIds.contains(id))
                .collect(Collectors.toSet());
    }


    private void cacheItems(List<Item> items) {
        items.forEach(item -> {
            if(item != null) {
                String key = ITEM_CACHE_PREFIX + item.getId();
                redisTemplate.opsForValue().set(key, item, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            }
        });
    }

}
