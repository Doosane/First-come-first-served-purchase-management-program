package com.doosan.msa.common.entity;

import com.doosan.msa.common.util.ValidEmailDomain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class EmailDomainValidator implements ConstraintValidator<ValidEmailDomain, String> {

    private static final List<String> ALLOWED_DOMAINS = Arrays.asList("gmail.com", "naver.com", "daum.net");

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return false; // null 또는 빈 값은 유효하지 않음
        }

        // 이메일에서 도메인 추출
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false; // 이메일 형식이 올바르지 않음
        }

        String domain = parts[1];

        // 도메인이 화이트리스트와 정확히 일치하는지 확인
        boolean isValid = ALLOWED_DOMAINS.contains(domain);

        if (!isValid) {
            System.out.println("유효하지 않은 도메인: " + domain); // 디버깅용 로그
        }

        return isValid;
    }
}
