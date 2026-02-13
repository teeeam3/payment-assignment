package sparta.paymentassignment.domain.product.excption;

import org.springframework.http.HttpStatus;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.ServiceException;

public class InsufficientStockException extends ServiceException {
    public InsufficientStockException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }
}
