package com.doosan.msa.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 세션을 나타내는 엔티티 클래스
 *
 * - 시험 세션(TestSession)에 대한 주요 정보를 담고 있음
 * - 관련된 상태(Status) 정보와 연관 관계를 가짐
 */
@Entity
@NamedEntityGraph( // EntityGraph를 활용해 상태(Status) 컬렉션을 조회하도록 설정
        name = "TestSession.withStatus", // 엔티티 그래프의 이름
        attributeNodes = @NamedAttributeNode("status") // 상태 컬렉션을 로딩할 때 포함
)
@Table(name = "exam_sessions") // 데이터베이스 테이블 이름을 "exam_sessions"로 설정
@Data // Lombok을 사용하여 Getter, Setter, toString, equals, hashCode 메서드 자동 생성
@NoArgsConstructor // 매개변수가 없는 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동 생성
public class TestSession {

    @Id // 기본 키 필드임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략 설정
    private Long id; // 테스트 세션 ID (고유 식별자)

    @Column(nullable = false) // 데이터베이스 컬럼으로 매핑, null 값 허용 안 함
    private String category; // 테스트 세션의 카테고리 (예: 수학, 과학 등)

    @Column(nullable = false) // 데이터베이스 컬럼으로 매핑, null 값 허용 안 함
    private String name; // 테스트 세션의 이름 또는 제목

    private Boolean online; // 세션이 온라인인지 여부를 나타내는 플래그

    @Column(nullable = false)
    private String ownerHomeId; // 세션을 소유한 사용자의 홈 ID

    private boolean sub; // 서브 세션 여부를 나타내는 플래그

    @Column(nullable = false)

    @OneToMany( // 테스트 세션과 상태(Status) 엔티티 간의 일대다 관계 설정
            mappedBy = "testSession", // 상태(Status) 엔티티의 "testSession" 필드에 의해 매핑됨
            cascade = CascadeType.ALL, // 세션과 관련된 상태 엔티티가 함께 관리됨 (삽입, 삭제 등)
            orphanRemoval = true, // 세션에서 상태가 제거되면 상태 엔티티도 삭제
            fetch = FetchType.LAZY // 상태 컬렉션은 지연 로딩으로 설정
    )
    private List<Status> status = new ArrayList<>(); // 세션에 관련된 상태 목록
}
