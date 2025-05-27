package com.yakmall.item.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.item.domain.dto.ItemCreateDTO;
import com.yakmall.item.domain.dto.ItemQueryDTO;
import com.yakmall.item.domain.dto.ItemUpdateDTO;
import com.yakmall.item.domain.dto.PageDTO;
import com.yakmall.item.domain.po.Item;
import com.yakmall.item.domain.query.PageQuery;
import com.yakmall.item.service.IItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "商品管理", description = "商品相关接口")
@RequestMapping("/items")
@RestController
@RequiredArgsConstructor
public class ItemController {
    private final IItemService itemService;
    private final HttpServletRequest request;


    @Operation(
            summary = "创建商品",
            description = "需要商品管理员权限<br/>" +
                    "商品名称需符合命名规范"
    )
    @PostMapping("/create")
    public Result<Void> createItem(
            @RequestBody @Valid ItemCreateDTO itemDTO) {
        return itemService.createItem(itemDTO);
    }


    @Operation(
            summary = "删除商品",
            description = "根据商品ID逻辑删除商品<br/>" +
                    "需要商品管理员权限"
    )
    @DeleteMapping("{id}")
    public Result<Void> removeById(@PathVariable String id) {
        try {
            log.info("[删除操作] 操作人： {}, 删除ID ：{}", Long.parseLong(request.getHeader("X-User-Id")), id);
            itemService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            log.warn("尝试删除不存在的ID， {}" ,id);
            return Result.error(404, "数据不存在");
        }
    }

    @Operation(
            summary = "更新商品",
            description = "支持更新商品基本信息<br/>" +
                    "需要商品管理员权限"
    )
    @PutMapping
    public Result<Void> updateItem(@RequestBody @Valid ItemUpdateDTO itemUpdateDTO) {
        return itemService.updateByID(itemUpdateDTO);
    }


    @Operation(
            summary = "批量查询商品",
            description = "根据商品ID列表批量查询商品信息"
    )
    @GetMapping
    public Result<List<ItemQueryDTO>> getItems(@RequestParam("ids") List<Long> ids) {
        return itemService.queryItemByIds(ids);
    }

    @Operation(
            summary = "查询单个商品",
            description = "根据商品ID获取商品详细信息"
    )
    @GetMapping("{id}")
    public Result<ItemQueryDTO> getItemById(@PathVariable Long id) {
        return Result.success(BeanUtils.copyBean(itemService.getById(id), ItemQueryDTO.class) );
    }

    /**
     * 根据id【分页查询商品】
     *
     * @param query
     * @return
     */
    @Operation(
            summary = "分页查询商品",
            description = "支持按创建时间倒序排序<br/>" +
                    "默认每页20条数据"
    )
    @GetMapping("/page")
    public Result<PageDTO<ItemQueryDTO>> getItemByIds(PageQuery query) {
        // 在方法开始处添加
        if (query.getPageNo() < 1 || query.getPageSize() < 1 || query.getPageSize()  > 1000) {
            throw new IllegalArgumentException("分页参数不合法");
        }
        //动态的获取分页参数构造方式--> 从PageQuery中去获取！！
        Page<Item> mpPage = query.toMpPage();
        Page<Item> result = itemService.lambdaQuery()
                .select(Item::getId, Item::getName, Item::getStatus, Item::getCommentCount)
                .eq(Item::getStatus, 1)
                .page(mpPage); // 这里返回Page<Item>

        return Result.success(PageDTO.of(result, ItemQueryDTO.class)) ;
    }



    @Operation(
            summary = "扣减商品库存"
    )
    @PostMapping("/stock/deduct")
    public Result<Void> deductItemStock(@RequestBody Map<Long, Integer> itemNumMap){
        return itemService.deductStock(itemNumMap);
    }

}
