package sparta.paymentassignment.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.product.dto.ProductDetailResponse;
import sparta.paymentassignment.product.dto.ProductSummaryResponse;
import sparta.paymentassignment.product.entity.Product;
import sparta.paymentassignment.product.entity.ProductStatus;
import sparta.paymentassignment.product.excption.InvalidProductStatusException;
import sparta.paymentassignment.product.excption.ProductNotFoundException;
import sparta.paymentassignment.product.repository.ProductRepository;

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
}
