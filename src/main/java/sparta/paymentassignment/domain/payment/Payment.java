package sparta.paymentassignment.domain.payment;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;
import sparta.paymentassignment.exception.InvalidStatusTransitionException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String portonePaymentId;

  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  private LocalDateTime paidAt;

  private LocalDateTime refundedAt;

  @Column(nullable = false, name = "order_id")
  private Long orderId;


  private Payment(String portonePaymentId, BigDecimal totalAmount, PaymentStatus paymentStatus,
      LocalDateTime paidAt, LocalDateTime refundedAt, Long orderId) {
    this.portonePaymentId = portonePaymentId;
    this.totalAmount = totalAmount;
    this.paymentStatus = paymentStatus;
    this.paidAt = paidAt;
    this.refundedAt = refundedAt;
    this.orderId = orderId;
  }

  public static Payment create(BigDecimal totalAmount, Long orderId, String orderNumber) {

    if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("금액은 0보다 커야 합니다");
    }

    if (orderId == null || orderNumber==null) {
      throw new IllegalArgumentException("주문은 반드시 존재해야 합니다.");
    }

    StringBuilder portonePaymentId = new StringBuilder();
    portonePaymentId.append("payment-").append(orderNumber)
        .append("-" + LocalDateTime.now());

    return new Payment(portonePaymentId.toString(), totalAmount, PaymentStatus.PENDING,
        null, null, orderId);
  }

  public Payment approve() {
    validateTransition(PaymentStatus.APPROVED);

    this.paymentStatus = PaymentStatus.APPROVED;
    return this;
  }

  public Payment cancel() {
    validateTransition(PaymentStatus.CANCELLED);

    this.paymentStatus = PaymentStatus.CANCELLED;
    return this;
  }

  public Payment refund() {
    validateTransition(PaymentStatus.REFUNDED);

    this.paymentStatus = PaymentStatus.REFUNDED;
    return this;
  }



  private void validateTransition(PaymentStatus target) {
    if (!paymentStatus.canMove(target)) {
      throw new InvalidStatusTransitionException(this.paymentStatus, target);
    }
  }
}
