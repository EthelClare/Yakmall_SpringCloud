package com.yakmall.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakmall.common.exception.BusinessException;
import com.yakmall.common.result.Result;
import com.yakmall.common.utils.BeanUtils;
import com.yakmall.common.utils.UserContext;
import com.yakmall.user.domain.dto.AddressDTO;
import com.yakmall.user.domain.po.Address;
import com.yakmall.user.mapper.AddressMapper;
import com.yakmall.user.service.IAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
    @Override
    @Transactional
    public Result<Void> saveAddress(AddressDTO addressDTO) {
        //首先要进行校验
        validateAddressDTO(addressDTO);
        Long userId = UserContext.getUser();
        if(userId == null){
            log.info("获取当前用户失败");
            return null;
        }

        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        address.setUserId(userId);
        address.setId(null); //避免绕过前端向后端直接传送id值

        //处理默认地址逻辑
        if(address.getIsDefault() == 1){
            //那么要将另一个为默认地址替换掉
            //减少查询次数，那么直接将这个设置成将其他的所有的默认状态全部清除掉
            lambdaUpdate()
                    .eq(Address::getId, userId)
                    .set(Address::getIsDefault, 0)
                    .update();
        }
        //TODO 这里最后待处理
        boolean save = save(address);
        if(save){
            log.info("{}保存地址成功", userId);
        }else {
            log.info("{}保存地址失败，请重试", userId);

        }
        return null;

    }

    @Override
    public Result<Void> updateAddress(AddressDTO addressDTO) {
        //首先要进行校验
        validateAddressDTO(addressDTO);
        Address address = BeanUtils.copyBean(addressDTO, Address.class);
        boolean b = updateById(address);
        if(b){
            return Result.success().msg("更新成功");
        }
        return Result.error().msg("更新失败");

    }

    /**
     * 参数校验逻辑
     */
    private void validateAddressDTO(AddressDTO dto) {
        if (StringUtils.isBlank(dto.getProvince())) {
            throw new BusinessException("省份不能为空");
        }
        if (StringUtils.isBlank(dto.getCity())) {
            throw new BusinessException("城市不能为空");
        }
        if (StringUtils.isBlank(dto.getStreet())) {
            throw new BusinessException("详细地址不能为空");
        }
        if (StringUtils.isBlank(dto.getContact())) {
            throw new BusinessException("联系人不能为空");
        }
        if (StringUtils.isBlank(dto.getMobile())) {
            throw new BusinessException("手机号不能为空");
        }
        if (!dto.getMobile().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        if (dto.getIsDefault() != null && dto.getIsDefault() != 0 && dto.getIsDefault() != 1) {
            throw new BusinessException("默认地址标识不合法");
        }

    }

}


