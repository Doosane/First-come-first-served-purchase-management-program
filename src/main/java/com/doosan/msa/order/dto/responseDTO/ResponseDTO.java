package com.doosan.msa.order.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Slf4j
public class ResponseDTO<T> {

    private boolean success; // 요청 성공 여부
    private T data; // 성공 시 반환할 데이터
    private Meta meta; // 성공 시 반환할 메타 정보
    private Error error; // 실패 시 반환할 에러 정보

    public ResponseDTO() {

    }

    /**
     * 성공 응답 생성 메서드
     *
     * @param statusCode HTTP 상태 코드
     * @param code 성공 코드
     * @param message 성공 메시지
     * @param data 클라이언트에 전달할 성공 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ResponseDTO<T> success(int statusCode, String code, String message, T data) {
        log.info("성공 응답 생성 - 상태 코드: {}, 코드: {}, 메시지: {}, 데이터: {}", statusCode, code, message, data);
        return new ResponseDTO<>(true, data, new Meta(statusCode, code, message, LocalDateTime.now()), null);
    }

    /**
     * 실패 응답 생성 메서드
     *
     * @param statusCode HTTP 상태 코드
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param <T> 데이터 타입 (null 반환)
     * @return 실패 응답 객체
     */
    public static <T> ResponseDTO<T> fail(int statusCode, String code, String message) {
        log.warn("실패 응답 생성 - 상태 코드: {}, 코드: {}, 메시지: {}", statusCode, code, message);
        return new ResponseDTO<>(false, null, null, new Error(statusCode, code, message, LocalDateTime.now()));
    }

    public void setSuccess(boolean b) {
    }

    public void setMessage(Object o) {
    }

    public void setData(T expectedResponse) {
    }

    /**
     * 성공 응답의 메타 정보 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class Meta {
        private int statusCode; // HTTP 상태 코드
        private String code; // 성공 코드
        private String message; // 성공 메시지
        private LocalDateTime timestamp; // 응답 생성 시각
    }

    /**
     * 실패 응답의 에러 정보 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class Error {
        private int statusCode; // HTTP 상태 코드
        private String code; // 에러 코드
        private String message; // 에러 메시지
        private LocalDateTime timestamp; // 에러 발생 시각
    }
}