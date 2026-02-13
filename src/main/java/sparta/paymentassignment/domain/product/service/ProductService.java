package sparta.paymentassignment.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.product.dto.ProductDetailResponse;
import sparta.paymentassignment.domain.product.dto.ProductSummaryResponse;
import sparta.paymentassignment.domain.product.entity.Product;
import sparta.paymentassignment.domain.product.entity.ProductStatus;
import sparta.paymentassignment.domain.product.excption.InvalidProductStatusException;
import sparta.paymentassignment.domain.product.excption.ProductNotFoundException;
import sparta.paymentassignment.domain.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductSummaryResponse> getProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(product -> new ProductSummaryResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock()
                ))
                .toList();


    }
    // 상품 상세 조회
    public ProductDetailResponse getProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new InvalidProductStatusException(product.getStatus());
        }

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getStatus(),
                product.getCategory()
        );
    }

    @Transactional
    public void refillProduct(Long productId, Integer quantity) {
      Product product = productRepository.findById(productId)
          .orElseThrow(() -> new ProductNotFoundException(productId));
      int newStock = product.getStock() + quantity;
      product.updateStock(newStock);
    }

    public Product getProductForOrderItem (Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.deductingStock(quantity);
        return product;
    }
}
