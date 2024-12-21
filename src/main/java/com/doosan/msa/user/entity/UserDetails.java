package com.doosan.msa.user.entity;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * Spring Security의 UserDetails 인터페이스를 상속하여 사용자 정보를 정의하는 인터페이스
 * 인증 및 권한 부여 과정에서 사용자의 상태와 권한 정보를 제공
 */
public interface UserDetails extends org.springframework.security.core.userdetails.UserDetails {

    /**
     * 사용자의 권한 정보 반환
     * @return GrantedAuthority의 컬렉션 (사용자의 권한 리스트)
     */
    @Override
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * 사용자의 비밀번호 반환
     * @return 사용자 비밀번호 (암호화된 상태)
     */
    @Override
    String getPassword();

    /**
     * 사용자의 이름 또는 아이디 반환
     * @return 사용자 이름 또는 아이디
     */
    @Override
    String getUsername();

    /**
     * 계정의 만료 여부 확인
     * @return 만료되지 않았으면 true, 만료되었으면 false
     */
    @Override
    boolean isAccountNonExpired();

    /**
     * 계정의 잠금 여부 확인
     * @return 잠금되지 않았으면 true, 잠금되었으면 false
     */
    @Override
    boolean isAccountNonLocked();

    /**
     * 자격 증명의 만료 여부 확인
     * @return 만료되지 않았으면 true, 만료되었으면 false
     */
    @Override
    boolean isCredentialsNonExpired();

    /**
     * 계정 활성화 여부 확인
     * @return 활성화 상태이면 true, 비활성화 상태이면 false
     */
    @Override
    boolean isEnabled();
}
