package sparta.paymentassignment.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderSummaryResponse {

    private final Long orderId;
    private final String orderNumber;
    private final Long totalAmount;
    private final OrderStatus status;
    private final String currency = "krw";
}
