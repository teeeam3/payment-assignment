package sparta.paymentassignment.product.excption;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("상품이 존재하지 않습니다."+productId);
    }
}
