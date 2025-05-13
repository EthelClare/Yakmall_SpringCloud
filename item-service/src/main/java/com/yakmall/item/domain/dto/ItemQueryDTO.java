package com.yakmall.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询商品表单实体")
public class ItemQueryDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "价格，单位分")
    private Integer price;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "商品类别")
    private String category;

    @Schema(description = "品牌名称")
    private String brand;

    @Schema(description = "规格 【比如颜色，大小，等等】")
    private String spec;

    @Schema(description = "销量")
    private Integer sold;

    @Schema(description = "评论数量")
    private Integer commentCount;

    @Schema(description = "商品状态 1-正常 2-下架 3-删除")
    private Integer status;

}
