package com.yakmall.common.exception;

public class UserBadRequestException extends BadRequestException {

    private static final String CODE = "bad_request";
    private static final String MODULE = "USER";

    // 子类构造器直接传递自定义的 CODE 和 MODULE
    public UserBadRequestException(String message) {
        super(CODE, MODULE, message); // 调用父类三参数构造器
    }
}
