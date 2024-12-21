package com.doosan.msa.user.controller;

import com.doosan.msa.user.dto.requestDTO.*;
import com.doosan.msa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    /****
     * 이메일로 인증 코드 발송
     * @param requestDTO
     * @return
     */
    @PostMapping("/email/request")
    public ResponseEntity<?> sendEmailAuth(@RequestBody EmailAuthRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.sendEmailAuth(requestDTO));
    }
}