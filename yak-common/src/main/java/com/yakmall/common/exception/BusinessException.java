package com.yakmall.common.exception;

public class BusinessException extends CommonException {
    public static final String CODE = "BIZ_ERROR"; // 建议使用更简短的错误码
    private static final String DEFAULT_MODULE = "BUSINESS"; // 可选：定义默认模块

    // 基础构造函数，强制指定模块和消息
    public BusinessException(String module, String message) {
        super(CODE, module, message);
    }

    // 提供默认模块的简化构造函数
    public BusinessException(String message) {
        super(CODE, DEFAULT_MODULE, message);
    }

    // 支持异常链传递
    public BusinessException(String module, String message, Throwable cause) {
        super(CODE, module, message);
        if (cause != null) initCause(cause);
    }
}