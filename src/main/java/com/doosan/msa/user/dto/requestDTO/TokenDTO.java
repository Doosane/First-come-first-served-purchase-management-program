package com.doosan.msa.user.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * TokenDTO 클래스
 * - 클라이언트와 서버 간 인증 및 권한 관리를 위한 토큰 데이터를 담는 DTO
 * - grantType, accessToken, refreshToken, accessToken 만료 시간을 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j // 로깅을 위한 어노테이션 추가
public class TokenDTO {
    private String grantType; // 인증 유형 (Bearer 등)
    private String accessToken; // Access Token
    private String refreshToken; // Refresh Token
    private Long accessTokenExpiresIn; // Access Token 만료 시간

    /**
     * 토큰 정보를 HTTP 응답 헤더에 추가하는 메서드
     * @param response HTTP 응답 객체
     */
    public void tokenToHeaders(HttpServletResponse response) {
        log.info("토큰 정보를 HTTP 헤더에 추가");

        response.addHeader("Authorization", "Bearer " + getAccessToken()); // Access Token 추가
        log.debug("Authorization 헤더에 Access Token 추가: {}", getAccessToken());

        response.addHeader("Refresh_Token", getRefreshToken()); // Refresh Token 추가
        log.debug("Refresh_Token 헤더에 Refresh Token 추가: {}", getRefreshToken());

        response.addHeader("Access-Token-Expire-Time", getAccessTokenExpiresIn().toString()); // Access Token 만료 시간 추가
        log.debug("Access-Token-Expire-Time 헤더에 만료 시간 추가: {}", getAccessTokenExpiresIn());

        log.info("HTTP 헤더에 토큰 정보 추가 완료.");
    }

    /**
     * 현재 시간 반환 (생성 시간)
     * @return LocalDateTime 현재 시간
     */
    public LocalDateTime getCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        log.info("토큰 생성 시간 반환: {}", now);
        return now;
    }

    /**
     * 현재 시간 반환 (수정 시간)
     * @return LocalDateTime 현재 시간
     */
    public LocalDateTime getModifiedAt() {
        LocalDateTime now = LocalDateTime.now();
        log.info("토큰 수정 시간 반환: {}", now);
        return now;
    }
}
