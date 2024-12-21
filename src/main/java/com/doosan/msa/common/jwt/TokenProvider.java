package com.doosan.msa.common.jwt;

import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.entity.RefreshToken;
import com.doosan.msa.user.entity.UserDetailsImpl;
import com.doosan.msa.user.dto.requestDTO.TokenDTO;
import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import com.doosan.msa.user.repository.RefreshTokenRepository;
import com.doosan.msa.common.shared.Authority;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth"; // 권한 키
    private static final String BEARER_PREFIX = "Bearer "; // 토큰 타입 (Bearer)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 액세스 토큰 유효 시간 (30분)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 리프레시 토큰 유효 시간 (7일)

    private final Key key; // 암호화에 사용할 키
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소

    // TokenProvider 초기화
    public TokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("TokenProvider가 초기화되었습니다.");
    }

    /**
     * JWT 토큰 생성
     */
    public TokenDTO generateTokenDTO(User user) {
        log.info("JWT 토큰 생성 시작: 사용자 이메일 - {}", user.getEmail());

        long now = (new Date().getTime());

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(user.getName()) // 토큰에 사용자 이름 설정
                .claim(AUTHORITIES_KEY, Authority.ROLE_USER.name()) // 권한 설정
                .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘과 키 설정
                .compact();

        log.debug("액세스 토큰 생성 완료: {}", accessToken);

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.debug("리프레시 토큰 생성 완료: {}", refreshToken);

        // Refresh Token 저장
        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(user.getId())
                .user(user)
                .value(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenObject);
        log.info("리프레시 토큰이 저장되었습니다: 사용자 이메일 - {}", user.getEmail());

        return TokenDTO.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 인증 객체로부터 사용자 정보 가져오기
     */
    public User getUserFromAuthentication(String refreshToken) {
        // RefreshToken을 통해 User 객체를 찾습니다.
        return refreshTokenRepository.findByValue(refreshToken)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));
    }



    /**
     * JWT 유효성 검증
     */
    public boolean validateToken(String token) {
        log.info("JWT 토큰 유효성 검증 시작: {}", token);

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("토큰이 유효합니다.");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 JWT 서명: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims 문자열이 비어있습니다: {}", e.getMessage());
        }

        log.warn("토큰 유효성 검증 실패.");
        return false;
    }

    /**
     * Refresh Token 조회
     */
    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(User user) {
        log.info("리프레시 토큰 조회 시작: 사용자 이메일 - {}", user.getEmail());

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);
        if (refreshToken == null) {
            log.warn("리프레시 토큰이 존재하지 않습니다: 사용자 이메일 - {}", user.getEmail());
        } else {
            log.debug("리프레시 토큰 조회 성공: 사용자 이메일 - {}, 토큰 값 - {}", user.getEmail(), refreshToken.getValue());
        }

        return refreshToken;
    }

    /**
     * Refresh Token 삭제
     */
    @Transactional
    public ResponseDTO<?> deleteRefreshToken(User user) {
        try {
            // Refresh Token 삭제 로직
            refreshTokenRepository.deleteByUserId(user.getId());
            log.info("Token 삭제 완료 - User ID: {}", user.getId());

            // 성공 응답 반환
            return ResponseDTO.success(
                    HttpStatus.OK.value(),
                    "TOKEN_DELETION_SUCCESS",
                    "Token 삭제 완료",
                    null
            );
        } catch (Exception e) {
            log.error("Token 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseDTO.fail(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "TOKEN_DELETION_ERROR",
                    "Token 삭제 중 오류가 발생했습니다."
            );
        }
    }



}
