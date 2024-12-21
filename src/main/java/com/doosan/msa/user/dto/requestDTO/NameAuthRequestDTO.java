package com.doosan.msa.user.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * NameAuthRequestDTO 클래스
 * - 이름 인증 요청을 처리하기 위한 DTO 클래스
 * - 사용자의 닉네임 데이터를 담고 있으며, 유효성 검사를 포함
 */
@Getter
@NoArgsConstructor
@Slf4j // 로깅 추가
public class NameAuthRequestDTO {
    /**
     * 이름
     * - 공백 허용 안 함
     * - 최소 1자, 최대 40자 제한
     * - 지정된 정규식 패턴 준수
     */
    @NotBlank(message = "이름에 공백은 허용되지 않습니다.")
    @Size(min = 1, max = 40, message = "이름은 최소 1자 이상, 최대 40자 미만으로 만들어주세요.")
    @Pattern(regexp = "[0-9a-zA-Zㄱ-ㅎ가-힣]*${1,40}", message = "이름 형식을 확인해 주세요.")
    private String name;

    /**
     * NameAuthRequestDTO 객체 생성 시 초기화 로그 출력
     *
     * @param name 사용자 이름 입력
     */
    public NameAuthRequestDTO(String name) {
        this.name = name;
        log.info("NameAuthRequestDTO 객체 생성: name = {}", name);
    }

    /**
     * 이름 값 검증 후 로그 출력
     *
     * @return String 이름 값
     */
    public String getName() {
        log.debug("NameAuthRequestDTO - 이름 조회 요청: {}", name);
        return name;
    }
}
