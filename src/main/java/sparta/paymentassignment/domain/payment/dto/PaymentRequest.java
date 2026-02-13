package sparta.paymentassignment.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    private Long orderId;

    @JsonProperty("merchant_uid")
    private String orderNumber;

    @JsonProperty("totalAmount")
    private BigDecimal amount;

    private String orderName;

    @JsonProperty("pointToUse")
    private BigDecimal usedPoint = BigDecimal.ZERO;
}
