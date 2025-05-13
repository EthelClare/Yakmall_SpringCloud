package com.yakmall.cart.domain.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车在数据库中的唯一id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 放用户的id
     */
    private Long userId;

    /**
     * 对应商品的id
     */
    private Long itemId;

    /**
     * 此种商品在购物车中的数量
     */
    private Integer num;

    /**
     * 商品标题
     */
    private String name;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 价格, 价格 分
     */
    private Integer price;
}
