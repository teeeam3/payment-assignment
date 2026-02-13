package sparta.paymentassignment.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.order.OrderStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderSummaryResponse {

    private final Long orderId;
    private final String orderNumber; // 순수 주문번호 느낌생성이 40자로 제한되면
    private final String orderName; // 아이폰 15 외 4개
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final String currency = "KRW";
}
