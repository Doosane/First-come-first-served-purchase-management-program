package com.doosan.msa.order.service;

import com.doosan.msa.common.exception.BusinessLogicException;
import com.doosan.msa.common.exception.TokenInvalidException;
import com.doosan.msa.common.jwt.TokenProvider;
import com.doosan.msa.common.shared.Authority;
import com.doosan.msa.common.util.AESUtil;
import com.doosan.msa.exam.entity.ExamSession;
import com.doosan.msa.exam.repository.ExamSessionRepository;
import com.doosan.msa.order.dto.requestDTO.OrderRequestDTO;
import com.doosan.msa.order.dto.requestDTO.WishListRequestDTO;
import com.doosan.msa.order.dto.responseDTO.OrderResponseDTO;
import com.doosan.msa.order.dto.responseDTO.WishListResponseDTO;
import com.doosan.msa.order.entity.BookStatus;
import com.doosan.msa.order.entity.ExamBook;
import com.doosan.msa.order.entity.Order;
import com.doosan.msa.order.entity.WishList;
import com.doosan.msa.order.repository.ExamBookRepository;
import com.doosan.msa.order.repository.OrderRepository;
import com.doosan.msa.order.repository.WishListRepository;
import com.doosan.msa.user.entity.User;
import com.doosan.msa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주문 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ExamSessionRepository examSessionRepository;
    private final ExamBookRepository examBookRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository; // 사용자 레포지토리
    private final WishListRepository wishListRepository; // 위시리스트 레포지토리

    // 주문하기
    @Transactional
    public Map<String, Object> createOrder(HttpServletRequest request, OrderRequestDTO orderRequestDTO) {
        try {
            log.info("주문하기 시작: {}", orderRequestDTO);

            // 토큰 유효성 검증
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
            if (user.getAuthority().equals(Authority.ROLE_INSTRUCTOR)) {
                throw new TokenInvalidException("강사는 자신의 강의를 주문 할 수 없습니다.");
            }

            //시험 세션 확인
            ExamSession session = examSessionRepository.findById(orderRequestDTO.getExamSessionId())
                    .orElseThrow(() -> new EntityNotFoundException("시험 세션을 찾을 수 없습니다."));
            log.debug("시험 세션 확인 완료: {}", session);

            // 중복 주문 체크
            boolean exists = orderRepository.existsByUserIdAndExamSessionId(orderRequestDTO.getUserId(), orderRequestDTO.getExamSessionId());
            log.info("중복 주문 체크 결과 (userId: {}, examSessionId: {}): {}", orderRequestDTO.getUserId(), orderRequestDTO.getExamSessionId(), exists);
            if (exists) {
                throw new IllegalArgumentException("이미 동일한 시험 세션에 대해 주문이 존재합니다.");
            }

            // 주문 생성
            Order order = new Order();
            order.setExamSession(session);
            order.setUserId(orderRequestDTO.getUserId());
            order.setStatus("CONFIRMED");
            order.setOrderDate(LocalDate.now());
            Order savedOrder = orderRepository.save(order);
            log.info("주문 생성 완료: 주문 ID: {}, 사용자 ID: {}, 시험 세션 ID: {}", savedOrder.getId(), savedOrder.getUserId(), session.getId());

            // 배송 정보 생성
            ExamBook examBook = new ExamBook();
            examBook.setOrder(savedOrder);
            examBook.setExamSession(session);
            examBook.setUserId(orderRequestDTO.getUserId());
            examBook.setStatus(BookStatus.BOOKED);
            examBook.setBookingDate(LocalDate.now());
            examBook.setDeliveryDate(LocalDate.now().plusDays(2)); // 주문 생성 날짜 + 2일로 설정
            examBookRepository.save(examBook);

            log.info("배송 정보 생성 완료: ExamBook ID: {}, 주문 ID: {}", examBook.getId(), savedOrder.getId());
            return Map.of("orderId", savedOrder.getId());
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 배송 시작
    @Transactional
    public void startShipment(Long orderId) {
        ExamBook examBook = examBookRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Exam book을 찾을 수 없습니다."));

        if (examBook.getStatus() != BookStatus.BOOKED) {
            throw new BusinessLogicException(400, "INVALID_SHIPMENT_STATUS", "배송이 준비 상태가 아닙니다.");
        }

        examBook.setStatus(BookStatus.DELIVERED);
        examBook.setDeliveryDate(LocalDate.now().plusDays(2)); // 배송일
        examBookRepository.save(examBook);

        log.info("배송이 시작된 주문 ID: {}", orderId);
    }

    // 배송 완료
    @Transactional
    public void completeShipment(Long orderId) {
        ExamBook examBook = examBookRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Exam book을 찾을 수 없습니다."));

        if (examBook.getStatus() != BookStatus.DELIVERED) {
            throw new BusinessLogicException(400, "INVALID_SHIPMENT_STATUS", "배송 정보가 없습니다.");
        }

        examBook.setStatus(BookStatus.DELIVERED);
        examBookRepository.save(examBook);

        log.info("배송이 완료된 주문 ID: {}", orderId);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUser(String userId) {
        log.info("사용자 ID: {}의 주문 조회", userId);

        List<Order> orders = orderRepository.findByUserId(userId);

        // exam_session_id를 기준으로 중복 제거
        Map<Long, Order> uniqueOrders = orders.stream()
                .collect(Collectors.toMap(
                        order -> order.getExamSession().getId(), // exam_session_id 기준으로
                        order -> order, // 중복되지 않은 주문
                        (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
                ));

        return uniqueOrders.values().stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getStatus(),
                        order.getOrderDate(),
                        order.getDeliveryDate()
                ))
                .collect(Collectors.toList());
    }

    // 주문 취소 로직
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("주문 ID: {} 취소 요청", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        if (!order.getStatus().equals("CONFIRMED")) {
            throw new RuntimeException("이 주문을 취소 할 수 없습니다.");
        }

        // 배송 상태 확인
        ExamBook examBook = examBookRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Exam book을 찾을 수 없습니다."));

        if (examBook.getStatus() != BookStatus.BOOKED) {
            throw new RuntimeException("이 주문을 취소 할 수 없습니다. 이미 배송이 시작되었습니다.");
        }

        // 주문 취소 처리
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        log.info("주문 ID: {} 취소 완료", orderId);
    }

    // 반품 로직
    @Transactional
    public void returnOrder(Long orderId) {
        log.info("반품 요청: 주문 ID: {}", orderId);

        ExamBook examBook = examBookRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Exam book을 찾을 수 없습니다."));

        if (examBook.getStatus() != BookStatus.DELIVERED) {
            throw new RuntimeException("이 주문을 반품 할 수 없습니다. 아직 배송하지 않았습니다.");
        }

        // D+1 반품 가능 조건 확인
        if (examBook.getDeliveryDate().isBefore(LocalDate.now().minusDays(1))) {
            throw new RuntimeException("반품 가능 기간이 이미 지났습니다.");
        }

        // 반품 처리
        examBook.setStatus(BookStatus.RETURNED);
        examBookRepository.save(examBook);

        log.info("반품 완료: 주문 ID: {}", orderId);
    }

    // WishList 등록
    @Transactional
    public Long addToWishList(WishListRequestDTO request) {
        log.info("위시리스트 등록 요청 : {}", request);

        ExamSession session = examSessionRepository.findById(request.getExamSessionId())
                .orElseThrow(() -> new RuntimeException("시험 세션을 찾을 수 없습니다."));

        WishList wishList = new WishList();
        wishList.setExamSession(session);
        wishList.setUserId(request.getUserId());

        WishList savedWishList = wishListRepository.save(wishList);
        log.info("위시리스트 추가 완료. ID: {}", savedWishList.getId());
        return savedWishList.getId();
    }

    @Transactional(readOnly = true)
    public List<WishListResponseDTO> getWishListByUser(String userId) {
        log.info("사용자 ID: {}의 위시리스트 조회", userId);

        List<WishList> wishLists = wishListRepository.findByUserId(userId);

        return wishLists.stream()
                .map(wishList -> new WishListResponseDTO(
                        wishList.getId(),
                        wishList.getExamSession().getName(),
                        wishList.getExamSession().getCategory()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFromWishList(Long wishListId) {
        log.info("위시리스트 ID: {} 삭제 요청", wishListId);

        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new RuntimeException("위시리스트를 찾을 수 없습니다."));

        wishListRepository.delete(wishList);
        log.info("위시리스트 ID: {} 삭제 완료", wishListId);
    }
}