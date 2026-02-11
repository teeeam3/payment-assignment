package sparta.paymentassignment.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.paymentassignment.common.dto.ApiResponse;
import sparta.paymentassignment.domain.product.dto.ProductDetailResponse;
import sparta.paymentassignment.domain.product.dto.ProductSummaryResponse;
import sparta.paymentassignment.domain.product.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProducts() {

        return ResponseEntity.ok(ApiResponse.success(200, "상품 목록 조회 성공", productService.getProducts()));
    }
    // 상품 상세 조회 API
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(200, "상품 상세 조회 성공", productService.getProduct(productId)));
    }
}
