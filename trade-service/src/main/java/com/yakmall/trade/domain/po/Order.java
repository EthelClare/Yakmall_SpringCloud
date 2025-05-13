package com.yakmall.trade.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 一个订单多种商品的时候，就多张order_detail表对应就好了
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders")
@Builder
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 总金额，单位为分
     */
    private Integer totalAmount;

    /**
     * 支付类型 1. 支付宝 2.微信 3.系统余额
     */
    private Integer paymentType;

    /**
     * 对应的用户的id
     */
    private Long userId;

    /**
     * 订单状态
     * 1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime consignTime;

    /**
     * 交易完成时间
     */
    private LocalDateTime endTime;

    /**
     * 交易关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 评价时间
     */
    private LocalDateTime commentTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;



}
