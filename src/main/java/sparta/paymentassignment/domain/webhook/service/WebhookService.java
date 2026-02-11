package sparta.paymentassignment.domain.webhook.service;

import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.PaidPayment;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.service.OrderService;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;
import sparta.paymentassignment.domain.payment.service.PaymentService;
import sparta.paymentassignment.domain.point.service.PointService;
import sparta.paymentassignment.domain.webhook.Webhook;
import sparta.paymentassignment.domain.webhook.WebhookStatus;
import sparta.paymentassignment.domain.webhook.exception.NotYetPaidException;
import sparta.paymentassignment.domain.webhook.exception.TotalAmountNotEqualException;
import sparta.paymentassignment.domain.webhook.repository.WebhookRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

  private final PortOneClient portOneClient;
  private final WebhookRepository webhookRepository;
  private final PaymentService paymentService;
  private final PointService pointService;
  private final OrderService orderService;

  // 결제가 승인되었을 때의 웹훅 처리
  public void processPaid(String webhookId, String paymentId) {
    // 멱등성 체크 : webhook 테이블에 저장되는 webhookId는 유일함을 활용
    // 이미 webhook 테이블에 존재하면 아무처리 안함
    if (webhookRepository.existsByWebhookId(webhookId)) {
      log.info("이미 처리된 웹훅입니다: {}", webhookId);
      return;
    }

    // 외부 API 검증
    try {
      verifyPayment(paymentId);
    } catch (Exception e) {
      log.error("결제 검증 실패: {}",e.getMessage());
      // 보상 트랜잭션 시작
      executeCompensationTransaction(paymentId);
      throw e;
    }

    // 실제 비즈니스 로직 및 DB 업데이트
    executeApproval(webhookId, paymentId);
  }

  // 결제 검증 실패 시 실행되는 보상 트랜잭션
  @Transactional
  public void executeCompensationTransaction(String paymentId) {
    Payment payment = paymentService.findByPortonePaymentId(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    // 재고 복구
    // 주문 생성 시 차감했던 재고를 다시 더해준다.
    orderService.restoreStock(payment.getOrderId());

    // 주문 상태를 확정시킴
    payment.cancel();
  }

  // 실제 DB 상태를 변경 시킴
  @Transactional
  public void executeApproval(String webhookId, String paymentId) {
    // 비관적 락을 통해 엔티티 조회
    Payment payment = paymentService.findByPortonePaymentIdWithLock(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제 건입니다."));

    if(payment.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
      webhookRepository.save(new Webhook(webhookId, WebhookStatus.FINISHED));
      return;
    }

    // payment를 approved 상태로 변경
    payment.approve();

    // 웹훅 기록 저장
    webhookRepository.save(new Webhook(webhookId, WebhookStatus.FINISHED));

    // 주문 도메인에서 userId를 뽑아내고 해당 user의 포인트 적립
    Long userId = orderService.findUserIdByOrderId(payment.getOrderId());
    pointService.registPoint(userId, payment.getOrderId(), payment.getTotalAmount());
  }

  // 웹훅에서 결제정보 받았을때 결제가 진짜로 되었는지 포트원 조회
  private void verifyPayment(String portonePaymentId) {
    portOneClient.getPayment()
        .getPayment(portonePaymentId)
        // 반환 값을 받아서 사용하고, 값을 반환하지 않는 콜백 (consumer)
        .thenAccept(payment -> {
          if (payment instanceof PaidPayment paidPayment) {
            BigDecimal webhookAmount = BigDecimal.valueOf(paidPayment.getAmount().getPaid());
            BigDecimal paidAmount = paymentService.getTotalAmount(portonePaymentId);
            if (webhookAmount.compareTo(paidAmount) == 0) {
              log.info("Webhook amount is equal to payment amount.");
            } else {
              throw new TotalAmountNotEqualException("웹훅에서 확인한 결제금액과 포트원에서 확인한 결제 금액이 불일치함");
            }
          } else {
            throw new NotYetPaidException("아직 포트원에서 결제되지 않음");
          }
        }).join();
  }

  @Transactional
  public void processRefund(String webhookId, String paymentId) {

  }
}
