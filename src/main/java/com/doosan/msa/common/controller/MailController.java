package com.doosan.msa.common.controller;


import com.doosan.msa.common.dto.requestDTO.EmailCheckDTO;
import com.doosan.msa.common.dto.requestDTO.EmailRequestDTO;
import com.doosan.msa.common.service.MailSendService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class MailController {
    private final MailSendService mailService;
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    /**
     * 인증 후 이메일로 전달받은 랜덤 코드로 이메일 인증 체크
     *
     * @param emailCheckDTO
     * @return 응답 메시지
     */
    @PostMapping("/email/verifyNumber")
    public String verifyEmailCode(@RequestBody @Valid EmailCheckDTO emailCheckDTO) {
        logger.info("이메일 인증 코드 검증 시작 - 이메일: {}, 인증 코드: {}", emailCheckDTO.getEmail(), emailCheckDTO.getAuthNum());
        return mailService.verifyEmailCode(emailCheckDTO.getEmail(), emailCheckDTO.getAuthNum());
    }
}
