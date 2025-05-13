package com.yakmall.api.client;


import com.yakmall.api.dto.ItemQueryDTO;
import com.yakmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("item-service")
public interface ItemClient {

    @GetMapping("/items")
    public Result<List<ItemQueryDTO>> getItems(@RequestParam("ids") List<Long> ids);


    @PostMapping("/items/stock/deduct")
    public Result<Void> deductItemStock(@RequestBody Map<Long, Integer> itemNumMap);

}