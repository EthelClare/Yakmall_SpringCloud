package com.yakmall.common.exception;

public class UnauthorizedException extends CommonException {

    private static final String CODE = "Unauthorized";
    private static final String MODULE = "JWT";

    public UnauthorizedException( String message) {
        super(CODE, MODULE, message);
    }

}
