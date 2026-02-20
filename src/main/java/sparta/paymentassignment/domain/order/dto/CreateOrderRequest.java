package sparta.paymentassignment.domain.order.dto;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateOrderRequest {

    private final Long userId;
    private final List<OrderItemRequest> items;

    public CreateOrderRequest(Long userId, List<OrderItemRequest> items) {
        this.userId = userId;
        this.items = new ArrayList<>(items);
    }
}
