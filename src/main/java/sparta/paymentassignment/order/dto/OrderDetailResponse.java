package sparta.paymentassignment.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.order.entity.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {
    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final Long totalAmount;
    private final OrderStatus status;
    private final LocalDateTime orderedAt;

}
