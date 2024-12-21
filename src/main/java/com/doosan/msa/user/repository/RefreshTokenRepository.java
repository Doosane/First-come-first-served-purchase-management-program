package com.doosan.msa.user.repository;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * RefreshTokenRepository 인터페이스
 * - JpaRepository를 상속받아 RefreshToken 엔티티 관련 데이터베이스 작업 처리
 * - 사용자와 관련된 리프레시 토큰 관리
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByValue(String value);

    void deleteByUserId(Long userId);



}