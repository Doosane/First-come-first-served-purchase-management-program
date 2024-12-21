package com.doosan.msa.user.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 유저 응답 DTO
 * 클라이언트에 유저 정보를 전달하기 위한 클래스
 */
@Builder
@Getter // Getter 메서드 자동 생성
@NoArgsConstructor // 기본 생성자 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 생성
@Slf4j // 로깅 기능 추가
public class UserResponseDTO {

    private Long id; // 사용자 고유 ID
    private String name; // 사용자 이름
    private String phone; // 사용자 전화번호
    private String email; // 사용자 이메일
    private String address; // 사용자 주소
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime modifiedAt; // 수정 시간

    /**
     * DTO 생성 시 로그 기록
     */
    public void logCreation() {
        log.info("UserResponseDTO 생성 - ID: {}, 이름: {}, 이메일: {}, 폰: {}, 주소: {}", id, name, email, phone, address);
    }

    /**
     * DTO 데이터 출력용 메서드
     * @return DTO의 필드 정보를 문자열로 반환
     */
    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
