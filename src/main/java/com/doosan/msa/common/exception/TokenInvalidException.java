package com.doosan.msa.common.exception;

/**
 * 잘못된 토큰 처리용 커스텀 예외 클래스
 */
public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException() {
        super("잘못된 토큰입니다.");
    }

    public TokenInvalidException(String message) {
        super(message);
    }
}

