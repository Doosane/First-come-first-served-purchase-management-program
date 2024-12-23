package com.doosan.msa.common.exception;

import lombok.Getter;

// 예외처리
@Getter
public class BusinessLogicException extends RuntimeException {
    private final int statusCode; // HTTP 상태코드
    private final String code;    // 커스텀 에러 코드
    private final String message; // 에러 메시지

    public BusinessLogicException(int statusCode, String code, String message) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }
}

