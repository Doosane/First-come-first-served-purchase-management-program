
package com.doosan.msa.user.service;

import static org.junit.jupiter.api.Assertions.*;
import com.doosan.msa.common.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AuthServiceTest {

    private static final String email = "test@example.com"; // 인증할 이메일
    private static final String authNum = "123456"; // 인증 번호
    private RedisUtil redisUtil; // RedisUtil Mock 객체
    private AuthService authService; // 테스트 대상 AuthService

    @BeforeEach
    void setUp() { // 테스트 초기화 - RedisUtil Mock 생성 및 AuthService에 주입
        redisUtil = mock(RedisUtil.class); // RedisUtil Mock 생성
        authService = new AuthService(redisUtil);  // AuthService에 Mock 주입
    }

    @Test
    void testCheckAuthNum_Success() {
        // 인증 번호(authNum)가 Redis에 저장되어 있고, 저장된 이메일과 입력된 이메일이 일치하는 경우를 검증
        // Mock Redis 동작 설정
        when(redisUtil.getData(authNum)).thenReturn(email);
        boolean result = authService.CheckAuthNum(email, authNum);
        assertTrue(result, "인증 번호가 유효할 경우 true를 반환해야 한다.");
        verify(redisUtil, times(1)).getData(authNum); // RedisUtil의 getData 메서드 호출 검증
    }

    // 인증 번호가 Redis에 저장되어 있지 않은 경우
    @Test
    void testCheckAuthNum_Failure_NoRedisData() {
        // Mock Redis 동작 설정 (null 반환)
        when(redisUtil.getData(authNum)).thenReturn(null);

        boolean result = authService.CheckAuthNum(email, authNum);

        assertFalse(result, "Redis에 인증 번호가 없을 경우 false를 반환해야 한다.");

        verify(redisUtil, times(1)).getData(authNum);
        // RedisUtil의 getData 메서드 호출 검증
    }

    @Test
    void testCheckAuthNum_Failure_EmailMismatch() {
        // 인증 번호(authNum)는 Redis에 저장되어 있지만, 저장된 이메일과 입력된 이메일이 일치하지 않는 경우를 검증
        // Mock Redis 동작 설정 (다른 이메일 반환)
        when(redisUtil.getData(authNum)).thenReturn("wrong@example.com");
        boolean result = authService.CheckAuthNum(email, authNum);
        assertFalse(result, "이메일이 일치하지 않을 경우 false를 반환해야 한다.");
        verify(redisUtil, times(1)).getData(authNum); // RedisUtil의 getData 메서드 호출 검증
    }
}