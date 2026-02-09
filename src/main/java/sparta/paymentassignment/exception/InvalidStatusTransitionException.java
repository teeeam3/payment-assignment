package sparta.paymentassignment.exception;

import sparta.paymentassignment.domain.payment.PaymentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(PaymentStatus current, PaymentStatus target) {
    super("잘못된 상태 전환: " + current.toString() + " 에서 " + target.toString() + "로");
  }
}
