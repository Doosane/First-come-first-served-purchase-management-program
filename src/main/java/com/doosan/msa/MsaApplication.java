package com.doosan.msa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MsaApplication 클래스
 * - Spring Boot 애플리케이션의 진입점
 * - JPA Auditing 및 스케줄링 기능 활성화
 */
@EnableJpaAuditing // JPA Auditing 활성화 (엔티티의 생성 및 수정 시간 자동 관리)
@SpringBootApplication // Spring Boot 애플리케이션 시작 지점
@EnableScheduling // 스케줄링 기능 활성화
public class MsaApplication {

    private static final Logger logger = LogManager.getLogger(MsaApplication.class);

    /**
     * 애플리케이션의 메인 메서드
     * - SpringApplication.run()을 호출하여 애플리케이션 실행
     *
     * @param args 프로그램 실행 시 전달받는 인자 배열
     */
    public static void main(String[] args) {
        SpringApplication.run(MsaApplication.class, args);
        logger.info("MSA 애플리케이션이 성공적으로 실행되었습니다.");
    }
}
