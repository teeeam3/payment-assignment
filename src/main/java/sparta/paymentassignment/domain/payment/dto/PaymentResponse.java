package sparta.paymentassignment.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    @JsonProperty("paymentId")
    private String portonePaymentId;

    @JsonProperty("amount")
    private BigDecimal totalAmount;

    private String orderName;

    private boolean success;

}