package com.doosan.msa.order.entity;

import com.doosan.msa.exam.entity.ExamSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

/**
 * 위시 리스트
 */
@Entity
@Table(name = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 위시리스트 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession; // 시험 세션 정보와의 다대일 관계

    @Column(nullable = false)
    private String userId;  // 사용자 ID
}
