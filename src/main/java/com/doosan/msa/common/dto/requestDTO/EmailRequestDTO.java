package com.doosan.msa.common.dto.requestDTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Email 요청 데이터 전송 객체
 * - @Email: 이메일 형식 검증
 * - @NotEmpty: 빈 값 허용 불가, 사용자 메시지 제공
 */
@Getter
@Setter
public class EmailRequestDTO {

    @Email(message = "유효한 이메일 주소를 입력해 주세요") // 이메일 형식 검증
    @NotEmpty(message = "이메일을 입력해 주세요") // 빈 값 허용 안 됨
    private String email;

}
