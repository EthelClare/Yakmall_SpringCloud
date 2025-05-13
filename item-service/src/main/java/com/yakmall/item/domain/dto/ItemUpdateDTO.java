package com.yakmall.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "更新商品表单实体")
public class ItemUpdateDTO {

    /**
     * 这个需要进行一次查询来获得id然后保存到里面
     */
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 价格, 单位 分
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
     * 规格 【比如颜色，大小，等等】
     */
    private String spec;

}
