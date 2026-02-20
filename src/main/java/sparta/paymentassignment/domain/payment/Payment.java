package sparta.paymentassignment.domain.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.paymentassignment.common.entity.BaseEntity;
import sparta.paymentassignment.exception.InvalidStatusTransitionException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 비관적 락 걸었을시 인덱스가 없으면 DB는 테이블 전체를 잠가버림.
@Table(name = "payments", indexes = {
    @Index(name = "idx_portone_payment_id", columnList = "portone_payment_id")
})
@Slf4j
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "portone_payment_id")
  private String paymentId;

  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  private LocalDateTime paidAt;

  private LocalDateTime refundedAt;

  private LocalDateTime cancelledAt;

  @Column(nullable = false, name = "order_id")
  private Long orderId;

  private Long userId;

  private BigDecimal usedPoint;

  private Payment(String paymentId, BigDecimal totalAmount, PaymentStatus paymentStatus,
      Long userId, LocalDateTime paidAt, LocalDateTime refundedAt, Long orderId,
      BigDecimal usedPoint, LocalDateTime cancelledAt) {

    this.paymentId = paymentId;
    this.totalAmount = totalAmount;
    this.paymentStatus = paymentStatus;
    this.paidAt = paidAt;
    this.refundedAt = refundedAt;
    this.orderId = orderId;
    this.usedPoint = usedPoint;
    this.userId = userId;
    this.cancelledAt = cancelledAt;
  }

  public static Payment create(BigDecimal totalAmount, Long orderId, String orderNumber,
      BigDecimal usedPoint, Long userId) {
    if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("금액은 0보다 커야 합니다");
    }

    if (orderId == null || orderNumber == null) {
      throw new IllegalArgumentException("주문은 반드시 존재해야 합니다.");
    }

    String finalPortonePaymentId = "pay-" + orderNumber;

    return new Payment(
            finalPortonePaymentId,
            totalAmount,
            PaymentStatus.PENDING,
            userId,
            null,
            null,
            orderId,
            usedPoint,
            null
    );
  }

  public Payment approve() {
    validateTransition(PaymentStatus.APPROVED);
    this.paymentStatus = PaymentStatus.APPROVED;
    this.paidAt = LocalDateTime.now();
    return this;
  }

  public Payment cancel() {
    validateTransition(PaymentStatus.CANCELLED);
    this.paymentStatus = PaymentStatus.CANCELLED;
    this.cancelledAt = LocalDateTime.now();
    return this;
  }

  public Payment refund() {
    validateTransition(PaymentStatus.REFUNDED);
    this.paymentStatus = PaymentStatus.REFUNDED;
    this.refundedAt = LocalDateTime.now();
    return this;
  }

  private void validateTransition(PaymentStatus target) {
    if (!paymentStatus.canMove(target)) {
      throw new InvalidStatusTransitionException(this.paymentStatus, target);
    }
  }
}
