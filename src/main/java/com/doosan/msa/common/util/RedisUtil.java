package com.doosan.msa.common.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final StringRedisTemplate redisTemplate; // Redis 접근 클래스

    /**
     * Redis에서 데이터를 조회
     * @param key Redis 키
     * @return Redis에서 조회된 값
     */
    public String getData(String key) {
        logger.info("getData 메서드 호출: key={}", key);
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            String value = valueOperations.get(key);
            if (value != null) {
                logger.info("Redis에서 데이터 조회 성공: key={}, value={}", key, value);
            } else {
                logger.warn("Redis에서 데이터 조회 실패: key={} (값 없음)", key);
            }
            return value;
        } catch (Exception e) {
            logger.error("Redis 데이터 조회 중 예외 발생: key={}, message={}", key, e.getMessage(), e);
            throw e; // 필요시 예외 재발생
        } finally {
            logger.info("getData 메서드 종료: key={}", key);
        }
    }

    /**
     * Redis에 데이터를 저장
     * @param key Redis 키
     * @param value 저장할 값
     */
    public void setData(String key, String value) {
        logger.info("setData 메서드 호출: key={}, value={}", key, value);
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
            logger.info("Redis에 데이터 저장 완료: key={}, value={}", key, value);
        } catch (Exception e) {
            logger.error("Redis 데이터 저장 중 예외 발생: key={}, value={}, message={}", key, value, e.getMessage(), e);
            throw e;
        } finally {
            logger.info("setData 메서드 종료: key={}, value={}", key, value);
        }
    }

    /**
     * Redis에 데이터를 저장하고 만료 시간을 설정
     * @param key Redis 키
     * @param value 저장할 값
     * @param duration 만료 시간 (초)
     */
    public void setDataExpire(String key, String value, long duration) {
        logger.info("setDataExpire 메서드 호출: key={}, value={}, duration={}초", key, value, duration);
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
            logger.info("Redis에 데이터 저장 및 만료 시간 설정 완료: key={}, value={}, duration={}초", key, value, duration);
        } catch (Exception e) {
            logger.error("Redis 데이터 저장 및 만료 시간 설정 중 예외 발생: key={}, value={}, duration={}, message={}",
                    key, value, duration, e.getMessage(), e);
            throw e;
        } finally {
            logger.info("setDataExpire 메서드 종료: key={}, value={}, duration={}초", key, value, duration);
        }
    }

    /**
     * Redis에서 데이터를 삭제
     * @param key Redis 키
     */
    public void deleteData(String key) {
        logger.info("deleteData 메서드 호출: key={}", key);
        try {
            boolean isDeleted = redisTemplate.delete(key);
            if (isDeleted) {
                logger.info("Redis에서 데이터 삭제 성공: key={}", key);
            } else {
                logger.warn("Redis에서 데이터 삭제 실패: key={} (존재하지 않거나 이미 삭제됨)", key);
            }
        } catch (Exception e) {
            logger.error("Redis 데이터 삭제 중 예외 발생: key={}, message={}", key, e.getMessage(), e);
            throw e;
        } finally {
            logger.info("deleteData 메서드 종료: key={}", key);
        }
    }
}
