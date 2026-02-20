package sparta.paymentassignment.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.product.entity.Product;
import sparta.paymentassignment.domain.product.entity.ProductStatus;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus status);
    Optional<Product> findById(Long id);
}
