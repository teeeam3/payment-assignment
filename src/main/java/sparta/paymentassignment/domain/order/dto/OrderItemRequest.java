package sparta.paymentassignment.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderItemRequest {

    private Long productId;
    private String productName;
    private Long price;
    private Integer quantity;
}
