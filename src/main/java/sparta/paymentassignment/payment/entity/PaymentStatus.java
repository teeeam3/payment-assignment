package sparta.paymentassignment.payment.entity;

public enum PaymentStatus {
  PENDING, // 결제 대기 상태
  APPROVED, // 결제 완료 상태
  CANCELLED, // 결제 실패 상태
  REFUNDED; // 환불 완료 상태

  public boolean canMove(PaymentStatus target) {
    if (target == null) {
      return false;
    }

    // 결제 대기에서 결제 완료나 결제 실패로 상태 변환 가능
    // 결제 완료에서 환불 완료 상태 변환 가능
    // 결제 실패와 환불 완료 상태는 최종 상태로 더이상 상태 변화 불가능
    return switch (this) {
      case PENDING -> target == APPROVED || target == CANCELLED;
      case APPROVED -> target == REFUNDED;
      case CANCELLED, REFUNDED -> false;
    };
  }
}
