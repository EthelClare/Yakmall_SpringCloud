package com.yakmall.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.api.client.CartClient;
import com.yakmall.api.client.ItemClient;
import com.yakmall.api.dto.ItemQueryDTO;
import com.yakmall.common.exception.BadRequestException;
import com.yakmall.common.exception.BusinessException;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.UserContext;
import com.yakmall.trade.domain.dto.OrderDetailDTO;
import com.yakmall.trade.domain.dto.OrderFormDTO;
import com.yakmall.trade.domain.po.Order;
import com.yakmall.trade.domain.po.OrderDetail;
import com.yakmall.trade.mapper.OrderMapper;
import com.yakmall.trade.service.IOrderDetailService;
import com.yakmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

//    private final IItemService itemService;
    private final ItemClient itemClient;

    private final IOrderDetailService orderDetailService;
//    private final ICarService carService;
    private final CartClient cartClient;

    private final OrderMapper orderMapper;


    //TODO 这里已经实现了基本的创建订单，但是还有一些问题，包括【商品库存扣了，但是商品的sold字段没有更新，更新时间没变】
    @Override
    public Result<Long> createOrder(OrderFormDTO orderFormDTO) {
        //已经使用了注解来进行了对数据的校验
        //1.确定订单数据
        List<OrderDetailDTO> details = orderFormDTO.getDetails();
        //将订单中的下单商品id和num拿出来合成一个Map
        Map<Long, Integer> itemNumMap = details.stream()
                .collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));

        //检查数据库中的数据是否合规
//        List<ItemQueryDTO> items = itemService.queryItemByIds(itemNumMap.keySet()).getData();
        List<Long> itemNumList = new ArrayList<>(itemNumMap.keySet());

        List<ItemQueryDTO> items = itemClient.getItems(itemNumList).getData();

        validDateItems(items, itemNumMap);
        //3.  扣件库存「需要被提到事务的最前面」
        Result<Void> voidResult = itemClient.deductItemStock(itemNumMap);
//        Result<Void> voidResult = itemService.deductStock(itemNumMap);
        //4.创建订单
        Integer total = calculateTotal(items, itemNumMap);
        Order order = buildOrder(orderFormDTO, total);
        save(order);


        // 4. 保存明细
        List<OrderDetail> orderDetails = buildOrderDetails(order.getId(), items, itemNumMap);
        orderDetailService.saveBatch(orderDetails);
        // 5. 异步清理购物车
        //6.sql执行
//        asyncCleanCart(UserContext.getUser(), itemNumMap.keySet());
//        carService.cleanCartItems(UserContext.getUser(), itemNumMap.keySet());
        cartClient.cleanUserCartItems(UserContext.getUser(), itemNumMap.keySet());
        return Result.success(order.getId());
    }

    @Override
    public Result<Void> changeStatus(Long orderId, Integer status) {
        //校验数据，尤其是状态参数
        if(status == null || status >= 6 || status <= 0) {
            throw new BadRequestException("状态参数不符合规定");
        }
        orderMapper.updateStatus(orderId, status);
        log.info("订单{}成功修改状态为{}", orderId, status);
        return Result.success().msg("订单" + orderId + "成功修改状态为" +status );


    }

//    HikariProxyCallableStatement asyncTaskExecutor;
//    /**
//     * 异步清理购物车（失败重试机制）
//     */
//    private void asyncCleanCart(Long userId, Set<Long> itemIds) {
//
//        asyncTaskExecutor.execute(() -> {
//            try {
//                carService.cleanCartItems(userId, itemIds);
//            } catch (Exception e) {
//                log.error("购物车清理失败，尝试重试", e);
//                // 加入重试队列
//                //TODO 这里后期换成 rabbitMQ
//                retryCartClean(userId, itemIds);
//            }
//        });
//
//    }


    /**
     * 构建订单实体（使用构建器模式）
     */
    private Order buildOrder(OrderFormDTO form, Integer totalAmount) {
        return Order.builder()
                .userId(UserContext.getUser())
                .totalAmount(totalAmount)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 计算订单总金额（精确计算）
     */
    private Integer calculateTotal(List<ItemQueryDTO> items, Map<Long, Integer> itemNumMap) {
        return items.stream()
                .mapToInt(item -> {
                    Integer price = item.getPrice();
                    int quantity = itemNumMap.get(item.getId());
                    return price * quantity;
                })
                .sum();

    }

    /**
     * 构建订单明细（批量操作优化）
     */
    private List<OrderDetail> buildOrderDetails(Long orderId, List<ItemQueryDTO> items, Map<Long, Integer> itemNumMap) {
        return items.stream()
                .map(item -> new OrderDetail(
                        orderId,
                        item.getId(),
                        itemNumMap.get(item.getId()),
                        item.getName(),
                        item.getSpec(),
                        item.getPrice(),
                        item.getImage())
                )
                .collect(Collectors.toList());
    }




    private void validDateItems(List<ItemQueryDTO> items, Map<Long, Integer> itemNumMap) {
        //首先校验商品是否存在
        Set<Long> foundIds = items.stream()
                .map(ItemQueryDTO::getId)
                .collect(Collectors.toSet());

        List<Long> notFoundIds = itemNumMap.keySet().stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        if(!notFoundIds.isEmpty()) {
            throw new BusinessException("ITEM_NOT_FOUND" ,"缺失商品id：" + notFoundIds);
        }
        //校验商品的状态
        items.forEach(item -> {
            if(item.getStatus() != 1){
                throw new BusinessException("ITEM_OFF_SHELF", "商品已下架: " + item.getId());
            }
            Integer required = itemNumMap.get(item.getId());
            if(item.getStock() < required){
                throw new BusinessException("STOCK_NOT_ENOUGH",
                        "商品库存不足: " + item.getId() + " 剩余:" + item.getStock());
            }
        });

    }
}
