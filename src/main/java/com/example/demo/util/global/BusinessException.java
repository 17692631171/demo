package com.example.demo.util.global;

import com.example.demo.util.response.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public BusinessException(ResponseCode responseCode, String message, Throwable cause) {
        super(message, cause);
        this.code = responseCode.getCode();
    }
}