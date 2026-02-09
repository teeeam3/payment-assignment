package sparta.paymentassignment.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private Long orderId;
    private String orderNumber;
    private String status;

}
