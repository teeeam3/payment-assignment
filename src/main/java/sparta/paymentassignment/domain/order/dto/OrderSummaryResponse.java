package sparta.paymentassignment.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long orderId;
    private String orderNumber;
    private Long totalAmount;
    private OrderStatus status;
}
