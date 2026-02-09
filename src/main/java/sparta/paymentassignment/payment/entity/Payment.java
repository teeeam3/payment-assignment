package sparta.paymentassignment.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;
import sparta.paymentassignment.order.entity.Order;

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

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;


  private Payment(String portonePaymentId, BigDecimal totalAmount, PaymentStatus paymentStatus,
      LocalDateTime paidAt, LocalDateTime refundedAt, Order order) {
    this.portonePaymentId = portonePaymentId;
    this.totalAmount = totalAmount;
    this.paymentStatus = paymentStatus;
    this.paidAt = paidAt;
    this.refundedAt = refundedAt;
    this.order = order;
  }

  public static Payment create(BigDecimal totalAmount, Order order) {

    if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("금액은 0보다 커야 합니다");
    }

    if (order == null) {
      throw new IllegalArgumentException("주문은 반드시 존재해야 합니다.");
    }

    StringBuilder portonePaymentId = new StringBuilder();
    portonePaymentId.append("payment-").append(order.getOrderNumber())
        .append("-" + LocalDateTime.now());

    return new Payment(portonePaymentId.toString(), totalAmount, PaymentStatus.PENDING,
        null, null, order);
  }
}
