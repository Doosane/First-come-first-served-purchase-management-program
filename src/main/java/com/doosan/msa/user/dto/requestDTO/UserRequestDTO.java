package com.doosan.msa.user.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 회원가입 요청 데이터를 담는 DTO 클래스
 * 유저가 입력한 이메일, 닉네임, 비밀번호, 주소, 비밀번호 확인 정보를 포함
 */
@Getter
@NoArgsConstructor
@Slf4j // 로깅 기능 추가
public class UserRequestDTO {

    private  String phone;

    @NotBlank(message = "이메일과 비밀번호를 모두 입력해주세요!")
    @Size(min = 8, max = 30, message = "8자리 이상 30자리 미만 글자로 이메일을 입력해주세요.")
    @Pattern(regexp = "^[0-9a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]+$", message = "이메일 형식을 확인해주세요.")
    private String email; // 사용자 이메일

    @NotBlank(message = "닉네임을 입력해주세요!")
    @Size(min = 1, max = 40, message = "닉네임은 최소 1자 이상 최대 40자 미만으로 입력해주세요.")
    @Pattern(regexp = "[0-9a-zA-Zㄱ-ㅎ가-힣]*${1,40}", message = "닉네임 형식을 확인해주세요.")
    private String name; // 사용자 닉네임

    @NotBlank(message = "이메일과 비밀번호를 모두 입력해주세요!")
    @Size(min = 4, max = 32, message = "비밀번호는 최소 4자 이상 최대 32자 미만으로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{4,32}$",
            message = "비밀번호에 영어 대소문자, 숫자, 특수문자를 모두 포함해주세요.")
    private String password; // 사용자 비밀번호

    private String address; // 사용자 주소 (필수 아님)

    public String passwordConfirm; // 비밀번호 확인용 필드

    private String authNum; // 인증 번호 필드 추가

    /**
     * 객체 생성 시 로그 출력
     */
    public UserRequestDTO(String email, String name, String phone,String password, String address, String passwordConfirm ,String authNum) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.passwordConfirm = passwordConfirm;
        this.authNum = authNum;

        log.info("UserRequestDTO 객체 생성 - Email: {}, Name: {}, Phone: {},Address: {} , authNum: {}" , email, name, phone, address, authNum);
        log.debug("Password와 PasswordConfirm: {}, {}", password, passwordConfirm);
    }

    /**
     * 이메일 값 설정 로그 출력
     * @param email 사용자 이메일
     */
    public void setEmail(String email) {
        this.email = email;
        log.info("이메일 설정됨 - Email: {}", email);
    }

    /**
     * 닉네임 값 설정 로그 출력
     * @param name 사용자 닉네임
     */
    public void setName(String name) {
        this.name = name;
        log.info("닉네임 설정됨 - Name: {}", name);
    }

    /**
     * 비밀번호 값 설정 로그 출력
     * @param password 사용자 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
        log.info("비밀번호 설정됨");
    }

    /**
     * 주소 값 설정 로그 출력
     * @param address 사용자 주소
     */
    public void setAddress(String address) {
        this.address = address;
        log.info("주소 설정됨 - Address: {}", address);
    }

    /**
     * 비밀번호 확인 값 설정 로그 출력
     * @param passwordConfirm 비밀번호 확인 값
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
        log.info("비밀번호 확인값 설정됨 - PasswordConfirm: {}", passwordConfirm);
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
        log.info("authNum: {}", authNum);
    }



}
