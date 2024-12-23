package com.doosan.msa.order.repository;

import com.doosan.msa.order.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WishListRepository 인터페이스
 */
@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    // 유저아이디로 위시리스트 조회
    List<WishList> findByUserId(String userId);
}
