package com.yakmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakmall.common.result.Result;
import com.yakmall.user.domain.dto.AddressDTO;
import com.yakmall.user.domain.po.Address;

import javax.validation.Valid;


public interface IAddressService extends IService<Address> {

    Result<Void> saveAddress(@Valid AddressDTO addressDTO);

    Result<Void> updateAddress(@Valid AddressDTO addressDTO);
}
