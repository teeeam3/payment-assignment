package sparta.paymentassignment.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException() {
        //결제 정보를 찾을수 없을때
        super("해당 결제 정보를 찾을 수 없습니다.");
    }
}