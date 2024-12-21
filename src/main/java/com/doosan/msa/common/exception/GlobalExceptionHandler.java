package com.doosan.msa.common.exception;

import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // NullPointerException 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseDTO<?>> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException 발생: {}", e.getMessage(), e);
        ResponseDTO<?> response = ResponseDTO.fail(
                HttpStatus.BAD_REQUEST.value(),
                "NULL_POINTER_EXCEPTION",
                "요청 처리 중 예상치 못한 오류가 발생했습니다."
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDTO<?>> handleCustomException(CustomException e) {
        log.error("CustomException 발생: {}", e.getMessage(), e);
        ResponseDTO<?> response = ResponseDTO.fail(
                HttpStatus.BAD_REQUEST.value(),
                e.getErrorCode() != null ? e.getErrorCode() : "CUSTOM_EXCEPTION",
                e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // IllegalArgumentException 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException 발생: {}", e.getMessage(), e);
        ResponseDTO<?> response = ResponseDTO.fail(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 기타 예외 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<?>> handleException(Exception e) {
        log.error("예외 발생: {}", e.getMessage(), e);
        ResponseDTO<?> response = ResponseDTO.fail(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
