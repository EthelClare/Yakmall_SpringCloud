package com.yakmall.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakmall.common.result.Result;
import com.yakmall.trade.domain.dto.OrderFormDTO;
import com.yakmall.trade.domain.po.Order;

import javax.validation.Valid;

public interface IOrderService extends IService<Order> {


    /**
     * 创建一个订单
     * @param orderFormDTO
     * @return
     */
    Result<Long> createOrder(@Valid OrderFormDTO orderFormDTO);

    Result<Void> changeStatus(Long orderId, Integer status);
}
