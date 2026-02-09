package sparta.paymentassignment.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.order.entity.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderSummaryResponse {

    private final Long orderId;
    private final String orderNumber;
    private final Long totalAmount;
    private final OrderStatus status;
}
