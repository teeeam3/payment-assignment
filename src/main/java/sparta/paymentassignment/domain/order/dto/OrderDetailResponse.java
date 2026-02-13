package sparta.paymentassignment.domain.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {
    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final LocalDateTime orderedAt;
    private final List<OrderItemResponse> items;  // 🔥 필수

}
