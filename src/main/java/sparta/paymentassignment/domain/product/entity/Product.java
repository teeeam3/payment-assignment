package sparta.paymentassignment.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.paymentassignment.domain.product.excption.InsufficientStockException;
import sparta.paymentassignment.exception.ErrorCode;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private String category;

    public Product(String name, BigDecimal price, int stock, String description, ProductStatus status, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.status = status;
        this.category = category;
    }
    public void addStock(int quantity) {
        this.stock += quantity;
    }

    public void updateStock(int newStock) {
    this.stock = newStock;
    }

    public void validateStock(int quantity) {
        if (quantity > this.stock) {
            throw new InsufficientStockException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    // 재고 차감 도메인 메서드
    public void deductingStock(int quantity) {
        validateStock(quantity);
        this.stock -= quantity;
    }
}
