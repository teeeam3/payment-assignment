package sparta.paymentassignment.domain.point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;
import sparta.paymentassignment.domain.point.exception.PointStatusException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class Point extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal points;

  @Enumerated(value = EnumType.STRING)
  private PointType pointType;

  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "order_id")
  private Long orderId;

  @Builder
  private Point(BigDecimal points, PointType pointType, LocalDateTime expiredAt, Long userId,
      Long orderId) {
    this.points = points;
    this.pointType = pointType;
    this.expiredAt = expiredAt;
    this.userId = userId;
    this.orderId = orderId;
  }

  public void updateType(PointType targetType) {
    if(!this.pointType.canMove(targetType)) {
      throw new PointStatusException(
          "현재 [" + this.pointType + "] 상태에서는 [" + targetType + "]으로 변경할 수 없습니다.");
    }
    this.pointType = targetType;
  }

  // 포인트 적립
  public static Point createAccumulated(Long userId, BigDecimal amount, Long orderId) {
    return Point.builder()
        .userId(userId)
        .points(amount)
        .orderId(orderId)
        .pointType(PointType.ACCUMULATED)
        .build();
  }

  // 포인트 차감/소멸 처리 로직
  public void reduce(PointType target, BigDecimal reduceAmount) {
    if (!this.pointType.canMove(target)) {
      throw new IllegalStateException("현재 포인트 상태에서 " + target + "으로 진행할 수 없습니다.");
    }

    if (this.points.compareTo(reduceAmount) < 0) {
      throw new IllegalArgumentException("차감할 잔액이 부족합니다. (잔액: " + this.points + ")");
    }

    this.points = this.points.subtract(reduceAmount);
  }

  // 처음 포인트는 100씩 줌
  public static Point createInitialPoint(Long userId) {
    return new Point(BigDecimal.valueOf(500L), PointType.ADJUSTED_PLUS,
        LocalDateTime.now().plusDays(30L), userId, null);
  }

    public void expire() {
        if (this.pointType != PointType.ACCUMULATED &&
                this.pointType != PointType.RESTORED &&
                this.pointType != PointType.ADJUSTED_PLUS) {
            return;
        }

        this.points = BigDecimal.ZERO;
        this.pointType = PointType.EXPIRED;
    }
}
