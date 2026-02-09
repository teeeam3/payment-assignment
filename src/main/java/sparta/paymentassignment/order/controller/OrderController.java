package sparta.paymentassignment.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.paymentassignment.common.dto.ApiResponse;
import sparta.paymentassignment.order.dto.CreateOrderRequest;
import sparta.paymentassignment.order.dto.CreateOrderResponse;
import sparta.paymentassignment.order.service.OrderService;
import sparta.paymentassignment.order.dto.OrderCreateResult;

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
}
