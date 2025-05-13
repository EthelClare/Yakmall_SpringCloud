package com.yakmall.user.controller;


import com.yakmall.common.exception.ForbiddenException;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.common.utils.UserContext;
import com.yakmall.user.domain.dto.AddressDTO;
import com.yakmall.user.domain.po.Address;
import com.yakmall.user.service.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "收货地址", description = "收货地址管理接口")
@Slf4j
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final IAddressService addressService;



    @Operation(summary = "根据id查询地址")
    @GetMapping("{addressId}")
    public Result<AddressDTO> getAddressById(@Parameter(description = "地址id") @PathVariable("addressId")Long id) throws NotFoundException {
        //1.根据id进行查询
        Address address = addressService.getById(id);
        if(address == null){
            throw new NotFoundException("地址不存在");
        }
        Long userId = UserContext.getUser();
        if( !address.getUserId().equals(userId) ){
            throw new ForbiddenException("无权访问改地址");
        }
        return Result.success(BeanUtils.copyBean(address, AddressDTO.class));

    }


    @Operation(summary = "查询当前用户地址列表")
    @GetMapping
    public Result<List<AddressDTO>>  findMyAddresses() {
        Long userId = UserContext.getUser();
        List<Address> list = addressService.query().eq("user_id", userId).list();
        return Result.success(BeanUtils.copyList(list, AddressDTO.class)) ;
    }

    @Operation(summary = "增加新的地址")
    @PostMapping
    public Result<Void> saveAddress(@Valid @RequestBody AddressDTO addressDTO) {
         return addressService.saveAddress(addressDTO);
    }


    @Operation(summary = "更新购物车数据")
    @PutMapping
    public Result<Void> updateAddress(@Valid @RequestBody AddressDTO addressDTO) {
        return addressService.updateAddress(addressDTO);
    }


    @Operation(summary = "删除购物车数据")
    @DeleteMapping
    public Result<Void> deleteAddress(Long id) {
        boolean b = addressService.removeById(id);
        return b ? Result.success().msg("删除成功") : Result.error().msg("删除失败");
    }

}
