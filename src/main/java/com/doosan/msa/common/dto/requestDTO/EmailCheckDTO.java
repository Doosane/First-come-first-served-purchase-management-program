package com.doosan.msa.common.dto.requestDTO;

import com.doosan.msa.common.util.ValidEmailDomain;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EmailCheckDTO {

    @NotEmpty(message = "이메일을 입력해 주세요")
    @ValidEmailDomain // 커스텀 Validator 어노테이션 추가
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String authNum;
}
