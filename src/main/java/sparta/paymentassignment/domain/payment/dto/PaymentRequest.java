package sparta.paymentassignment.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private BigDecimal usePoint;
}
