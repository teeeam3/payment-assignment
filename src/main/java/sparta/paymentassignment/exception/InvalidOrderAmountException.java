package sparta.paymentassignment.exception;

import java.math.BigDecimal;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(BigDecimal amount) {
        super("유효하지 않은 주문 금액입니다. amount=" + amount);
    }
}
