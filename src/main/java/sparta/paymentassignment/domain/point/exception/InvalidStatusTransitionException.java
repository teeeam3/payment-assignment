package sparta.paymentassignment.domain.point.exception;


import sparta.paymentassignment.domain.point.PointType;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(PointType current, PointType target) {
    super("잘못된 상태 전환: " + current.toString() + " 에서 " + target.toString() + "로");
  }
}
