package com.doosan.msa.exam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 시험 세션
 */
@Entity
@NamedEntityGraph(
        name = "ExamSession.withStatus", // EntityGraph를 활용하여 상태(Status) 컬렉션을 조회 가능하도록 설정
        attributeNodes = @NamedAttributeNode("status") // 상태 컬렉션을 즉시 로딩 대상으로 포함
)
@Table(name = "exam_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 시험 세션의 고유 식별자 (Primary Key)

    @Column(nullable = false)
    private String category; // 시험 세션의 카테고리

    @Column(nullable = false)
    private String name; // 시험 세션의 이름

    @Column(nullable = false)
    private Boolean online; // 세션의 온라인 여부

    @Column(nullable = false)
    private boolean sub; // 서브 세션 여부

    // 상태(Status)와의 일대다 관계 설정
    @OneToMany(
            mappedBy = "examSession", // 상태(Status) 엔티티의 "examSession" 필드와 매핑
            cascade = CascadeType.ALL, // 세션과 관련된 상태 엔티티가 함께 관리됨 (삽입, 삭제 등)
            orphanRemoval = true, // 세션에서 상태가 제거되면 상태 엔티티도 삭제
            fetch = FetchType.LAZY // 상태 컬렉션은 지연 로딩으로 설정
    )

    private List<Status> status = new ArrayList<>(); // 세션에 관련된 상태(Status) 목록
}
