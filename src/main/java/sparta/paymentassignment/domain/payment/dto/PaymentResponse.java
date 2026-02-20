package sparta.paymentassignment.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponse {
  // 엔티티의 portonePaymentId 값
  private String paymentId;

  // 결제 금액 재확인
  private BigDecimal totalAmount;

  private String orderName;

  private boolean success;

  private String status;

  private String currency;
}