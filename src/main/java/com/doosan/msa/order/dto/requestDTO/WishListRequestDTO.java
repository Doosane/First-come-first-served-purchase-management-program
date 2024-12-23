package com.doosan.msa.order.dto.requestDTO;

import lombok.Data;

/**
 * 위시리스트 요청 DTO 클래스
 */
@Data
public class WishListRequestDTO {
    private String userId; // 사용자 ID
    private Long examSessionId; // 시험 세션 ID
}
