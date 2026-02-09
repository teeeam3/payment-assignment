package sparta.paymentassignment.domain.payment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentDetail {
    private String portonePaymentId; // 포트원 결제 식별자
    private Long orderId;            // 연결된 주문 ID
    private BigDecimal totalAmount;  // 총 결제 금액
    private PaymentStatus status;    // 결제 상태 (APPROVED, CANCELLED 등)
    private LocalDateTime paidAt;    // 결제 완료 시각


    public static PaymentDetail from(Payment payment) {
        return new PaymentDetail(
                payment.getPortonePaymentId(),
                payment.getOrderId(),
                payment.getTotalAmount(),
                payment.getPaymentStatus(),
                payment.getPaidAt()
        );
    }
}