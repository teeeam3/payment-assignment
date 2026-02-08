package sparta.paymentassignment.order.dto;

import lombok.Getter;

@Getter
public class CreateOrderRequest {

    private Long customerId;
    private Long totalAmount;
}
