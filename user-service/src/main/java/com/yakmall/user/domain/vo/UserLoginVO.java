package com.yakmall.user.domain.vo;

import lombok.Data;

/**
 * 用于返回到前端页面用于展示的数据
 */
@Data
public class UserLoginVO {
    private String username;
    private Long userId;
    private String token;
    //余额
//    private Integer balance;

}
