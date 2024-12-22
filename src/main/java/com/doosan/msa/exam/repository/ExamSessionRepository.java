package com.doosan.msa.exam.repository;

import com.doosan.msa.exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * JPQL 활용, JOIN FETCH를 통해 연관된 상태 데이터를 함께 로드하여 성능 최적화
 */
@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {

    Optional<ExamSession> findById(Long id);

    // @Query에서 DISTINCT를 추가하여 중복된 엔티티가 반환되지 않도록 설정함 12-22
    @Query("SELECT  DISTINCT ts FROM ExamSession ts JOIN FETCH ts.status")
    List<ExamSession> findAllWithStatus();

    // JOIN FETCH 사용하여 연관 데이터 즉시 로딩
    @Query("SELECT ts FROM ExamSession ts LEFT JOIN FETCH ts.status WHERE ts.id = :id")
    Optional<ExamSession> findByIdWithStatus(@Param("id") Long id);



}