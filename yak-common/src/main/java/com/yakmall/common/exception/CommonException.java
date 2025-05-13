package com.yakmall.common.exception;

import lombok.Getter;

/**
 * 标准异常基类
 */
@Getter
public class CommonException extends RuntimeException {
    private final String code;
    private final String module;
    //使用String
    //使用module，能够快速定位问题的来源

    public CommonException(String code, String module, String message) {
        super(message);
        this.code = code;
        this.module = module;
    }

    public CommonException(String code, String module, String message, Throwable cause) {
        super(message, cause);  // 调用父类带cause的构造
        this.code = code;
        this.module = module;
    }

    //throw new CommonException("INVALID_STATUS", "ORDER", "订单状态不合法");
}
