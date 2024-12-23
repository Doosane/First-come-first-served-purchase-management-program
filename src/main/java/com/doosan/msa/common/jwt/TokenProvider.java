package com.doosan.msa.common.jwt;

import com.doosan.msa.common.exception.TokenInvalidException;
import com.doosan.msa.common.util.AESUtil;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.entity.RefreshToken;
import com.doosan.msa.user.dto.requestDTO.TokenDTO;
import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import com.doosan.msa.user.repository.RefreshTokenRepository;
import com.doosan.msa.common.shared.Authority;
import com.doosan.msa.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth"; // 권한 키
    private static final String BEARER_PREFIX = "Bearer "; // 토큰 타입 (Bearer)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 액세스 토큰 유효 시간 (30분)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 리프레시 토큰 유효 시간 (7일)

    private final Key key; // 암호화에 사용할 키
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소
    private final UserRepository userRepository; // 사용자 저장소

    // TokenProvider 초기화
    public TokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("TokenProvider가 초기화되었습니다.");
    }

    /**
     * JWT 토큰 생성
     */
    public TokenDTO generateTokenDTO(User user) {
        log.info("JWT 토큰 생성 시작: 사용자 이메일 - {}, 권한 - {}", user.getEmail(), user.getAuthority());

        long now = (new Date().getTime());

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(AESUtil.decrypt(user.getEmail())) // 복호화된 이메일 사용
                .claim(AUTHORITIES_KEY, user.getAuthority().name()) // 사용자 권한 설정
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

        return TokenDTO.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    /***
     * 수정된 TokenProvider 클래스에 getUserIdFromToken 추가
     * @param token
     * @return userId
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 토큰의 subject에서 사용자 ID 추출
            String userId = claims.getSubject();
            log.info("토큰에서 추출된 사용자 ID: {}", userId);
            return userId;
        } catch (JwtException e) {
            log.error("토큰 파싱 중 오류 발생: {}", e.getMessage());
            throw new TokenInvalidException("유효하지 않은 토큰입니다.");
        }
    }

    /**
     * 인증 객체로부터 사용자 정보 가져오기
     */
    public User getUserFromAuthentication(String refreshToken) {
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

        return refreshTokenRepository.findByUser(user).orElse(null);
    }

    /**
     * 권한 검증 메서드
     */
    public void validateUserRole(String token, Authority requiredAuthority) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        String userRole = claims.get(AUTHORITIES_KEY, String.class);

        if (!userRole.equals(requiredAuthority.name())) {
            throw new TokenInvalidException("권한이 부족합니다. 요구된 권한: " + requiredAuthority.name());
        }

        log.info("권한 검증 성공: {}", userRole);
    }
}
