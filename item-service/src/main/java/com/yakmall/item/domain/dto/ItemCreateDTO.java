package com.yakmall.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "创建商品表单实体")
public class ItemCreateDTO {

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 价格， 单位 分
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin("1")
    private Integer price;

    /**
     * 库存数量
     */

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
     * 商品状态 1-正常 2-下架 3-删除
     */
    private Integer status;
}
