package com.yakmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakmall.common.result.Result;
import com.yakmall.item.domain.dto.ItemCreateDTO;
import com.yakmall.item.domain.dto.ItemQueryDTO;
import com.yakmall.item.domain.dto.ItemUpdateDTO;
import com.yakmall.item.domain.po.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IItemService extends IService<Item> {
    Result<Void> createItem (ItemCreateDTO itemDTO);

    /**
     * 这是通过自己写的来完成对商品的修改
     * @return
     */
    Result<Void> updateByID(ItemUpdateDTO itemUpdateDTO);

    /**
     * 批量查询 id
     * @param ids
     * @return
     */
    Result<List<ItemQueryDTO>> queryItemByIds(Collection<Long> ids);


    Result<Void> deductStock(Map<Long, Integer> itemNumMap);
}
