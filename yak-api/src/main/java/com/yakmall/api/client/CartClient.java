package com.yakmall.api.client;

import com.yakmall.common.result.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Set;


@FeignClient("cart-service")
public interface CartClient {

    /**
     * 删除购物车
     * @param id
     * @return
     */
    @DeleteMapping("/cars/{id}")
    public Result<Void> removeItemFromCart(@PathVariable("id") Long id) ;


    @Schema(description = "清空购物车")
    @DeleteMapping("/cars")
    public Result<Void> cleanUserCartItems(@RequestParam("userId") @NotNull(message = "用户ID不能为空") Long userId,
                                           @RequestBody @NotNull(message = "商品ID集合不能为空") Set<Long> itemIds);
}
