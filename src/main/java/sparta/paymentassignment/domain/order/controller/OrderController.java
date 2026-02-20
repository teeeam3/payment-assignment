package sparta.paymentassignment.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.paymentassignment.common.dto.ApiResponse;
import java.util.List;
import sparta.paymentassignment.domain.order.dto.CreateOrderRequest;
import sparta.paymentassignment.domain.order.dto.CreateOrderResponse;
import sparta.paymentassignment.domain.order.dto.OrderCreateResult;
import sparta.paymentassignment.domain.order.dto.OrderDetailResponse;
import sparta.paymentassignment.domain.order.dto.OrderSummaryResponse;
import sparta.paymentassignment.domain.order.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    // 출력용 dto 설계 Result
    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResult>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        CreateOrderResponse response = orderService.createOrder(request, userId);

        return ResponseEntity.status(201).body(
                ApiResponse.success(
                        201,
                        "주문 생성이 완료되었습니다.",
                        new OrderCreateResult(response.getOrderId())
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getOrders(){
        return ResponseEntity.ok(ApiResponse.success(200,"주문 목록 조회 성공", orderService.getOrders()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(ApiResponse.success(200, "주문 상세 조회 성공", orderService.getOrderDetail(orderId)));
    }
}
