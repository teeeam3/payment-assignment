package sparta.paymentassignment.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.product.entity.Product;
import sparta.paymentassignment.product.entity.ProductStatus;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByStatus(ProductStatus status);
}
