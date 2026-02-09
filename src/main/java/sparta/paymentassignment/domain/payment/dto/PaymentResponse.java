package sparta.paymentassignment.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    // 엔티티의 portonePaymentId 값
    private String portonePaymentId;

    // 결제 금액 재확인
    private BigDecimal amount;
}