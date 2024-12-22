package com.doosan.msa.exam.dto.responseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 시험 세션 응답 데이터를 처리하기 위한 DTO 클래스
 * 클라이언트로 반환될 세션 정보를 캡슐화하며, 시험 세션의 주요 속성 및 상태 정보를 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionResponseDTO {

    private String category; // 세션이 속한 카테고리

    private String id; // 세션 ID

    private String name; // 세션 이름

    private boolean online; // 온라인 여부 , 세션이 온라인인지 오프라인인지 여부를 나타냄

    /**
     * 서브 세션 여부
     * 세션이 메인 세션의 하위 세션인지 여부를 나타냄
     * `true`: 서브 세션
     * `false`: 메인 세션
     */
    private boolean sub; // 세션이 메인 세션의 하위 세션인지 여부를 나타냄

    private List<StatusDTO> status; // 세션의 상태 목록

    public ExamSessionResponseDTO(String category, String string, String name, Boolean online, String s, List<StatusDTO> collect) {
    }

    /**
     * 세션 상태 정보를 표현하기 위한 내부 클래스
     * 각 상태의 코드와 값을 포함
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDTO {

        private String code; // 상태를 나타내는 고유 코드 , ACTIVE , INACTIVE

        private String value; // 상태 값 , 활성화됨 , 비활성화됨
    }
}