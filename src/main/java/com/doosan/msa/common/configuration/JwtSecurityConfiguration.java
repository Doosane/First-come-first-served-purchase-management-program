package com.doosan.msa.common.configuration;

import com.doosan.msa.common.jwt.JwtFilter;
import com.doosan.msa.common.jwt.TokenProvider;
import com.doosan.msa.user.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final String SECRET_KEY; // JWT 서명에 사용되는 비밀 키
    private final TokenProvider tokenProvider; // 토큰 생성 및 검증을 담당하는 Provider
    private final UserDetailsServiceImpl userDetailsService; // 사용자 세부 정보를 로드하는 서비스

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        // HttpSecurity 설정 시작 로그
        log.info("HttpSecurity 설정을 JwtSecurityConfiguration으로 구성 중...");
        log.debug("SECRET_KEY 값: {}", SECRET_KEY);
        log.debug("JwtFilter 초기화: SECRET_KEY, TokenProvider, UserDetailsServiceImpl 사용");

        // 커스텀 JWT 필터 생성
        JwtFilter customJwtFilter = new JwtFilter(SECRET_KEY, tokenProvider, userDetailsService);

        // JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        httpSecurity.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("JwtFilter를 보안 필터 체인에 성공적으로 추가");
    }
}
