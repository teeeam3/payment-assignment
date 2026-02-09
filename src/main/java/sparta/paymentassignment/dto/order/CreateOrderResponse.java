package sparta.paymentassignment.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private final Long orderId;
    private final String orderNumber;
    private final String status;

}
