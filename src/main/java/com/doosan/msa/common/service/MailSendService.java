package com.doosan.msa.common.service;

import com.doosan.msa.common.exception.CustomException;
import com.doosan.msa.common.util.RedisUtil;
import com.doosan.msa.user.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.net.InetAddress;
@Service
public class MailSendService {


    private static final Logger logger = LoggerFactory.getLogger(MailSendService.class);

    // 허용된 도메인 화이트리스트
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList("gmail.com", "naver.com", "daum.net","outlook.com","yahoo.com", "hotmail.com");

    @Value("${mail.sender.email}") // application.properties에서 발신자 이메일 가져오기
    private String setFrom;

    public boolean isDomainWhitelisted(String email) {
        // 이메일 주소에서 도메인 추출
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(); // 소문자 처리
        boolean isWhitelisted = ALLOWED_DOMAINS.contains(domain);
        if (!isWhitelisted) {
            logger.warn("도메인이 화이트리스트에 포함되지 않음: {}", domain);
        }
        return isWhitelisted;
    }

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AuthService authService; // AuthService 의존성 주입

    private int authNumber;

    /**
     * 랜덤 인증 번호 생성
     * 6자리의 숫자로 이루어진 인증 번호를 생성합니다.
     */
    public void makeRandomNumber() {
        logger.info("랜덤 인증 번호 생성을 시작합니다.");
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber.toString());
        logger.info("랜덤 인증 번호 생성 완료: {}", authNumber);
    }

    // DNS Lookup을 통한 도메인 존재 여부 검증
    public boolean isDomainValid(String email) {
        // 이메일 주소에서 도메인 추출
        String domain = email.substring(email.indexOf('@') + 1);
        try {
            InetAddress.getByName(domain); // 도메인에 대한 DNS 조회
            return true; // 도메인 존재
        } catch (Exception e) {
            logger.warn("도메인이 존재하지 않습니다: {}", domain, e);
            return false; // 도메인 존재하지 않음
        }
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }



    /**
     * 인증 이메일 생성 및 전송
     * @param email 수신자 이메일 주소
     * @return 생성된 인증 번호
     */
    public String joinEmail(String email) {
        logger.info("인증 이메일 생성 및 전송 시작 - 수신자: {}", email);

        // 이메일 형식 및 도메인 유효성 검증
        if (!isEmailValid(email) || !isDomainValid(email)) {
            logger.error("잘못된 이메일 형식이거나 존재하지 않는 도메 : {}", email);
            throw new IllegalArgumentException("잘못된 이메일 형식이거나 존재하지 않는 도메인");
        }

        makeRandomNumber();
        // 발신 이메일을 application.properties에서 가져옴
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
        String setFrom = "doosan0425@naver.com";
        // 이메일 내용에 만료 시간 강조 추가
        String content = "서비스를 이용해주셔서 감사합니다." +
                "<br><br>" +
                "인증 번호는 <strong>" + authNumber + "</strong> 입니다." +
                "<br>" +
                "<span style=\"color: red;\">해당 인증 번호는 10분 후 만료됩니다.</span>" +
                "<br>" +
                "인증번호를 입력해 주세요." +
                 "<br>" +
                "주의 : 인증 번호를 다른 사람에게 공유 하지 마세요." ;

        logger.debug("인증 이메일 내용: {}", content);

        mailSend(setFrom, toMail, title, content);

        // Redis에 인증 번호와 이메일 저장
        redisUtil.setDataExpire(String.valueOf(authNumber), email, 60 * 10L); // 만료 시간 10분 (600초)
        logger.info("인증 번호를 Redis에 저장 완료 - 키: {}, 이메일: {}, 만료 시간: {}초", authNumber, email, 60 * 10L);

        return String.valueOf(authNumber);
    }


    /**
     * 이메일 전송
     * @param setFrom 발신자 이메일
     * @param toMail 수신자 이메일
     * @param title 이메일 제목
     * @param content 이메일 내용
     */
    public void mailSend(String setFrom, String toMail, String title, String content) {
        logger.info("이메일 전송 시작 - 발신자: {}, 수신자: {}, 제목: {}", setFrom, toMail, title);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
            logger.info("이메일 전송 성공 - 수신자: {}", toMail);

        } catch (MessagingException e) {
            logger.error("이메일 전송 실패 - 수신자: {}, 오류: {}", toMail, e.getMessage(), e);
        }

    }

    /**
     * 인증 번호 검증
     * @param email 사용자 이메일
     * @param authNum 입력한 인증 번호
     * @return 인증 성공 여부
     */
    public Boolean CheckAuthNum(@Email @NotEmpty(message = "이메일을 입력해 주세요") String email,
                                @NotEmpty(message = "인증 번호를 입력해 주세요") String authNum) {
        logger.info("인증 번호 검증 시작 - 이메일: {}, 인증 번호: {}", email, authNum);

        // Redis에서 인증 번호 조회
        String storedEmail = redisUtil.getData(authNum);
        if (storedEmail == null) {
            logger.warn("인증 번호가 Redis에 존재하지 않음 - 인증 번호: {}", authNum);
            return false;
        }

        logger.debug("Redis에서 조회된 이메일: {}", storedEmail);

        // Redis에 저장된 이메일과 입력받은 이메일이 일치하는지 확인
        boolean isMatch = storedEmail.equals(email);
        if (isMatch) {
            logger.info("인증 성공 - 이메일: {}", email);
        } else {
            logger.warn("인증 실패 - 입력 이메일: {}, 저장된 이메일: {}", email, storedEmail);
        }
        return isMatch;
    }

    public String verifyEmailCode(String email, String authNum) {
        String storedEmail = redisUtil.getData(authNum); // Redis에서 인증 번호에 해당하는 이메일 조회
        if (storedEmail == null || !storedEmail.equals(email)) {
            throw new CustomException("이메일 인증 실패. 올바른 인증 코드를 입력해주세요.", "EMAIL_VERIFICATION_FAILED");
        }
        return "ok"; // 인증 성공 시 메시지 반환
    }
}
