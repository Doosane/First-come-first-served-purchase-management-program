package com.doosan.msa.user.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

/**
 * LoginRequestDTO 클래스
 * - 로그인 요청 정보를 처리하기 위한 DTO 클래스
 * - 사용자의 이메일과 비밀번호를 포함하며 유효성 검사를 수행
 */
@Getter
@NoArgsConstructor
@Slf4j // 로깅 추가
public class LoginRequestDTO {

    /**
     * 사용자 이메일
     * - 공백 허용 안 함
     */
    @NotBlank(message = "이메일과 비밀번호를 모두 입력해주세요!")
    private String email;

    /**
     * 사용자 비밀번호
     * - 공백 허용 안 함
     */
    @NotBlank(message = "이메일과 비밀번호를 모두 입력해주세요!")
    private String password;

    /**
     * LoginRequestDTO 객체 생성자
     * - 초기화 시 이메일과 비밀번호 값 로깅
     *
     * @param email    사용자 입력 이메일
     * @param password 사용자 입력 비밀번호
     */
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
        log.info("LoginRequestDTO 객체 생성: email = {}, password = [PROTECTED]", email);
    }

    /**
     * 이메일 값 접근 시 로그 출력
     *
     * @return String 이메일 값
     */
    public String getEmail() {
        log.debug("LoginRequestDTO - 이메일 값 조회 요청: {}", email);
        return email;
    }

    /**
     * 비밀번호 값 접근 시 로그 출력
     *
     * @return String 비밀번호 값
     */
    public String getPassword() {
        log.debug("LoginRequestDTO - 비밀번호 값 조회 요청: [PROTECTED]");
        return password;
    }
}
