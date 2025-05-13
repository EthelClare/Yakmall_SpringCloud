package com.yakmall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakmall.cart.domain.dto.CartFormDTO;
import com.yakmall.cart.domain.dto.CartUpdateDTO;
import com.yakmall.cart.domain.po.Cart;
import com.yakmall.cart.domain.vo.CartVO;
import com.yakmall.common.result.Result;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

public interface ICarService extends IService<Cart> {

    /**
     * 添加商品到购物车
     * @param cartFormDTO
     * @return
     */
    Void addItemToCart(@Valid CartFormDTO cartFormDTO);

    Result<CartUpdateDTO> updateCart(@Valid CartUpdateDTO cartUpdateDTO);

    Result<List<CartVO>> queryMyCarts();

    Result<Void> cleanCartItems(Long userId, Set<Long> itemIds);
}
