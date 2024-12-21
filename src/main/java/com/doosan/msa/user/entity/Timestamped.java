package com.doosan.msa.user.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 공통적으로 사용하는 생성일자와 수정일자 필드를 관리하기 위한 추상 클래스
 * JPA Auditing 기능을 활용하여 엔티티의 생성 및 수정 시간을 자동으로 기록
 */
@Slf4j
@Getter
@MappedSuperclass // 다른 엔티티 클래스에서 상속받아 사용할 수 있도록 설정
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public abstract class Timestamped {

    @JsonSerialize(using = LocalDateTimeSerializer.class) // LocalDateTime을 JSON으로 직렬화할 때 포맷 지정
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // JSON에서 LocalDateTime으로 역직렬화할 때 포맷 지정
    @CreatedDate // 엔티티가 생성될 때 자동으로 현재 시간이 기록됨
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @LastModifiedDate // 엔티티가 수정될 때 자동으로 현재 시간이 기록됨
    private LocalDateTime modifiedAt;

    /**
     * 수정 시간 설정 메서드
     * @param now 현재 시간
     */
    protected void setModifiedAt(LocalDateTime now) {
        log.info("수정 시간 설정: {}", now);
        this.modifiedAt = now;
    }

    /**
     * 생성 시간 로깅
     */
    public void logCreationTimestamp() {
        log.info("엔티티 생성 시간: {}", createdAt);
    }

    /**
     * 수정 시간 로깅
     */
    public void logModificationTimestamp() {
        log.info("엔티티 수정 시간: {}", modifiedAt);
    }
}
