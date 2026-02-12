package sparta.paymentassignment.domain.point;

public enum PointType {
  // 포인트 +
  ACCUMULATED("구매 적립"), // 구매 확정시 포인트 증가
  RESTORED("결제 취소 복구"), // 사용자가 결제 취소시 포인트 증가
  ADJUSTED_PLUS("관리자 가산"), // 관리자가 수동 조정 포인트 +

  // 포인트 -
  USED("포인트 사용"),   // 포인트 사용 (결제 시 차감)
  EXPIRED("기간 만료 소멸"), // 유효기간 만료시 소멸됨
  ACCUMULATED_CANCELLED("적립 취소 회수"), // 구매 확정 취소시 적립받은 포인트 차감
  ADJUSTED_MINUS("관리자 차감"); // 관리자가 수동 조정 포인트 -

  private final String description;

  PointType(String description) {
    this.description = description;
  }

  public boolean canMove(PointType target) {
    if (target == null) {
      return false;
    }

    return switch (this) {
      // 적립성 포인트는 사용되거나, 만료되거나, 적립 자체가 취소될 수 있음
      case ACCUMULATED, RESTORED, ADJUSTED_PLUS ->
          target == USED || target == EXPIRED || target == ACCUMULATED_CANCELLED;

      // 사용된 포인트는 결제 취소 시 복구되거나, 만료 가능
      case USED -> target == RESTORED || target == EXPIRED;

      // 이미 소멸되었거나 취소된 포인트는 더 이상 상태 변화 불가능
      case EXPIRED, ACCUMULATED_CANCELLED, ADJUSTED_MINUS -> false;
    };
  }
}
