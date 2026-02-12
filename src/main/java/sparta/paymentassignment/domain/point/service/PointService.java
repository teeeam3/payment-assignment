package sparta.paymentassignment.domain.point.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.point.Point;
import sparta.paymentassignment.domain.point.PointType;
import sparta.paymentassignment.domain.point.exception.PointNotFoundException;
import sparta.paymentassignment.domain.point.exception.PointStatusException;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.user.service.UserService;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PointService {

  private final UserService userService;
  private final PointRepository pointRepository;

  @Transactional
  public void registPoint(Long userId, Long orderId, BigDecimal totalAmount) {
    // 총 결제금액의 3%가 point로 들어감
    BigDecimal pointAccumulatedAmount = totalAmount.multiply(BigDecimal.valueOf(0.03));

    // UserService를 통해 유저 잔액 업데이트
    int updatedRows = userService.updatePointByUserId(userId, pointAccumulatedAmount);
    if (updatedRows == 0) {
      throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
    }

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

  @Transactional
  public void cancelPoint(Long userId, Long orderId) {
    Point point = pointRepository.findByUserIDAndOrderId(userId, orderId)
        .orElseThrow(() -> new PointNotFoundException("해당하는 포인트가 존재하지 않음"));

    // 상태 전이 가능 여부 확인 및 상태 변경
    point.updateType(PointType.ACCUMULATED_CANCELLED);

    // 유저의 포인트 차감
    int updatedRows = userService.retrievePoint(userId, point.getPoints());
    if(updatedRows == 0) {
      throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
    }

    log.info("포인트 회수 완료: userId={}, orderId={}, amount={}", userId, orderId, point.getPoints());
  }
}
