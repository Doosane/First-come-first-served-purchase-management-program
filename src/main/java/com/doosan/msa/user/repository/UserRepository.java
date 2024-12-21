package com.doosan.msa.user.repository;

import com.doosan.msa.user.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * UserRepository 인터페이스
 * - JpaRepository를 상속받아 User 엔티티 관련 데이터베이스 작업 처리
 * - 사용자 정보를 이름 및 이메일로 조회 가능
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
}