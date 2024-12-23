package com.doosan.msa.exam.service;
import com.doosan.msa.common.jwt.TokenProvider;
import com.doosan.msa.common.shared.Authority;
import com.doosan.msa.common.util.AESUtil;
import com.doosan.msa.exam.dto.requestDTO.ExamSessionRequestDTO;
import com.doosan.msa.exam.dto.responseDTO.ExamSessionResponseDTO;
import com.doosan.msa.exam.entity.Status;
import com.doosan.msa.exam.entity.ExamSession;
import com.doosan.msa.exam.repository.ExamSessionRepository;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.doosan.msa.common.exception.TokenInvalidException;
import javax.servlet.http.HttpServletRequest;

/**
 * ExamSessionService 클래스
 * - 시험 세션과 관련된 비즈니스 로직을 처리하는 서비스 클래스
 * - 시험 세션 생성, 조회 등 주요 기능을 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamSessionService {

    private final ExamSessionRepository examSessionRepository;
    private final TokenProvider tokenProvider; // 주입 추가
    private final UserRepository userRepository; // UserRepository 필드 추가

    // 모든 시험 세션 조회
    @Transactional
    public List<ExamSessionResponseDTO> getAllSessions() {
        List<ExamSession> sessions = examSessionRepository.findAllWithStatus(); // n+1 문제 해결 JPQL로 변경 join fetch를 활용한 데이터 로딩 적용

        // Lazy 컬렉션 명시적 초기화
        sessions.forEach(session -> session.getStatus().size());

        log.info("조회된 세션 수: {}", sessions.size());
        return sessions.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 특정 시험 세션 조회
    @Transactional(readOnly = true)
    public ExamSessionResponseDTO getSessionById(Long sessionId) {
        log.info("시험 세션 ID: {} 조회 요청", sessionId);
        ExamSession session = examSessionRepository.findByIdWithStatus(sessionId)
                .orElseThrow(() -> {
                    log.error("시험 세션 ID: {}를 찾을 수 없음", sessionId);
                    return new RuntimeException("Exam session not found");
                });
        log.info("시험 세션 ID: {} 조회 성공", sessionId);
        return toResponseDto(session);
    }

    // 세션 생성
    @Transactional
    public Map<String, Object> createSession(HttpServletRequest request, ExamSessionRequestDTO sessionRequestDTO) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenInvalidException("유효하지 않은 Authorization 헤더 형식입니다.");
        }

        String token = authorizationHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new TokenInvalidException("유효하지 않은 토큰입니다.");
        }

        // JWT에서 이메일 추출 및 암호화
        String email = tokenProvider.getUserIdFromToken(token);
        String encryptedEmail = AESUtil.encrypt(email);

        // 사용자 조회
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 권한 확인
        if (!user.getAuthority().equals(Authority.ROLE_INSTRUCTOR)) {
            throw new TokenInvalidException("세션을 생성할 권한이 없습니다.");
        }

        ExamSession session = new ExamSession();
        session.setCategory(sessionRequestDTO.getCategory());
        session.setName(sessionRequestDTO.getName());
        session.setOnline(sessionRequestDTO.isOnline());
        session.setSub(sessionRequestDTO.isSub());

        for (ExamSessionRequestDTO.StatusDTO statusDto : sessionRequestDTO.getStatus()) {
            Status status = new Status();
            status.setCode(statusDto.getCode());
            status.setValue(statusDto.getValue());
            status.setExamSession(session);
            session.getStatus().add(status);
        }

        ExamSession savedSession = examSessionRepository.save(session);
        log.info("시험 세션 생성 완료. ID: {}", savedSession.getId());

        return Map.of(
                "id", savedSession.getId(),
                "name", savedSession.getName(),
                "category", savedSession.getCategory(),
                "online", savedSession.getOnline(),
                "sub", savedSession.isSub(),
                "createdAt", LocalDateTime.now()
        );
    }



    private ExamSessionResponseDTO toResponseDto(ExamSession session) {
        log.debug("시험 세션 변환 중: {}", session.getId());
        return new ExamSessionResponseDTO(
                session.getCategory(),
                session.getId().toString(),
                session.getName(),
                session.getOnline(),
                session.isSub(),
                session.getStatus().stream()
                        .map(status -> new ExamSessionResponseDTO.StatusDTO(status.getCode(), status.getValue()))
                        .collect(Collectors.toList())
        );
    }

    // 특정 세션 삭제 api
    @Transactional
    public void deleteSessionById(HttpServletRequest request, Long sessionId) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenInvalidException("유효하지 않은 Authorization 헤더 형식입니다.");
        }

        String token = authorizationHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new TokenInvalidException("유효하지 않은 토큰입니다.");
        }

        // JWT에서 이메일 추출 및 암호화
        String email = tokenProvider.getUserIdFromToken(token);
        String encryptedEmail = AESUtil.encrypt(email);

        // 사용자 조회
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 권한 확인
        if (!user.getAuthority().equals(Authority.ROLE_INSTRUCTOR)) {
            throw new TokenInvalidException("세션을 삭제할 권한이 없습니다.");
        }

        // 세션 삭제
        examSessionRepository.deleteById(sessionId);
        log.info("ID가 {}인 세션이 삭제되었습니다.", sessionId);
    }


    // 특정 세션 수정
    @Transactional
    public Map<String, Object> updateSession(HttpServletRequest request, Long sessionId, ExamSessionRequestDTO sessionRequestDTO) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenInvalidException("유효하지 않은 Authorization 헤더 형식입니다.");
        }

        String token = authorizationHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new TokenInvalidException("유효하지 않은 토큰입니다.");
        }

        // JWT에서 이메일 추출 및 암호화
        String email = tokenProvider.getUserIdFromToken(token);
        String encryptedEmail = AESUtil.encrypt(email);

        // 사용자 조회
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 권한 확인
        if (!user.getAuthority().equals(Authority.ROLE_INSTRUCTOR)) {
            throw new TokenInvalidException("세션을 수정할 권한이 없습니다.");
        }

        // 세션 조회 및 업데이트
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: ID " + sessionId));
        session.setCategory(sessionRequestDTO.getCategory());
        session.setName(sessionRequestDTO.getName());
        session.setOnline(sessionRequestDTO.isOnline());
        session.setSub(sessionRequestDTO.isSub());

        // 상태 업데이트 로직
        session.getStatus().clear();
        for (ExamSessionRequestDTO.StatusDTO statusDto : sessionRequestDTO.getStatus()) {
            Status status = new Status();
            status.setCode(statusDto.getCode());
            status.setValue(statusDto.getValue());
            status.setExamSession(session);
            session.getStatus().add(status);
        }
        examSessionRepository.save(session);
        log.info("ID가 {}인 세션이 수정되었습니다.", sessionId);

        // 반환할 데이터 구성
        return Map.of(
                "id", sessionId,
                "name", sessionRequestDTO.getName(),
                "category", sessionRequestDTO.getCategory(),
                "online", sessionRequestDTO.isOnline(),
                "sub", sessionRequestDTO.isSub(),
                "updatedAt", LocalDateTime.now()
        );
    }
}