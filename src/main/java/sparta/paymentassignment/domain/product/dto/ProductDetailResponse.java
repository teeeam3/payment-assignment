package sparta.paymentassignment.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.product.entity.ProductStatus;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final int stock;
    private final String description;
    private final ProductStatus status;
    private final String category;

}
