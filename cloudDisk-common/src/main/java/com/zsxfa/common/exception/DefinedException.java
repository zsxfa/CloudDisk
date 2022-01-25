package com.zsxfa.common.exception;

import com.zsxfa.common.result.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zsxfa
 */
@Data
@NoArgsConstructor
public class DefinedException extends RuntimeException {

    //状态码
    private Integer code;

    //错误消息
    private String message;

    /**
     *
     * @param message 错误消息
     */
    public DefinedException(String message) {
        this.message = message;
    }

    /**
     *
     * @param message 错误消息
     * @param code 错误码
     */
    public DefinedException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     *
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原始异常对象
     */
    public DefinedException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
    }

    /**
     *
     * @param resultCodeEnum 接收枚举类型
     */
    public DefinedException(ResponseEnum resultCodeEnum) {
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }

    /**
     *
     * @param resultCodeEnum 接收枚举类型
     * @param cause 原始异常对象
     */
    public DefinedException(ResponseEnum resultCodeEnum, Throwable cause) {
        super(cause);
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }
}
