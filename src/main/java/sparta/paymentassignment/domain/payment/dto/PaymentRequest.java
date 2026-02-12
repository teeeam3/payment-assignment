package sparta.paymentassignment.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private String orderNumber;
    @JsonProperty("totalAmount")
    private BigDecimal amount;
    private BigDecimal usePoint=BigDecimal.ZERO;
}
