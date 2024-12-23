package com.doosan.msa.order.entity;
import com.doosan.msa.exam.entity.ExamSession;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "exam_book")
@Data
public class ExamBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 시험 교재 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // 시험 교재와 연관된 주문 정보

    @ManyToOne
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession; // 시험 교재가 사용될 특정 시험 세션

    @Column(name = "user_id", nullable = false)
    private String userId; // 교재를 사용하는 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status; // 교재 상태, BOOKED(예약됨), DELIVERED(배송 완료)

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate; // 교재가 예약된 날짜

    @Column(name = "delivery_date")
    private LocalDate deliveryDate; // 교재가 사용자에게 전달된 날짜, 배송 완료일을 저장
}
