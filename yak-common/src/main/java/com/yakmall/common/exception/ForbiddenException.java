package com.yakmall.common.exception;

/**
 * 这个异常类是处理的用户的状态是被冻结状态
 *
 */
public class ForbiddenException extends CommonException{

    private static final String CODE = "forbidden";
    private static final String MODULE = "AUTH";

    public ForbiddenException( String message) {
        super(CODE, MODULE, message);
    }

}
