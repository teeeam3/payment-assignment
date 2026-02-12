package sparta.paymentassignment.domain.webhook;

import java.util.Arrays;

public enum TransactionType {
  PAID("Transaction.Paid"),
  CANCELLED("Transaction.Cancelled"),
  FAILED("Transaction.Failed"),
  UNKNOWN("UNKNOWN");

  private final String portOneTransactionType;

  TransactionType(String portOneTransactoinType) {
    this.portOneTransactionType = portOneTransactoinType;
  }

  public static TransactionType fromPortOneTransactionType(String portOneTransactoinType) {
    return Arrays.stream(values())
        .filter(type -> type.portOneTransactionType.equals(portOneTransactoinType))
        .findFirst()
        .orElse(TransactionType.UNKNOWN);
  }
}
