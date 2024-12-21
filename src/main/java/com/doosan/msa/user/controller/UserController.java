package com.doosan.msa.user.controller;

import com.doosan.msa.user.dto.responseDTO.ResponseDTO;
import com.doosan.msa.user.dto.requestDTO.LoginRequestDTO;
import com.doosan.msa.user.dto.requestDTO.UserRequestDTO;
import com.doosan.msa.user.entity.UserDetailsImpl;
import com.doosan.msa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * UserController 클래스
 * - 유저 관련 요청을 처리하는 REST 컨트롤러
 * - 회원가입, 로그인, 로그아웃, 토큰 재발급, 회원 탈퇴 등 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 처리 메서드
     * - 클라이언트로부터 회원가입 요청 데이터를 받아 처리
     *
     * @param requestDTO 회원가입 요청 데이터
     * @return ResponseDTO 회원가입 결과 반환
     * @throws IOException 예외 처리
     */
    @PostMapping(value = "/signup")
    public ResponseDTO<?> signup(@RequestBody @Valid UserRequestDTO requestDTO) throws IOException {
        log.info("회원가입 요청: {}", requestDTO);
        ResponseDTO<?> response = userService.createUser(requestDTO);
        log.info("회원가입 완료: {}", response);
        return response;
    }


    /**
     * 로그인 처리 메서드
     * - 클라이언트로부터 로그인 요청 데이터를 받아 처리
     *
     * @param requestDTO 로그인 요청 데이터
     * @param response   HttpServletResponse 객체
     * @return ResponseDTO 로그인 결과 반환
     */
    @PostMapping(value = "/login")
    public ResponseDTO<?> login(@RequestBody @Valid LoginRequestDTO requestDTO, HttpServletResponse response) {
        log.info("로그인 요청: {}", requestDTO.getEmail());
        ResponseDTO<?> result = userService.login(requestDTO, response);
        log.info("로그인 처리 완료: {}", result);
        return result;
    }

    /**
     * 로그아웃 처리 메서드
     * - 클라이언트로부터 로그아웃 요청을 받아 처리
     *
     * @param request HttpServletRequest 객체
     * @return ResponseDTO 로그아웃 결과 반환
     */
    @PostMapping(value = "/logout")
    public ResponseDTO<?> logout(HttpServletRequest request) {
        log.info("로그아웃 요청 처리 시작");
        ResponseDTO<?> result = userService.logout(request);
        log.info("로그아웃 처리 완료: {}", result);
        return result;
    }

    /**
     * 토큰 재발급 처리 메서드
     * - AccessToken 만료 시 RefreshToken을 사용해 토큰 재발급
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @return ResponseDTO 토큰 재발급 결과 반환
     */
    @PostMapping(value = "/reissue")
    public ResponseDTO<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("토큰 재발급 요청 처리 시작");
        ResponseDTO<?> result = userService.reissue(request, response);
        log.info("토큰 재발급 처리 완료: {}", result);
        return result;
    }

    /**
     * 회원 탈퇴 처리 메서드
     * - 특정 사용자를 삭제
     *
     * @param userId      탈퇴 대상 사용자 ID
     * @param userDetails 인증된 사용자 정보
     * @return ResponseDTO 회원 탈퇴 결과 반환
     */
    @DeleteMapping(value = "/withdrawl/{userId}")
    public ResponseDTO<?> withdrawal(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("회원 탈퇴 요청: userId = {}, 인증 사용자: {}", userId, userDetails.getUsername());
        ResponseDTO<?> result = userService.withdrawUser(userId, userDetails);
        log.info("회원 탈퇴 처리 완료: {}", result);
        return result;
    }
}