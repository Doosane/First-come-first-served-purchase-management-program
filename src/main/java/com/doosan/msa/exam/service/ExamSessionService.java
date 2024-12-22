package com.doosan.msa.exam.service;

import com.doosan.msa.exam.dto.requestDTO.ExamSessionRequestDTO;
import com.doosan.msa.exam.dto.responseDTO.ExamSessionResponseDTO;
import com.doosan.msa.exam.entity.Status;
import com.doosan.msa.exam.entity.ExamSession;
import com.doosan.msa.exam.repository.ExamSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public List<ExamSessionResponseDTO> getAllSessions() {
        log.info("모든 시험 세션 조회");
        List<ExamSession> sessions = examSessionRepository.findAllWithStatus(); // n+1 문제 해결 JPQL로 변경 join fetch를 활용한 데이터 로딩 적용

        // Lazy 컬렉션 명시적 초기화
        sessions.forEach(session -> session.getStatus().size());

        log.info("조회된 세션 수: {}", sessions.size());
        return sessions.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // 서비스 계층에서 트랜잭션을 확장하여 Lazy Loading 문제가 발생하지 않도록 보장
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

    public Long createSession(ExamSessionRequestDTO request) {
        log.info("새로운 시험 세션 생성 요청: {}", request);
        ExamSession session = new ExamSession();
        session.setCategory(request.getCategory());
        session.setName(request.getName());
        session.setOnline(request.isOnline());
        session.setSub(request.isSub()); // boolean 값을 그대로 설정

        for (ExamSessionRequestDTO.StatusDTO statusDto : request.getStatus()) {
            log.debug("세션 상태 추가: {}", statusDto);
            Status status = new Status();
            status.setCode(statusDto.getCode());
            status.setValue(statusDto.getValue());
            status.setExamSession(session);
            session.getStatus().add(status);
        }

        ExamSession savedSession = examSessionRepository.save(session);
        log.info("시험 세션 생성 완료. ID: {}", savedSession.getId());
        return savedSession.getId();
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

    @Transactional
    public void deleteSessionById(Long sessionId) {
        examSessionRepository.deleteById(sessionId);
        log.info("ID가 {}인 세션이 삭제되었습니다.", sessionId);
    }


    @Transactional
    public void updateSession(Long sessionId, ExamSessionRequestDTO request) {
        ExamSession session = examSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다: ID " + sessionId));

        session.setCategory(request.getCategory());
        session.setName(request.getName());
        session.setOnline(request.isOnline());
        session.setSub(request.isSub());

        // 상태 업데이트 로직 (필요한 경우 추가)
        session.getStatus().clear();
        for (ExamSessionRequestDTO.StatusDTO statusDto : request.getStatus()) {
            Status status = new Status();
            status.setCode(statusDto.getCode());
            status.setValue(statusDto.getValue());
            status.setExamSession(session);
            session.getStatus().add(status);
        }

        examSessionRepository.save(session);
        log.info("ID가 {}인 세션이 수정되었습니다.", sessionId);
    }

}
