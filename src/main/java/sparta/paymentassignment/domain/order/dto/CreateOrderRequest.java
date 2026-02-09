package sparta.paymentassignment.domain.order.dto;

import lombok.Getter;

@Getter
public class CreateOrderRequest {

    private final Long customerId;
    private final Long totalAmount;

    public CreateOrderRequest(Long customerId, Long totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }
}
