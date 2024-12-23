package com.doosan.msa.common.shared;

import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 권한(Enum) 클래스
 * - 사용자 권한을 관리하기 위한 열거형 정의
 */
@Slf4j
public enum Authority {

    ROLE_USER, // 학생 사용자 권한
    ROLE_INSTRUCTOR; // 강사

    // Enum 클래스 초기화 시 로그 기록
    static {
        log.info("Authority Enum 클래스가 초기화되었습니다.");
    }

    /**
     * 권한 이름을 반환하는 메서드
     * @return String 권한 이름
     */
    public String getAuthorityName() {
        String authorityName = this.name();
        log.debug("권한 이름 반환: {}", authorityName);
        return authorityName;
    }
}
