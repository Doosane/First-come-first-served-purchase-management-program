package com.doosan.msa.user.service;

import com.doosan.msa.common.util.AESUtil;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.entity.UserDetailsImpl;
import com.doosan.msa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService 구현체
 * Spring Security의 인증 과정을 위해 사용자 정보를 조회하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // 사용자 데이터베이스 접근을 위한 UserRepository
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("평문 이메일: {}", email);
        // 이메일 복호화 후 검색
        String encryptedEmail = AESUtil.encrypt(email); // 이메일 암호화
        log.debug("암호화된 이메일: {}", encryptedEmail);
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        log.debug("데이터베이스 조회 결과: {}", user != null ? "존재함" : "존재하지 않음");

        return new UserDetailsImpl(user);
    }

}