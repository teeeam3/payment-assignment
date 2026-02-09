package sparta.paymentassignment.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

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
