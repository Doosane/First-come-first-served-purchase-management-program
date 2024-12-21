package com.doosan.msa.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * 유저 정보를 저장하는 JPA 엔티티 클래스
 * 데이터베이스의 users 테이블과 매핑되며, 사용자 정보를 관리
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Slf4j
@Table(name = "users") // users 테이블과 매핑
public class User extends Timestamped implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략
    private Long id;

    @Column(unique = true) // 유니크 제약 조건: 중복된 이름 허용 안함
    private String name;

    @Column(unique = true) // 유니크 제약 조건: 중복된 이메일 허용 안함
    private String email;

    private String address; // 사용자 주소


    @Column(unique = true) // 유니크 제약 조건: 중복된 전화번호 허용 안함
    private String phone; // 전화번호

    @JsonIgnore
    @Column(nullable = false) // 비밀번호는 null 값을 허용하지 않음
    private String password;

    @JsonIgnore
    @Builder.Default
    private boolean isDeleted = false;


    /**
     * 사용자 객체 생성자 (추가 생성자)
     * @param encodedPassword 암호화된 비밀번호
     * @param name 사용자 이름
     */
    public User(String encodedPassword, String name) {
        this.name = name;
        this.password = encodedPassword;
    }

    /**
     * 두 객체가 동일한 사용자 객체인지 비교
     * @param object 비교 대상 객체
     * @return 동일 객체인지 여부
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) { // 동일 객체일 경우 true 반환
            return true;
        }
        if (object == null || Hibernate.getClass(this) != Hibernate.getClass(object)) { // 클래스 타입 다르면 false
            return false;
        }
        User user = (User) object;
        return id != null && Objects.equals(id, user.id); // ID가 같으면 동일 객체로 간주
    }

    /**
     * 객체의 고유 해시코드 반환
     * @return 해시코드 값
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * 비밀번호 검증
     * @param passwordEncoder 비밀번호 인코더
     * @param password 사용자 입력 비밀번호
     * @return 비밀번호가 일치하는지 여부
     */
    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        boolean isValid = passwordEncoder.matches(password, this.password);
        log.info("비밀번호 검증 결과: {}", isValid);
        return isValid;
    }

    /**
     * 사용자 비밀번호 변경
     * @param password 새 비밀번호
     */
    public void updateUserPassword(String password) {
        this.password = password;
        log.info("사용자 비밀번호가 변경되었습니다. ID: {}", id);
    }

    /**
     * 사용자 이름 변경
     * @param name 새 이름
     */
    public void update(String name) {
        this.name = name;
        log.info("사용자 이름이 변경되었습니다. ID: {}, 새 이름: {}", id, name);
    }
}
