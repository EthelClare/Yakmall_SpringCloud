package com.yakmall.trade.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Schema(description = "商品细节表单实体")
public class OrderDetailDTO {
    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须是正数")
    @Schema(description = "商品id")
    private Long itemId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "至少购买1件")
    @Max(value = 99, message = "单件商品最多购买99件")
    @Schema(description = "商品购买数量")
    private Integer num;
}
