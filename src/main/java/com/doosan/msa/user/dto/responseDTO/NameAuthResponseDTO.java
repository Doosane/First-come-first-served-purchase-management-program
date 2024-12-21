package com.doosan.msa.user.dto.responseDTO;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 닉네임 인증 결과를 클라이언트로 전달하기 위한 DTO
 */
@Builder
@Getter
@NoArgsConstructor
@Slf4j // 로깅 기능 추가
public class NameAuthResponseDTO {

    private Long id; // 사용자 ID
    private String name; // 사용자 닉네임

    /**
     * 객체 생성 로그 출력
     */
    public NameAuthResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        log.info("NameAuthResponseDTO 객체 생성 - ID: {}, Name: {}", id, name);
    }

    /**
     * 닉네임 값 설정 로그 출력
     * @param name 사용자 닉네임
     */
    public void setName(String name) {
        this.name = name;
        log.info("닉네임 설정됨 - Name: {}", name);
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
