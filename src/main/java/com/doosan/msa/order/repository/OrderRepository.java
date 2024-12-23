package com.doosan.msa.order.repository;
import com.doosan.msa.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * OrderRepository 인터페이스
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 시험 세션 ID에 속한 모든 주문을 조회
    List<Order> findByExamSessionId(Long examSessionId);

    // 특정 사용자 ID로 주문 목록을 조회
    List<Order> findByUserId(String userId);

    // 특정 사용자 ID와 시험 세션 ID의 주문 존재 여부 확인
    boolean existsByUserIdAndExamSessionId(String userId, Long examSessionId);

}
