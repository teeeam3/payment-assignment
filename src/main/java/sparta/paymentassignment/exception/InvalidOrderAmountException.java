package sparta.paymentassignment.exception;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(Long amount) {
        super("유효하지 않은 주문 금액입니다. amount=" + amount);
    }
}
