package com.doosan.msa.order.controller;

import com.doosan.msa.order.dto.requestDTO.OrderRequestDTO;
import com.doosan.msa.order.dto.requestDTO.WishListRequestDTO;
import com.doosan.msa.order.dto.responseDTO.OrderResponseDTO;
import com.doosan.msa.order.dto.responseDTO.WishListResponseDTO;
import com.doosan.msa.order.service.OrderService;
import com.doosan.msa.order.dto.responseDTO.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 *  주문 컨트롤러
 */
@RestController
@RequestMapping("/v1/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    // 주문하기
    @PostMapping
    public ResponseEntity<ResponseDTO<Map<String, Object>>> createOrder( HttpServletRequest request,@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        Map<String, Object> responseData = orderService.createOrder(request, orderRequestDTO);
        return ResponseEntity.status(201).body(
                ResponseDTO.success(201, "ORDER_CREATED", "시험 세션 주문이 성공적으로 완료 되었습니다.", responseData)
        );
    }

    // 사용자 주문 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUser(@PathVariable String userId) {
        logger.info("사용자 ID: {}의 주문 조회 요청", userId);
        List<OrderResponseDTO> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", orders));
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        logger.info("주문 ID: {} 취소 요청", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 강의 책 주문 배송 시작
    @PostMapping("/{orderId}/shipment/start")
    public ResponseEntity<Map<String, Object>> startShipment(@PathVariable Long orderId) {
        orderService.startShipment(orderId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Shipment started"));
    }

    // 강의 책 주문 배송 완료
    @PostMapping("/{orderId}/shipment/complete")
    public ResponseEntity<Map<String, Object>> completeShipment(@PathVariable Long orderId) {
        orderService.completeShipment(orderId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Shipment completed"));
    }

    // 반품
    @PostMapping("/{orderId}/return")
    public ResponseEntity<Map<String, Object>> returnOrder(@PathVariable Long orderId) {
        logger.info("반품 요청: 주문 ID {}", orderId);
        orderService.returnOrder(orderId);
        return ResponseEntity.ok(Map.of("success", true, "message", "반품이 완료되었습니다."));
    }

    // WishList 등록
    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, Object>> addToWishList(@RequestBody @Valid WishListRequestDTO request) {
        logger.info("위시리스트 추가 요청: {}", request);
        Long wishListId = orderService.addToWishList(request);
        return ResponseEntity.status(201).body(Map.of( "success", true, "message", "위시리스트에 추가되었습니다.","wishListId", wishListId ));
    }

    // WishList 조회
    @GetMapping("/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> getWishListByUser(@PathVariable String userId) {
        logger.info("사용자 ID: {}의 위시리스트 조회 요청", userId);
        List<WishListResponseDTO> wishLists = orderService.getWishListByUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", wishLists));
    }

    // WishList 삭제
    @DeleteMapping("/wishlist/{wishListId}")
    public ResponseEntity<Map<String, Object>> removeFromWishList(@PathVariable Long wishListId) {
        logger.info("위시리스트 ID: {} 삭제 요청", wishListId);
        orderService.removeFromWishList(wishListId);
        return ResponseEntity.ok(Map.of("success", true, "message", "위시리스트에서 삭제되었습니다."));
    }
}