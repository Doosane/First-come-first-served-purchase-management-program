package com.doosan.msa.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@Configuration
public class CustomCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {

        // CORS 설정 소스를 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        // 자격 증명 허용 설정
        config.setAllowCredentials(true);

        // 허용할 도메인 추가
        config.addAllowedOrigin("http://localhost:8081");

        config.addAllowedOrigin("http://localhost:6379");

        // 모든 도메인 패턴 허용
        config.addAllowedOriginPattern("*");

        // 모든 헤더 허용
        config.addAllowedHeader("*");

        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");

        // 모든 헤더 노출 허용
        config.addExposedHeader("*");

        // 특정 경로에 대해 CORS 설정 등록
        source.registerCorsConfiguration("/v1/users/**", config);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
