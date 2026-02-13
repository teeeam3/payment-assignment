package sparta.paymentassignment.exception;

public class PaymentAmountMismatchException extends RuntimeException {
    public PaymentAmountMismatchException() {
        //포트원 금액과 서버의 금액이 다른경우
        super("결제 요청 금액과 실제 결제 금액이 일치하지 않습니다.");
    }

  public PaymentAmountMismatchException(String message) {
    super(message);
  }
}