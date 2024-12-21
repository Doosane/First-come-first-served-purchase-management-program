package com.doosan.msa.user.dto.requestDTO;

import lombok.Data;

@Data
public class EmailAuthVerifyRequestDTO {
    private String email; // 인증에 사용될 이메일
    private String authCode; // 전달받은 인증 코드 숫자
}
