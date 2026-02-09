package sparta.paymentassignment.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSummaryResponse {
    private Long id;
    private String name;
    private Long price;
    private int stock;
}
