package com.doosan.msa.common.jwt;

import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 접근 거부 처리 클래스
 * - 사용자가 인증은 되었으나 권한이 부족하여 요청을 처리할 수 없는 경우 호출됩니다.
 */
@Slf4j
@Component
public class AccessDeniedHandlerException implements AccessDeniedHandler {

    /**
     * 접근 거부 시 실행되는 메서드
     * @param request  클라이언트의 HTTP 요청
     * @param response 서버의 HTTP 응답
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException 예외 처리 중 발생할 수 있는 입출력 오류
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 로깅: 접근 거부 상황을 로그로 기록
        log.info("AccessDeniedHandlerException 시작 - 접근 거부 감지");
        log.debug("요청 URI: {}", request.getRequestURI());
        log.debug("예외 메시지: {}", accessDeniedException.getMessage());

        // 응답 Content-Type 설정: JSON 형식으로 응답
        response.setContentType("application/json;charset=UTF-8");

        // 응답에 JSON 형태로 실패 메시지 작성
        response.getWriter().println(
                new ObjectMapper().writeValueAsString(
                        ResponseDTO.fail(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", "로그인이 필요합니다.")

                )
        );

        // 응답 상태 코드 설정: 403 (Forbidden)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        log.info("AccessDeniedHandlerException 종료 - 응답 상태 코드 403 반환");
    }
}
