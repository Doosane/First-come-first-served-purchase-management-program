package com.doosan.msa.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * UserDetailsImpl 클래스
 * - Spring Security의 UserDetails 인터페이스를 구현하여 사용자 정보를 관리
 * - 사용자 권한, 인증 상태 등을 반환
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private User user; // 사용자 엔티티 객체

    /**
     * 사용자 권한 반환 메서드
     * @return Collection<? extends GrantedAuthority> 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("사용자 권한 조회 시작");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        log.debug("사용자 권한: {}", authorities);
        return authorities;
    }

    /**
     * 사용자 비밀번호 반환 메서드
     * @return String 비밀번호
     */
    @Override
    public String getPassword() {
        String password = user.getPassword();
        log.info("사용자 비밀번호 조회");
        log.debug("비밀번호: {}", password != null ? "비밀번호 존재" : "비밀번호 없음");
        return password;
    }

    /**
     * 사용자 이름 반환 메서드
     * @return String 사용자 이름
     */
    @Override
    public String getUsername() {
        String username = user.getName();
        log.info("사용자 이름 조회");
        log.debug("사용자 이름: {}", username);
        return username;
    }

    /**
     * 계정 만료 여부 반환 메서드
     * @return boolean 계정 만료 여부 (false: 만료됨)
     */
    @Override
    public boolean isAccountNonExpired() {
        log.info("계정 만료 여부 확인");
        log.debug("계정 만료 상태: 만료됨");
        return false;
    }

    /**
     * 계정 잠금 여부 반환 메서드
     * @return boolean 계정 잠금 여부 (false: 잠김)
     */
    @Override
    public boolean isAccountNonLocked() {
        log.info("계정 잠금 여부 확인");
        log.debug("계정 잠금 상태: 잠김");
        return false;
    }

    /**
     * 자격 증명 만료 여부 반환 메서드
     * @return boolean 자격 증명 만료 여부 (false: 만료됨)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        log.info("자격 증명 만료 여부 확인");
        log.debug("자격 증명 만료 상태: 만료됨");
        return false;
    }

    /**
     * 계정 활성화 여부 반환 메서드
     * @return boolean 계정 활성화 여부 (false: 비활성화)
     */
    @Override
    public boolean isEnabled() {
        log.info("계정 활성화 여부 확인");
        log.debug("계정 활성화 상태: 비활성화");
        return false;
    }
}
