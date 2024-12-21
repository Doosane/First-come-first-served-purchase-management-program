package com.doosan.msa.common.jwt;

import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증 실패 시 처리하는 EntryPoint 클래스
 * - 사용자가 인증되지 않은 상태에서 보호된 리소스에 접근하려고 할 때 호출
 */
@Slf4j
@Component
public class AuthenticationEntryPointException implements AuthenticationEntryPoint {

    /**
     * 인증되지 않은 사용자가 보호된 리소스에 접근 시 실행되는 메서드
     * @param request  클라이언트의 HTTP 요청
     * @param response 서버의 HTTP 응답
     * @param authException 인증 실패 예외
     * @throws IOException 예외 처리 중 발생할 수 있는 입출력 오류
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 인증 실패 상황 로그로 기록
        log.info("AuthenticationEntryPointException 시작 - 인증되지 않은 요청 감지");
        log.debug("요청 URI: {}", request.getRequestURI());
        log.debug("예외 메시지: {}", authException.getMessage());

        // 응답 Content-Type 설정: JSON 형식으로 응답
        response.setContentType("application/json;charset=UTF-8");

        // 응답에 JSON 형태로 실패 메시지 작성
        response.getWriter().println(
                new ObjectMapper().writeValueAsString(
                        ResponseDTO.fail(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", "로그인이 필요합니다.")
                )
        );

        // 응답 상태 코드 설정: 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        log.info("AuthenticationEntryPointException 종료 - 응답 상태 코드 401 반환");
    }
}
