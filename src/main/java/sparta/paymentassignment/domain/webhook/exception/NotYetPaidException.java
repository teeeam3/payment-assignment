package sparta.paymentassignment.domain.webhook.exception;

public class NotYetPaidException extends RuntimeException {

  public NotYetPaidException(String message) {
    super(message);
  }
}
