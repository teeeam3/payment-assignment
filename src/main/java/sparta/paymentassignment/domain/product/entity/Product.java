package sparta.paymentassignment.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long price;

    @Column(nullable = false)
    private int stock;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private String category;

    public Product(String name, Long price, int stock, String description, ProductStatus status, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.status = status;
        this.category = category;
    }

  public void updateStock(int newStock) {
    this.stock = newStock;
  }
}
