package sparta.paymentassignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.paymentassignment.common.dto.ApiResponse;
import sparta.paymentassignment.dto.order.CreateOrderRequest;
import sparta.paymentassignment.dto.order.CreateOrderResponse;
import sparta.paymentassignment.dto.order.OrderCreateResult;
import sparta.paymentassignment.dto.order.OrderDetailResponse;
import sparta.paymentassignment.dto.order.OrderSummaryResponse;
import sparta.paymentassignment.order.dto.*;
import sparta.paymentassignment.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    // 출력용 dto 설계 Result
    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResult>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResponse response = orderService.createOrder(request);

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
