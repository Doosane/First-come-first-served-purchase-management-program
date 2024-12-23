package com.doosan.msa.order.repository;

import com.doosan.msa.order.entity.BookStatus;
import com.doosan.msa.order.entity.ExamBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ExamBookRepository 인터페이스
 */
@Repository
public interface ExamBookRepository extends JpaRepository<ExamBook, Long> {

    // 특정 주문 ID로 ExamBook을 조회
    Optional<ExamBook> findByOrderId(Long orderId);

    // 상태와 예약 날짜로 ExamBook 목록 조회
    List<ExamBook> findAllByStatusAndBookingDate(BookStatus bookStatus, LocalDate localDate);
}

