package com.doosan.msa.order.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 위시리스트 응답 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListResponseDTO {
    private Long wishListId;  // 위시리스트 ID
    private String examSessionName; // 시험 세션 이름
    private String category; // 카테고리
}
