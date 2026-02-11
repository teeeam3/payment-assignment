package sparta.paymentassignment.domain.product.excption;

import sparta.paymentassignment.domain.product.entity.ProductStatus;

public class  InvalidProductStatusException extends RuntimeException {
    public InvalidProductStatusException(ProductStatus status) {
        super(switch (status) {
            case INACTIVE -> "판매 중지된 상품입니다.";
            case SOLD_OUT -> "품절된 상품입니다.";
            default -> "현재 상품 상태로는 처리할 수 없습니다. 상태: " + status;
        });
    }
}
