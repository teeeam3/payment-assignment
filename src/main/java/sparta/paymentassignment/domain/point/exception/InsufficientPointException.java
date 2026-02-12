package sparta.paymentassignment.domain.point.exception;

public class InsufficientPointException extends RuntimeException {

  public InsufficientPointException(String message) {
    super(message);
  }
}
