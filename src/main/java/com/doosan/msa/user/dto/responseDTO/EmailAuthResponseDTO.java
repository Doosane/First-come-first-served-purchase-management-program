package com.doosan.msa.user.dto.responseDTO;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이메일 인증 결과를 클라이언트로 전달하기 위한 DTO
 */
@Builder
@Getter
@NoArgsConstructor
@Slf4j // 로깅 기능 추가
public class EmailAuthResponseDTO {

    private Long id; // 사용자 ID
    private String email; // 사용자 이메일

    /**
     * 객체 생성 로그 출력
     */
    public EmailAuthResponseDTO(Long id, String email) {
        this.id = id;
        this.email = email;
        log.info("EmailAuthResponseDTO 객체 생성 - ID: {}, Email: {}", id, email);
    }

    /**
     * 이메일 값 설정 로그 출력
     * @param email 사용자 이메일
     */
    public void setEmail(String email) {
        this.email = email;
        log.info("이메일 설정됨 - Email: {}", email);
    }

    /**
     * ID 값 설정 로그 출력
     * @param id 사용자 ID
     */
    public void setId(Long id) {
        this.id = id;
        log.info("ID 설정됨 - ID: {}", id);
    }
}
