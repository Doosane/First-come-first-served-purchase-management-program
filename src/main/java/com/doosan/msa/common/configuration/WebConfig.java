package com.doosan.msa.common.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Web MVC 설정 클래스
 * - 메시지 소스 설정 및 Validator 설정을 포함
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 메시지 소스 설정
     * - 메시지 파일 경로와 인코딩 설정
     * - Validation 메시지와 다국어 처리를 위한 설정
     *
     * @return MessageSource 메시지 소스 객체
     */
    @Bean
    public MessageSource messageSource() {
        log.info("메시지 소스(MessageSource) 설정 시작");

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/message"); // 메시지 파일 경로 설정
        log.debug("메시지 소스 경로: classpath:/message");

        messageSource.setDefaultEncoding("UTF-8"); // 메시지 파일의 기본 인코딩 설정
        log.debug("메시지 소스 기본 인코딩: UTF-8");

        log.info("메시지 소스(MessageSource) 설정 완료");
        return messageSource;
    }

    /**
     * Validator 설정
     * - Validation 메시지를 메시지 소스와 연결하여 커스터마이징
     *
     * @return Validator Spring의 LocalValidatorFactoryBean
     */
    @Override
    public Validator getValidator() {
        log.info("Validator 설정 시작");

        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource()); // 메시지 소스를 Validator에 연결
        log.debug("Validator 메시지 소스 설정 완료");

        log.info("Validator 설정 완료");
        return bean;
    }
}
