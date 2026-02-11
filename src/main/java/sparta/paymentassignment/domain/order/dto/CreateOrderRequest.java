package sparta.paymentassignment.domain.order.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateOrderRequest {

    private final Long customerId;
    private final List<OrderItemRequest> items;

    public CreateOrderRequest(Long customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
    }
}
