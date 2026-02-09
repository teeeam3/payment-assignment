package sparta.paymentassignment.exception;

public class InvalidOrderAmountException extends RuntimeException {
    public InvalidOrderAmountException(Long amount) {
        super("잘못된 주문 금액 입니다." + amount);
    }
}
