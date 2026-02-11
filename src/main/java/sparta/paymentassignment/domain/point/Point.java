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
import sparta.paymentassignment.domain.payment.PaymentStatus;

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

  private LocalDateTime expired_at;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "order_id")
  private Long orderId;

  @Builder
  private Point(BigDecimal points, PointType pointType, LocalDateTime expired_at, Long userId,
      Long orderId) {
    this.points = points;
    this.pointType = pointType;
    this.expired_at = expired_at;
    this.userId = userId;
    this.orderId = orderId;
  }

  // 처음 포인트는 100씩 줌
  public static Point createInitialPoint(Long userId) {
    return new Point(BigDecimal.valueOf(100L), PointType.ADJUSTED_PLUS,
        LocalDateTime.now().plusDays(30L), userId, null);
  }
}
