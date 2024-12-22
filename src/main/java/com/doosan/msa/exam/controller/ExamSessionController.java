package com.doosan.msa.exam.controller;

import com.doosan.msa.exam.dto.requestDTO.ExamSessionRequestDTO;
import com.doosan.msa.exam.dto.responseDTO.ExamSessionResponseDTO;
import com.doosan.msa.exam.service.ExamSessionService;
import com.doosan.msa.exam.dto.responseDTO.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 시험 세션 관련 API 컨트롤러 클래스
 * 사용자의 요청을 처리하고 비즈니스 로직 호출 후 결과를 반환
 */
@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ExamSessionController {

    private static final Logger logger = LoggerFactory.getLogger(ExamSessionController.class);

    private final ExamSessionService examSessionService;

    @GetMapping("/sessions")
    public ResponseEntity<ResponseDTO<List<ExamSessionResponseDTO>>> getAllSessions() {
        logger.info("모든 세션 정보를 조회하는 요청을 받았습니다.");
        List<ExamSessionResponseDTO> sessions = examSessionService.getAllSessions();
        logger.info("총 {}개의 세션 데이터를 성공적으로 조회했습니다.", sessions.size());
        return ResponseEntity.ok(
                ResponseDTO.success(200, "SUCCESS", "세션 조회 성공", sessions)
        );
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ResponseDTO<ExamSessionResponseDTO>> getSession(@PathVariable Long sessionId) {
        try {
            ExamSessionResponseDTO session = examSessionService.getSessionById(sessionId);
            logger.info("ID가 {}인 세션 정보 조회 : {}", sessionId, session);
            return ResponseEntity.ok(
                    ResponseDTO.success(200, "SUCCESS", "세션 조회 성공", session)
            );
        } catch (Exception e) {
            logger.error("ID가 {}인 세션 정보를 조회하는 중 오류 발생", sessionId, e);
            return ResponseEntity.status(500).body(
                    ResponseDTO.fail(500, "INTERNAL_SERVER_ERROR", "세션 조회 중 문제가 발생했습니다.")
            );
        }
    }

    @PostMapping("/sessions")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> createSession(
            HttpServletRequest request,
            @RequestBody @Valid ExamSessionRequestDTO sessionRequestDTO) {

        Map<String, Object> data = examSessionService.createSession(request, sessionRequestDTO);

        return ResponseEntity.status(201).body(
                ResponseDTO.success(201, "SUCCESS", "새로운 세션이 성공적으로 생성되었습니다.", data)
        );
    }


    /**
     * 특정 세션을 삭제
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ResponseDTO<Void>> deleteSession(HttpServletRequest request, @PathVariable Long sessionId) {
        examSessionService.deleteSessionById(request, sessionId);
        return ResponseEntity.ok(
                ResponseDTO.success(200, "SUCCESS", "세션이 성공적으로 삭제되었습니다.", null)
        );
    }

    /**
     * 특정 세션의 내용을 수정
     */
    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> updateSession( HttpServletRequest request, @PathVariable Long sessionId, @RequestBody @Valid ExamSessionRequestDTO sessionRequestDTO) {
        Map<String, Object> data = examSessionService.updateSession(request, sessionId, sessionRequestDTO);
        return ResponseEntity.ok(
                ResponseDTO.success(200, "SUCCESS", "세션이 성공적으로 수정되었습니다.", data)
        );
    }
}