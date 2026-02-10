package sparta.paymentassignment.domain.point;

public enum PointType {
  // 포인트 +
  ACCUMULATED, // 구매 확정시 포인트 증가
  RESTORED, // 사용자가 결제 취소시 포인트 증가
  ADJUSTED_PLUS, // 관리자가 수동 조정 포인트 +

  // 포인트 -
  USED,   // 포인트 사용 (결제 시 차감)
  EXPIRED, // 유효기간 만료시 소멸됨
  ACCUMULATED_CANCELLED, // 구매 확정 취소시 적립받은 포인트 차감
  ADJUSTED_MINUS // 관리자가 수동 조정 포인트 -

}
