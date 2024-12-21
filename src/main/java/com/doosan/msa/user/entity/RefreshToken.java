package com.doosan.msa.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

/**
 * Refresh Token 엔티티 클래스
 * 사용자와 연관된 리프레시 토큰을 관리하기 위한 데이터베이스 매핑 클래스
 */
@Getter
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
@Builder // 빌더 패턴 사용 가능
@Entity
@Table(name = "refresh_token")
@Slf4j
public class RefreshToken extends Timestamped { // Timestamped 클래스 상속으로 생성 및 수정 시간 자동 관리

    @Id
    @Column(nullable = false) // 기본 키 설정 및 null 불가
    private Long id;


    @JoinColumn(name = "user_id", nullable = false) // user_id 컬럼과 User 엔티티 매핑
    @OneToOne(fetch = FetchType.LAZY) // User 엔티티와 1:1 관계, 지연 로딩 설정
    private User user;


    @Column(nullable = false) // null 불가 설정
    private String value;

    /**
     * 리프레시 토큰 값 업데이트
     * @param token 새 리프레시 토큰 값
     */
    public void updateValue(String token) {
        log.info("리프레시 토큰 값 업데이트 - 이전 값: {}, 새로운 값: {}", this.value, token);
        this.value = token;
    }

    /**
     * 엔티티 생성 시 로깅
     */
    @PostPersist
    private void logCreation() {
        log.info("RefreshToken 엔티티 생성 - ID: {}, 사용자: {}, 토큰 값: {}", id, user != null ? user.getEmail() : "null", value);
    }

    /**
     * 엔티티 업데이트 시 로깅
     */
    @PostUpdate
    private void logUpdate() {
        log.info("RefreshToken 엔티티 업데이트 - ID: {}, 사용자: {}, 현재 토큰 값: {}", id, user != null ? user.getEmail() : "null", value);
    }

    /**
     * 엔티티 삭제 시 로깅
     */
    @PreRemove
    private void logDeletion() {
        log.info("RefreshToken 엔티티 삭제 - ID: {}, 사용자: {}, 토큰 값: {}", id, user != null ? user.getEmail() : "null", value);
    }
}
