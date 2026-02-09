package sparta.paymentassignment.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.order.entity.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private Long totalAmount;
    private OrderStatus status;
    private LocalDateTime orderedAt;

}
