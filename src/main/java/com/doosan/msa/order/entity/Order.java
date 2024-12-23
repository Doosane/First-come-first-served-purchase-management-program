package com.doosan.msa.order.entity;

import com.doosan.msa.exam.entity.ExamSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession; // 시험 세션 정보 (다대일 관계)

    @Column(nullable = false)
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String status; // 주문 상태  CONFIRMED, CANCELLED, SHIPPED, DELIVERED, RETURNED

    @Column(nullable = false)
    private LocalDate orderDate; // 주문 날짜

    private LocalDate deliveryDate; // 배송 완료 날짜

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamBook> examBooks = new ArrayList<>(); // 주문에 연결된 여러 ExamBook (일대다 관계)
}

