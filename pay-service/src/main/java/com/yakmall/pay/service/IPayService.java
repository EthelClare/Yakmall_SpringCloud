package com.yakmall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakmall.common.result.Result;
import com.yakmall.pay.domain.dto.PayApplyDTO;
import com.yakmall.pay.domain.po.PayOrder;

import javax.validation.Valid;

public interface IPayService extends IService<PayOrder> {
    Result<String> applyPayOrder(@Valid PayApplyDTO payApplyDTO);
}
