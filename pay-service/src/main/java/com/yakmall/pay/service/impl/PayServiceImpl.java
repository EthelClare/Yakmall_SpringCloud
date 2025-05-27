package com.yakmall.pay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.common.exception.BusinessException;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.pay.domain.dto.PayApplyDTO;
import com.yakmall.pay.domain.po.PayOrder;
import com.yakmall.pay.mapper.PayMapper;
import com.yakmall.pay.service.IPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class PayServiceImpl extends ServiceImpl<PayMapper, PayOrder> implements IPayService {

    private final PayMapper payMapper;
    private final HttpServletRequest request;


    @Override
    public Result<String> applyPayOrder(PayApplyDTO payApplyDTO) {
        //幂等性校验和支付单的处理
        PayOrder payOrder =  handleIdempotentCheck(payApplyDTO);

        //返回订单号[支付单号]可修改
    return Result.success(payOrder.getPayOrderNo().toString());
    }

    private PayOrder handleIdempotentCheck(PayApplyDTO payApplyDTO) {
        //1.首先需要查询历史订单
        PayOrder existingOrder = queryByBizOrderNo(payApplyDTO.getBizOrderNo());
        //没有旧订单，那么就返回创建一个新订单
        if(existingOrder == null) {
            return createNewPayOrder(payApplyDTO);
        }
        //3.有历史订单，那么对历史订单的状态进行校验
        validateExistingOrder(existingOrder, payApplyDTO.getPayChannelCode());
        //4.返回可用的支付单
        return existingOrder;
    }

    private void validateExistingOrder(PayOrder existingOrder, @NotNull(message = "支付渠道编码不能为空") String newPayChannelCode) {
        //1.已支付成功 --> 直接阻断并返回
        // TODO 这里的状态后期修改成枚举变量
        if(existingOrder.getStatus() == 3){
            throw new BusinessException(existingOrder.getBizOrderNo() +"订单已经支付了");
        }
        //2.订单支付超时或者已经取消
        if(existingOrder.getStatus() == 2){
            throw new BusinessException(existingOrder.getBizOrderNo() +"订单已经支付取消或超时");

        }
        //3.支付渠道变更， 重置支付单消息。
        if(StringUtils.equals(existingOrder.getPayChannelCode(), newPayChannelCode)){
            resetPayOrderForNewChannel(existingOrder, newPayChannelCode);
        }

    }

    private void resetPayOrderForNewChannel(PayOrder existingOrder, String newPayChannelCode) {
        lambdaUpdate()
                .set(PayOrder::getPayChannelCode, newPayChannelCode)
                .set(PayOrder::getQrCodeUrl, "")
                .set(PayOrder::getStatus, 1) //TODO 这里的状态待修改
                .set(PayOrder::getId, existingOrder.getId())
                .update();
    }

    /**
     * 创建支付订单
     * @param payApplyDTO
     * @return
     */
    private PayOrder createNewPayOrder(PayApplyDTO payApplyDTO) {
        PayOrder newOrder = buildPayOrder(payApplyDTO);
        save(newOrder);
        return newOrder;
    }

    /**
     * 构建支付实体
     * @param payApplyDTO
     * @return
     */
    private PayOrder buildPayOrder(PayApplyDTO payApplyDTO) {
        PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
        //补充剩余系统字段
        payOrder.setCreateTime(LocalDateTime.now());
        payOrder.setUpdateTime(LocalDateTime.now());
//        Long userId = UserContext.getUser();
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        payOrder.setCreater(userId);
        payOrder.setUpdater(userId);
        payOrder.setBizUserId(userId);
        payOrder.setStatus(1);
        return payOrder;
    }

    /**
     * 根据 orderid 来查询支付订单
     * @param bizOrderNo
     * @return
     */
    private PayOrder queryByBizOrderNo(@NotNull(message = "业务订单id不能为空") Long bizOrderNo) {
        return lambdaQuery().eq(PayOrder::getBizOrderNo, bizOrderNo).one();

    }

}
