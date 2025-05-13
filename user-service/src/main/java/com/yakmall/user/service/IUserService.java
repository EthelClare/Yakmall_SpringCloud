package com.yakmall.user.service;


import com.yakmall.common.result.Result;
import com.yakmall.user.domain.dto.LoginFormDTO;
import com.yakmall.user.domain.dto.UserRegisterDTO;
import com.yakmall.user.domain.vo.UserLoginVO;

public interface IUserService {
    /**
     * 登陆
     * @param loginFormDTO
     * @return
     */
    Result<UserLoginVO> login(LoginFormDTO loginFormDTO);

    Result<Void> register(UserRegisterDTO userRegisterDTO);
}
