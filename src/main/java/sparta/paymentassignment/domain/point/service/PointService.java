package sparta.paymentassignment.domain.point.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.point.Point;
import sparta.paymentassignment.domain.point.PointType;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.user.service.UserService;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

  private final UserService userService;
  private final PointRepository pointRepository;

  @Transactional
  public void registPoint(Long userId, Long orderId, BigDecimal totalAmount) {
    // UserService를 통해 유저 잔액 업데이트
    int updatedRows = userService.updatePointByUserId(userId, totalAmount);
    if (updatedRows == 0) {
      throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
    }
    // 총 결제금액의 3%가 point로 들어감
    BigDecimal pointAccumulatedAmount = totalAmount.multiply(BigDecimal.valueOf(0.03));

    // 포인트 테이블에 이력 저장
    Point pointHistory = Point.builder()
        .points(pointAccumulatedAmount)
        .pointType(PointType.ACCUMULATED)
        .orderId(orderId)
        .userId(userId)
        .expired_at(LocalDateTime.now().plusDays(30))
        .build();

    pointRepository.save(pointHistory);
  }
}
