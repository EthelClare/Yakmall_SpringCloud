package com.yakmall.pay.controller;


import com.yakmall.common.result.Result;
import com.yakmall.pay.domain.dto.PayApplyDTO;
import com.yakmall.pay.service.IPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Tag(name = "支付管理", description = "支付相关接口")

@RequestMapping("/pay-orders")
@RestController
@RequiredArgsConstructor
public class PayController {
    private final IPayService payService;



    @Operation( summary = "生成支付单")
    @PostMapping
    public Result<String> PayOrder(@Valid @RequestBody PayApplyDTO payApplyDTO) {
        //TODO 技术限制 ，这里暂时默认采取使用余额支付
        payApplyDTO.setPayType(5);
        return payService.applyPayOrder(payApplyDTO);

    }

}
