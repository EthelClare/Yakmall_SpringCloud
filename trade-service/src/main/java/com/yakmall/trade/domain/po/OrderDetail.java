package com.yakmall.trade.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_detail")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单详情id【这里的id生成策略建议和order表中保持一致】
     */
    @TableId(value="id", type= IdType.ASSIGN_ID)
    private Long id;

    /**
     * 对应的order 的id
     */
    private Long orderId;

    /**
     * 包含的商品的id
     */
    private Long itemId;

    /**
     * 包含商品的数量
     */
    private Integer num;

    /**
     * 商品标题
     */
    private String name;

    /**
     * 商品动态属性键值集
     */
    private String spec;

    /**
     * 价格,单位：分
     */
    private Integer price;

    /**
     * 商品图片
     */
    private String image;

    public OrderDetail(Long orderId, Long id, Integer integer, String name, String spec, Integer price, String image) {
    }
}
