package sparta.paymentassignment.domain.order.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

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
