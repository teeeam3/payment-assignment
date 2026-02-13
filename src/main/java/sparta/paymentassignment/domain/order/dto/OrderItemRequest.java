package sparta.paymentassignment.domain.order.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemRequest {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
