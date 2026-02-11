package sparta.paymentassignment.domain.webhook.exception;

public class TotalAmountNotEqualException extends RuntimeException {

  public TotalAmountNotEqualException(String message) {
    super(message);
  }
}
