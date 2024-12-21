package com.doosan.msa.common.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    @Value("${spring.mail.host}")  // 메일 서버의 호스트 이름
    private String mailHost;

    @Value("${spring.mail.port}") // 메일 서버의 포트 번호
    private int mailPort;

    @Value("${spring.mail.username}")  // 메일 서버 인증에 사용할 사용자 이름
    private String mailUsername;

    @Value("${spring.mail.password}") // 메일 서버 인증에 사용할 비밀번호
    private String mailPassword;

    @Value("${spring.mail.protocol}") // 메일 서버에 사용할 프로토콜
    private String mailProtocol;

    /**
     * JavaMailSender 빈 등록
     * SMTP 서버와의 통신 설정을 구성
     */
    @Bean
    public JavaMailSender mailSender() {
            logger.info("이메일 설정 시작");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            logger.debug("JavaMailSenderImpl 객체 생성 완료");

        // SMTP 서버 정보 설정
        mailSender.setHost(mailHost);
            logger.debug("SMTP 서버 호스트 설정 완료: {}", mailHost);

        mailSender.setPort(mailPort);
            logger.debug("SMTP 서버 포트 설정 완료: {}", mailPort);

        mailSender.setUsername(mailUsername);
            logger.debug("SMTP 서버 사용자 이름 설정 완료: {}", mailUsername);

        mailSender.setPassword(mailPassword);
            logger.debug("SMTP 서버 비밀번호 설정 완료");

        // JavaMail 속성 설정
        Properties javaMailProperties = new Properties(); // JavaMail의 속성 정의
            logger.info("JavaMail 속성 설정 시작");

        javaMailProperties.put("mail.transport.protocol", "smtp"); // SMTP 프로토콜 사용
            logger.debug("JavaMail 속성 설정 - mail.transport.protocol: smtp");

        javaMailProperties.put("mail.smtp.auth", "true"); // SMTP 서버 인증 활성화
            logger.debug("JavaMail 속성 설정 - mail.smtp.auth: true");

        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL 소켓 사용
            logger.debug("JavaMail 속성 설정 - mail.smtp.socketFactory.class: javax.net.ssl.SSLSocketFactory");

        javaMailProperties.put("mail.smtp.starttls.enable", "true"); // STARTTLS 활성화
            logger.debug("JavaMail 속성 설정 - mail.smtp.starttls.enable: true");

        javaMailProperties.put("mail.debug", "true"); // 디버그 모드 활성화
            logger.debug("JavaMail 속성 설정 - mail.debug: true");

        javaMailProperties.put("mail.smtp.ssl.trust", "smtp.naver.com"); // 특정 SMTP 서버 SSL 인증서 신뢰
            logger.debug("JavaMail 속성 설정 - mail.smtp.ssl.trust: smtp.naver.com");

        javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // TLS 버전 설정
            logger.debug("JavaMail 속성 설정 - mail.smtp.ssl.protocols: TLSv1.2");

        javaMailProperties.put("mail.smtp.ssl.enable", "true"); // SSL 활성화
            logger.debug("JavaMail 속성 설정 - mail.smtp.ssl.enable: true");

        mailSender.setJavaMailProperties(javaMailProperties);
            logger.info("JavaMailSender 설정 완료");

        return mailSender; // JavaMailSender 빈 반환
    }
}