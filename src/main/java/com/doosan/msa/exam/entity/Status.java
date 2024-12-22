package com.doosan.msa.exam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 세션 상태
 */
@Entity
@Table(name = "statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // 상태 코드 ("READY", "ONGOING", "COMPLETED")

    @Column(nullable = false)
    private String value; // 상태의 설명

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 설정, 연관된 ExamSession 엔티티와 연결
    @JoinColumn(name = "exam_session_id") // 외래 키 컬럼 이름을 "exam_session_id"로 설정
    private ExamSession examSession; // 이 상태가 속하는 시험 세션 (ExamSession 엔티티와의 연관 관계)

}
