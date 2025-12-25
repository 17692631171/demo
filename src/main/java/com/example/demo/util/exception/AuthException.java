package com.example.demo.util.exception;

import com.example.demo.util.response.ResponseCode;

public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(ResponseCode.UNAUTHORIZED, message);
    }

    public AuthException(String message, Object data) {
        super(ResponseCode.UNAUTHORIZED, message, data);
    }
}