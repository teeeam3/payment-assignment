package sparta.paymentassignment.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSummaryResponse {
    private final Long id;
    private final String name;
    private final Long price;
    private final int stock;
}
