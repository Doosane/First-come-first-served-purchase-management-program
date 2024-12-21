package com.doosan.msa.user.service;

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
    /**
     * 사용자의 이름(name)을 기반으로 사용자 세부 정보를 로드
     * @param name 사용자 이름
     * @return UserDetails 사용자의 인증 정보를 반환
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String name) {
        log.info("loadUserByUsername 호출됨: 사용자 이름 - {}", name);

        // 사용자 이름으로 데이터베이스에서 사용자 조회
        return userRepository.findByName(name)
                .map(user -> {
                    log.info("사용자 조회 성공: {}", user.getName());
                    return new UserDetailsImpl(user); // UserDetailsImpl로 변환
                })
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다: {}", name);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + name);
                });
    }
}