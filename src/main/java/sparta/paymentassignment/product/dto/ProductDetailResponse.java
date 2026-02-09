package sparta.paymentassignment.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.product.entity.ProductStatus;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    private final Long id;
    private final String name;
    private final Long price;
    private final int stock;
    private final String description;
    private final ProductStatus status;
    private final String category;

}
