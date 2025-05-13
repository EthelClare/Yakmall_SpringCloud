package com.yakmall.common.exception;

public class BadRequestException extends CommonException {

    private final String code;
    private final String module;

//    public BadRequestException(String message) {
//        super(CODE, MODULE, message);
//    }

    // 父类构造器接受 code、module、message
    public BadRequestException(String code, String module, String message) {
        super(code, module, message);
        this.code = code;
        this.module = module;
    }

    public BadRequestException(String message) {
        this("bad_request", "GENERAL", message); // 默认值
    }

}
