package com.doosan.msa.order.dto.requestDTO;

import lombok.Data;

/**
 * 주문 요청 데이터를 처리하기 위한 DTO 클래스
 */
@Data
public class OrderRequestDTO {
    private String userId;  // 사용자 ID
    private Long examSessionId; // 시험 세션 ID
}
