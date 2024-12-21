package com.doosan.msa.refund.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 응답 DTO (Data Transfer Object)
 * API 응답 결과를 클라이언트로 전달하기 위한 클래스
 * @param <T> 클라이언트로 전달할 데이터의 타입
 */
@Getter
@AllArgsConstructor
@Slf4j // 로깅 기능 추가
public class ResponseDTO<T> {

    private boolean success; // 요청 성공 여부
    private T data; // 성공 시 반환할 데이터
    private Error error; // 실패 시 반환할 에러 정보

    /**
     * 성공 응답 생성 메서드
     *
     * @param data 클라이언트에 전달할 성공 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ResponseDTO<T> success(T data) {
        log.info("성공 응답 생성: {}", data);
        return new ResponseDTO<>(true, data, null);
    }

    /**
     * 실패 응답 생성 메서드
     *
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param <T> 데이터 타입 (null 반환)
     * @return 실패 응답 객체
     */
    public static <T> ResponseDTO<T> fail(String code, String message) {
        log.warn("실패 응답 생성 - 코드: {}, 메시지: {}", code, message);
        return new ResponseDTO<>(false, null, new Error(code, message));
    }

    /**
     * 에러 정보 클래스
     * 요청 실패 시 반환할 에러 코드와 메시지를 담고 있음
     */
    @Getter
    @AllArgsConstructor
    static class Error {
        private String code; // 에러 코드
        private String message; // 에러 메시지

        /**
         * 에러 생성 시 로그 기록
         */
        public void logError() {
            log.error("에러 생성 - 코드: {}, 메시지: {}", code, message);
        }
    }
}
