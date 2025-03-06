package com.example.community.exception;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final ErrorCode errorCode;

    public APIException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
