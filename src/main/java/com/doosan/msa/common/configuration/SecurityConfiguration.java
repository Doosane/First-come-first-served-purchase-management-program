package com.doosan.msa.common.configuration;

import com.doosan.msa.common.jwt.AccessDeniedHandlerException;
import com.doosan.msa.common.jwt.AuthenticationEntryPointException;
import com.doosan.msa.common.jwt.TokenProvider;
import com.doosan.msa.user.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    String SECRET_KEY; // JWT 서명에 사용할 비밀 키

    private final TokenProvider tokenProvider; // JWT 생성 및 검증을 담당하는 Provider
    private final UserDetailsServiceImpl userDetailsService; // 사용자 정보를 로드하는 서비스
    private final AuthenticationEntryPointException authenticationEntryPointException; // 인증 실패 처리 핸들러
    private final AccessDeniedHandlerException accessDeniedHandlerException; // 권한 거부 처리 핸들러
    private final CorsFilter corsFilter; // CORS 설정 필터

    /**
     * PasswordEncoder Bean 등록
     * - BCryptPasswordEncoder를 사용하여 비밀번호 암호화
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("PasswordEncoder (BCryptPasswordEncoder) Bean 등록 시작");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        log.debug("PasswordEncoder (BCryptPasswordEncoder) 생성 완료");
        return encoder;
    }

    /**
     * SecurityFilterChain 설정
     * - 인증 및 권한 부여, 세션 관리, CORS, CSRF 등의 보안 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 과정 중 발생할 수 있는 예외
     */
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain 설정 시작");

        // CORS 필터 활성화
        log.debug("CORS 필터 활성화 시작");
        http.cors();
        log.debug("CORS 필터 활성화 완료");

        // CSRF 비활성화 및 예외 처리 핸들러 등록
        log.info("CSRF 비활성화 및 예외 처리 핸들러 등록 시작");
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException);
        log.debug("CSRF 비활성화 및 예외 처리 핸들러 등록 완료");

        // 세션 관리 정책을 STATELESS로 설정 (JWT 기반 인증)
        log.info("세션 관리 정책 설정 시작 (STATELESS)");
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        log.debug("세션 관리 정책 설정 완료 (STATELESS)");

        // URL별 접근 권한 설정
        log.info("URL별 접근 권한 설정 시작");
        http.authorizeRequests()
                .antMatchers("/v1/users/**").permitAll()
                .antMatchers("/v1/api/**").permitAll()
                .antMatchers("/v1/api/orders/**").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/v1/auth/**").permitAll()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 경로 허용
                .antMatchers("/v1/mailauthCheck/**").permitAll()
                .antMatchers("/v1/auth/mailSend").permitAll()
                .antMatchers("/v1/auth/email/verify").permitAll()
                .antMatchers("/api/auth").permitAll()
                .antMatchers("/sub/**").permitAll()
                .antMatchers("/pub/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**").permitAll()
                .anyRequest().authenticated();
        log.debug("URL별 접근 권한 설정 완료");

        // CORS 필터 추가
        log.info("CORS 필터 추가 시작");
        http.addFilter(corsFilter);
        log.debug("CORS 필터 추가 완료");

        // JwtSecurityConfiguration 적용
        log.info("JwtSecurityConfiguration 설정 적용 시작");
        http.apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));
        log.debug("JwtSecurityConfiguration 설정 적용 완료");

        // 설정 완료 후 SecurityFilterChain 반환
        log.info("SecurityFilterChain 설정 완료");
        return http.build();
    }
}