package com.doosan.msa.exam.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 시험 세션 생성 요청을 처리하기 위한 DTO 클래스
 * 클라이언트에서 전달받은 시험 세션 관련 데이터를 처리하기 위해 사용됨
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionRequestDTO {

    private String category; // 시험 세션의 카테고리

    private String name; // 시험 세션의 이름

    private boolean online; // 온라인 여부를 나타내는 플래그 , true: 온라인 세션, false: 오프라인 세션

    private boolean sub; // 세션의 서브 여부를 나타내는 플래그 , true: 서브 세션, false: 메인 세션

    private List<StatusDTO> status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDTO {

        private String code; // 상태 코드 , "ACTIVE", "INACTIVE", "PENDING"

        private String value; // "활성화", "비활성화"
    }
}
