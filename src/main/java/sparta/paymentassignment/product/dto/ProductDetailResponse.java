package sparta.paymentassignment.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.product.entity.ProductStatus;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    private Long id;
    private String name;
    private Long price;
    private int stock;
    private String description;
    private ProductStatus status;
    private String category;

}
