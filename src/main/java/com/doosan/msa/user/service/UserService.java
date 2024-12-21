package com.doosan.msa.user.service;

import com.doosan.msa.common.jwt.TokenProvider;
import com.doosan.msa.common.service.MailSendService;
import com.doosan.msa.common.util.AESUtil;
import com.doosan.msa.user.dto.requestDTO.*;
import com.doosan.msa.user.dto.responseDTO.*;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.entity.UserDetailsImpl;
import com.doosan.msa.user.repository.RefreshTokenRepository;
import com.doosan.msa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailSendService mailSendService;
    private final AuthService authService;

    // 이메일 인증 요청
    @Transactional
    public ResponseDTO<?> sendEmailAuth(EmailAuthRequestDTO requestDTO) {
        log.info("이메일 인증 전송: {}", requestDTO.getEmail());

        if (isPresentUser(requestDTO.getEmail()) != null) {
            log.warn("이미 사용중인 이메일: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.CONFLICT.value(), "DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다.");
        }

        String authCode = mailSendService.joinEmail(requestDTO.getEmail());
        log.info("인증 코드 전송 완료: {}", authCode);

        return ResponseDTO.success(HttpStatus.OK.value(), "EMAIL_AUTH_SENT", "인증 코드가 이메일로 전송되었습니다.", null);
    }


    // 이메일 인증 확인
    @Transactional
    public ResponseDTO<?> verifyEmailAuth(EmailAuthVerifyRequestDTO requestDTO) {
        log.info("이메일 인증 확인 요청: {}", requestDTO.getEmail());

        if (requestDTO.getAuthCode() == null || requestDTO.getAuthCode().isEmpty()) {
            log.warn("인증 코드가 누락되었습니다.");
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST.value(), "INVALID_AUTH_CODE", "인증 코드는 필수 입력 항목입니다.");
        }

        boolean isValid = mailSendService.CheckAuthNum(requestDTO.getEmail(), requestDTO.getAuthCode());
        if (!isValid) {
            log.warn("인증 실패: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.UNAUTHORIZED.value(), "AUTH_CODE_MISMATCH", "인증 코드와 이메일이 일치하지 않습니다.");
        }

        log.info("이메일 인증 성공: {}", requestDTO.getEmail());
        return ResponseDTO.success(HttpStatus.OK.value(), "EMAIL_VERIFIED", "이메일 인증이 완료되었습니다.", null);
    }


    // 회원가입 email 필드를 저장하기 전에 암호화하도록 서비스 코드를 수정
    @Transactional
    public ResponseDTO<?> createUser(UserRequestDTO requestDTO) {
        log.info("회원가입 요청: {}", requestDTO.getEmail());

        // 이메일 암호화
        String encryptedEmail = AESUtil.encrypt(requestDTO.getEmail());

        if (isPresentUser(encryptedEmail) != null) {
            log.warn("중복된 이메일: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.CONFLICT.value(), "DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다.");
        }

        // 기존 로직 유지
        if (!authService.CheckAuthNum(requestDTO.getEmail(), requestDTO.getAuthNum())) {
            log.warn("이메일 인증이 필요합니다: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.UNAUTHORIZED.value(), "EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다.");
        }

        if (!requestDTO.getPassword().equals(requestDTO.getPasswordConfirm())) {
            log.warn("비밀번호 불일치: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST.value(), "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }

        try {
            // 다른 필드 암호화
            String encryptedName = AESUtil.encrypt(requestDTO.getName());
            String encryptedPhone = AESUtil.encrypt(requestDTO.getPhone());
            String encryptedAddress = AESUtil.encrypt(requestDTO.getAddress());

            // 암호화된 데이터를 저장
            User user = User.builder()
                    .name(encryptedName)
                    .password(passwordEncoder.encode(requestDTO.getPassword()))
                    .email(encryptedEmail)
                    .phone(encryptedPhone)
                    .address(encryptedAddress)
                    .build();

            userRepository.save(user);
            userRepository.flush();
            log.info("회원가입 성공: {}", requestDTO.getEmail());

            return ResponseDTO.success(
                    HttpStatus.CREATED.value(),
                    "USER_CREATED",
                    "회원가입이 완료되었습니다.",
                    UserResponseDTO.builder()
                            .id(user.getId())
                            .name(requestDTO.getName())
                            .email(requestDTO.getEmail())
                            .phone(requestDTO.getPhone())
                            .address(requestDTO.getAddress())
                            .createdAt(user.getCreatedAt())
                            .modifiedAt(user.getModifiedAt())
                            .build()
            );
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생: {}", e.getMessage(), e);
            return ResponseDTO.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ENCRYPTION_ERROR", "회원 정보 저장 중 오류가 발생했습니다.");
        }
    }


    // 민감 데이터 마스킹 처리 유틸 메서드
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****"; // 데이터가 짧을 경우 전부 마스킹
        }
        return data.substring(0, 2) + "****" + data.substring(data.length() - 2); // 앞 2자리와 뒤 2자리만 남김
    }



    // 유저 존재 여부 확인
    // 암호화된 이메일은 검색 시 복호화해야 하므로, 데이터베이스 검색 시 복호화하거나 암호화된 상태로 비교해야 한다
    private User isPresentUser(String email) {
        log.debug("유저 이메일 확인: {}", email);

        // 입력된 이메일을 암호화하여 비교
        String encryptedEmail = AESUtil.encrypt(email);
        Optional<User> optionalUser = userRepository.findByEmail(encryptedEmail);
        return optionalUser.orElse(null);
    }

    // 로그인
    @Transactional
    public ResponseDTO<?> login(LoginRequestDTO requestDTO, HttpServletResponse response) {
        log.info("로그인 요청: {}", requestDTO.getEmail());

        User user = isPresentUser(requestDTO.getEmail());
        if (user == null) {
            log.warn("사용자를 찾을 수 없습니다: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND", "이메일이 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            log.warn("비밀번호 불일치: {}", requestDTO.getEmail());
            return ResponseDTO.fail(HttpStatus.UNAUTHORIZED.value(), "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
        }

        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(user);
        tokenToHeaders(tokenDTO, response);

        log.info("로그인 성공: {}", requestDTO.getEmail());
        return ResponseDTO.success(
                HttpStatus.OK.value(),
                "LOGIN_SUCCESS",
                "로그인이 성공적으로 완료되었습니다.",
                UserResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .createdAt(user.getCreatedAt())
                        .modifiedAt(user.getModifiedAt())
                        .build()
        );
    }

    // 로그아웃
    @Transactional
    public ResponseDTO<?> logout(HttpServletRequest request) {
        log.info("로그아웃 요청");

        String refreshToken = request.getHeader("Refresh_Token");
        if (!tokenProvider.validateToken(refreshToken)) {
            log.warn("유효하지 않은 리프레시 토큰: {}", refreshToken);
            return ResponseDTO.fail(HttpStatus.UNAUTHORIZED.value(), "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        }

        User user = tokenProvider.getUserFromAuthentication(refreshToken);
        log.debug("로그아웃 대상 사용자: {}", user.getEmail());

        refreshTokenRepository.deleteByUserId(user.getId());
        log.info("로그아웃 성공");
        return ResponseDTO.success(HttpStatus.OK.value(), "LOGOUT_SUCCESS", "로그아웃이 완료되었습니다.", null);
    }


    // 헤더에 토큰 추가
    private void tokenToHeaders(TokenDTO tokenDTO, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDTO.getAccessToken());
        response.addHeader("Refresh_Token", tokenDTO.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDTO.getAccessTokenExpiresIn().toString());
    }

    /**
     * 토큰 재발급 ( 리이슈 )
     * @param request
     * @param response
     * @return
     */
    @Transactional
    public ResponseDTO<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("토큰 재발급 시작");

        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            log.warn("Refresh Token 이 유효하지 않습니다.");
            return ResponseDTO.fail(HttpStatus.BAD_REQUEST.value(),"INVALID_TOKEN", "리프레시 토큰이 유효하지 않습니다.");
        }

        User user = refreshTokenRepository.findByValue(request.getHeader("Refresh_Token")).get().getUser();
        log.debug("토큰 재발급 유저 확인 {}", user);

        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(user);
        tokenToHeaders(tokenDTO, response);

        log.info("토큰이 성공적으로 재발급 되었습니다 : {}", user.getEmail());
        return ResponseDTO.success(HttpStatus.OK.value(),"TOKEN_REISSUE_SUCCESS","토큰이 성공적으로 재발급 되었습니다.",null);

    }

    /****
     *  유저 계정 삭제
     * @param userId
     * @param userDetails
     * @return
     */
    @Transactional
    public ResponseDTO<?> withdrawUser(Long userId, UserDetailsImpl userDetails) {
        log.info("회원탈퇴 시작 user ID: {}", userId);
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("등록되지 않은 회원입니다.")
        );
        log.debug("회원 탈퇴 계정 확인: {}", user);
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh token 삭제 user ID: {}", userId);
        userRepository.deleteById(userId);
        log.info("계정 삭제 user ID: {}", userId);
        return ResponseDTO.success(HttpStatus.OK.value(), "USER_DELETE_SUCCESSFULLY", "회원 탈퇴가 완료되었습니다.", null);
    }
}