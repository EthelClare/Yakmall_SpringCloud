package com.yakmall.trade.controller;


import com.yakmall.common.result.Result;
import com.yakmall.trade.domain.dto.OrderFormDTO;
import com.yakmall.trade.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Tag(name = "订单", description = "订单相关接口")
@RestController
@RequestMapping("/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    /**
     * 创建一个订单
     * @return order的id
     */
    @Operation(summary = "创建一个订单")
    @PostMapping
    public Result<Long> createOrder(@RequestBody @Valid OrderFormDTO orderFormDTO) {
        return orderService.createOrder(orderFormDTO);
    }

    /**
     * 改变订单状态
     *
     * @param orderId
     * @param status
     * @return
     */
    @Operation(summary = "改变订单状态")
    @PutMapping("/{orderId}")
    public Result<Void> changeOrderStatus(@PathVariable Long orderId,@RequestBody Integer status) {
        //这里使用了 @PathVariable 加上@RequestBody的方式，更易于扩展参数

        return orderService.changeStatus(orderId, status);
    }

}
