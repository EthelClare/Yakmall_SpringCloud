package com.yakmall.item.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.common.utils.UserContext;
import com.yakmall.item.domain.dto.ItemCreateDTO;
import com.yakmall.item.domain.dto.ItemQueryDTO;
import com.yakmall.item.domain.dto.ItemUpdateDTO;
import com.yakmall.item.domain.po.Item;
import com.yakmall.item.mapper.ItemMapper;
import com.yakmall.item.service.IItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {


    private final ItemMapper itemMapper;

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
        Long userId = UserContext.getUser();
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
        item.setUpdateUser(UserContext.getUser());

        updateById(item);
        log.info("[更新操作] 操作人： {}, 更新ID ：{}", UserContext.getUser(), item.getId());

        return Result.success().msg("更新成功");
    }

    /**
     * 批量查询
     * @param ids
     * @return
     */
    @Override
    public Result<List<ItemQueryDTO>> queryItemByIds(Collection<Long> ids) {
        return Result.success(BeanUtils.copyList(listByIds(ids), ItemQueryDTO.class));
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
}
