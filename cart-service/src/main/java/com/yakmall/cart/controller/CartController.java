package com.yakmall.cart.controller;


import com.yakmall.cart.domain.dto.CartFormDTO;
import com.yakmall.cart.domain.dto.CartUpdateDTO;
import com.yakmall.cart.domain.vo.CartVO;
import com.yakmall.cart.service.ICarService;
import com.yakmall.common.result.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cars")
@Schema(description = "购物车相关接口")
@RestController
public class CartController {
    private final ICarService cartService;

    @Schema(description = "添加商品到购物车")
    @PostMapping
    public Result<Void> addItemToCart(@RequestBody @Valid CartFormDTO cartFormDTO) {
        return Result.success(cartService.addItemToCart(cartFormDTO));
    }


    @Schema(description = "删除购物车中的商品")
    @DeleteMapping("{id}")
    public Result<Void> removeItemFromCart(@PathVariable("id") Long id) {
        cartService.removeById(id);
        return Result.success().msg("删除成功" + id);
    }

    @Schema(description = "更新购物车数据")
    @PutMapping
    public Result<CartUpdateDTO> updateCart(@Valid @RequestBody CartUpdateDTO cartUpdateDTO) {
       return cartService.updateCart(cartUpdateDTO);
    }


    @Schema(description = "查询购物车列表")
    @GetMapping
    public Result<List<CartVO>> queryMyCars() {
        return cartService.queryMyCarts();
    }


    @Schema(description = "清空购物车")
    @DeleteMapping
    public Result<Void> cleanUserCartItems(@RequestParam @NotNull(message = "用户ID不能为空") Long userId,
                                           @RequestParam @NotNull(message = "商品ID集合不能为空") Set<Long> itemIds) {
        return cartService.cleanCartItems(userId, itemIds);
    }
}
