package com.doosan.msa.order.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * 주문 응답 데이터를 처리하기 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId; // 주문 ID
    private String status; // 주문 상태 (예: CONFIRMED, SHIPPED 등)
    private LocalDate orderDate; // 주문 날짜
    private LocalDate deliveryDate;  // 배송 완료 날짜
}
