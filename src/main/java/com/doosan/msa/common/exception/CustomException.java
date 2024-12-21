package com.doosan.msa.common.exception;

public class CustomException extends RuntimeException {

    private final String errorCode; // 에러 코드

    public CustomException(String message) {
        super(message);
        this.errorCode = "CUSTOM_EXCEPTION"; // 기본 코드 설정
    }

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
