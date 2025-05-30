package com.yakmall.cart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.api.client.ItemClient;
import com.yakmall.api.dto.ItemQueryDTO;
import com.yakmall.cart.config.CartProperties;
import com.yakmall.cart.domain.dto.CartFormDTO;
import com.yakmall.cart.domain.dto.CartUpdateDTO;
import com.yakmall.cart.domain.po.Cart;
import com.yakmall.cart.domain.vo.CartVO;
import com.yakmall.cart.mapper.CarMapper;
import com.yakmall.cart.service.ICarService;
import com.yakmall.common.exception.BusinessException;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.common.utils.CollUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yakmall.cart.utils.CartRedisConstants.CART_CACHE_KEY_PREFIX;
import static com.yakmall.cart.utils.CartRedisConstants.EMPTY_CACHE_TTL_MINUTES;


@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CarMapper, Cart> implements ICarService {

    private final CarMapper cartMapper;
    private final ItemClient itemClient;
    private final CartProperties cartProperties;
    private final RedisTemplate redisTemplate;


    private final HttpServletRequest request;


    @Override
    @Transactional
    public Void addItemToCart(CartFormDTO cartFormDTO) {
        //添加校验
        if(cartFormDTO.getItemId() == null && cartFormDTO.getNum() > 0) {
            throw new IllegalArgumentException("商品参数不合法");
        }
        //1.获取登陆的用户
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        log.debug("用户{}尝试添加商品{}到购物车", userId, cartFormDTO.getItemId());
        // 3.尝试直接更新数量（原子操作）
        int affectedRows = baseMapper.incrementNum(
                cartFormDTO.getItemId(),
                userId,
                cartFormDTO.getNum()
        );

        // 4.更新失败说明不存在，执行新增
        if (affectedRows == 0) {
            checkCartsFull(userId); // 会抛异常
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setItemId(cartFormDTO.getItemId());
            cart.setNum(cartFormDTO.getNum());
            cart.setCreateTime(cart.getCreateTime());
            cart.setUpdateTime(cart.getUpdateTime());
            save(cart);
            clearCartCache(userId);
            log.info("用户{}购物车新增商品{}", userId, cart.getId());
            return null;
        }
        clearCartCache(userId);
        return null;
    }


    @Override
    public Result<CartUpdateDTO> updateCart(CartUpdateDTO cartUpdateDTO) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        //添加校验
        if(cartUpdateDTO.getItemId() == null && cartUpdateDTO.getNum() > 0) {
            throw new IllegalArgumentException("商品参数不合法");
        }
        Cart cart = BeanUtils.copyBean(cartUpdateDTO, Cart.class);
        cart.setUpdateTime(LocalDateTime.now());
        updateById(cart);

        //这里需要添加清除缓存
        clearCartCache(userId);
        return Result.success(cartUpdateDTO);
    }


    //optimize 购物车也加入查询缓存
    @Override
    public Result<List<CartVO>> queryMyCarts() {
        //使用用户id来查询
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        //cart:user:id
        String cacheCartKey  =  CART_CACHE_KEY_PREFIX + userId;

        // 首先尝试从缓存中获取
        List<CartVO> cacheResult = getCartsFromCache(cacheCartKey);
        if(cacheResult != null) {
            return Result.success(cacheResult);
        }

        //缓存未命中，查询数据库
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, userId).list();

        //缓存空结果，防止缓存穿透
        if(CollUtils.isEmpty(carts)) {
            cacheEmptyResult(cacheCartKey);
            return Result.success(Collections.emptyList());
        }

        //封装成VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);
        handleCartItems(vos);

        //缓存查询结果
        cacheCartResult(cacheCartKey, vos);

        return Result.success(vos);

    }

    // 缓存查询结果
    private void cacheCartResult(String cacheCartKey, List<CartVO> carts) {
        try {
            redisTemplate.opsForValue().set(cacheCartKey, carts);
        } catch (Exception e) {
            log.error("缓存购物车数据失败， key:{}", cacheCartKey, e);
        }
    }

    // 清空购物车缓存
    private void clearCartCache(Long userId) {
        try {
            redisTemplate.delete(CART_CACHE_KEY_PREFIX + userId);
        } catch (Exception e) {
            log.error("清除购物车失败， key:{}", userId, e);
        }
    }


    // 缓存空结果（防止缓存穿透）
    private void cacheEmptyResult(String cacheCartKey) {
        try{
            redisTemplate.opsForValue().set(
                    cacheCartKey,
                    CollUtils.emptyList(),
                    EMPTY_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }catch (Exception e) {
            log.error("缓存空购物车失败, key: {}", cacheCartKey, e);
        }

    }

    //从缓冲中获取购物车数据
    private List<CartVO> getCartsFromCache(String cacheCartKey) {

        try {
            Object cached = redisTemplate.opsForValue().get(cacheCartKey);
            if(cached instanceof List) {
                return (List<CartVO>) cached;
            }
        } catch (Exception e) {
            log.error("获取购物车缓存失败， key: {}", cacheCartKey, e);
        }

        return null;
    }


    private void handleCartItems(List<CartVO> vos) {
        // 1. 提取商品ID并去重[这里是通过使用 Set中不能包含重复的元素来完成的去重的功能]
        Set<Long> itemIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());
        if (CollUtils.isEmpty(itemIds)) {
            return; // 避免无意义查询
        }

        // 2. 将 Set<Long> 转换为 List<Long>
        List<Long> itemIdList = new ArrayList<>(itemIds);
        // 2. 批量查询商品信息
        List<ItemQueryDTO> items = itemClient.getItems(itemIdList).getData();
        Map<Long, ItemQueryDTO> itemMap = CollUtils.isEmpty(itemIdList)
                ? Map.of()
                : items.stream().collect(Collectors.toMap(ItemQueryDTO::getId, Function.identity()));

        // 3. 单次遍历设置VO信息
        vos.forEach(v -> {
            ItemQueryDTO item = itemMap.get(v.getItemId());
            if (item != null) {
                v.setNewPrice(item.getPrice());
                v.setStatus(item.getStatus());
                v.setStock(item.getStock());
            }
        });
    }

    // 修改检查购物车容量方法
    private void checkCartsFull(Long userId) {
        Long count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= cartProperties.getMaxAmount()) { // 假设最大100条
            throw new BusinessException("购物车已满，最多添加100件商品");
        }
    }

    @Override
    public Result<Void> cleanCartItems(Long userId, Set<Long> itemIds) {

        try {
            // 调用数据层方法
            int affectedRows = cartMapper.deleteByUserIdAndItemIds(userId, itemIds);

            // 处理结果
            if (affectedRows > 0) {
                log.info("用户[{}]清理购物车成功，删除{}条记录", userId, affectedRows);
                return Result.success();
            }
            return Result.error().msg("不存在");
        } catch (DataAccessException e) {
            log.error("购物车清理失败 | userId:{} itemIds:{} | {}", userId, itemIds, e.getMessage());
            //TODO 这里异常待处理
            throw e;
        }
    }

}
