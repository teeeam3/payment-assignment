package sparta.paymentassignment.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    //장지혁님의 Order와 연결하기 위한 정보
    private Long orderId;
    private String orderNumber;

    // 결제할 총 금액
    private BigDecimal amount;
}
