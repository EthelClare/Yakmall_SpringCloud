package com.yakmall.item.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("item")
@Schema(description = "商品实体")
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;


    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;


    /**
     * 价格， 单位 分
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "1", message = "价格不能小于 1 分")
    private Integer price;

    /**
     * 库存数量
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品类别
     * TODO 这里需要使用枚举类，将后面商品进行分类
     */
    private String category;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 规格 【比如颜色，大小，等等】
     */
    private String spec;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 商品状态 1-正常 2-下架 3-删除
     */
    //TODO这里要修改成一个枚举类
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 修改人
     */
    private Long updateUser;

}
