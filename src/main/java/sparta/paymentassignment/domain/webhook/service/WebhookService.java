package sparta.paymentassignment.domain.webhook.service;

import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.PaidPayment;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;
import sparta.paymentassignment.domain.payment.repository.PaymentRepository;
import sparta.paymentassignment.domain.payment.service.PaymentService;
import sparta.paymentassignment.domain.point.service.PointService;
import sparta.paymentassignment.domain.webhook.Webhook;
import sparta.paymentassignment.domain.webhook.WebhookStatus;
import sparta.paymentassignment.domain.webhook.exception.NotYetPaidException;
import sparta.paymentassignment.domain.webhook.exception.TotalAmountNotEqualException;
import sparta.paymentassignment.domain.webhook.repository.WebhookRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WebhookService {

  private final PortOneClient portOneClient;
  private final WebhookRepository webhookRepository;
  private final PaymentService paymentService;
  private final PointService pointService;

  // 웹훅에서 결제정보 받았을때 결제가 진짜로 되었는지 포트원 조회
  public void verifyPayment(String portonePaymentId) {
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
  public void processWebhook(String webhookId, String paymentId, String type) {
    // 결제가 승인되었을 때의 웹훅 이벤트
    if(!"Transaction.Paid".equals(type)) {
      log.info("처리 대상이 아닌 웹훅 이벤트입니다: {}", type);
      return;
    }

    // 멱등성 체크 : webhook 테이블에 저장되는 webhookId는 유일함을 활용
    // 이미 webhook 테이블에 존재하면 아무처리 안함
    if (webhookRepository.existsByWebhookId(webhookId)) {
      log.info("이미 처리된 웹훅 입니다: {}", webhookId);
      return;
    }

    // 결제 엔티티 조회 및 비즈니스 상태 체크
    // 클라이언트 응답을 통해 payment 상태가 APPROVED 상태로 변경되었다면 로직 종료
    Payment payment = paymentService.findByPortonePaymentIdWithLock(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 paymentId로 payment 얻으려 함"));
    if(payment.getPaymentStatus()== PaymentStatus.APPROVED){
      log.info("이미 결제 완료 처리된 주문입니다.");
      webhookRepository.save(new Webhook(webhookId, WebhookStatus.FINISHED));
      return;
    }

    // 신규 웹훅 처리 시작 기록
    Webhook webhook = webhookRepository.save(new Webhook(webhookId, WebhookStatus.PROCESSING));

    // 포트원 API를 사용하여 웹훅에서 받은 결제 내용과 실제 결제 내용 검증
    verifyPayment(paymentId);

    // 비즈니스 로직 및 상태 확정
    payment.approve();

    // 포인트 적립, 멤버십 갱신 진행
    Long userId = paymentService.findUserIdByOrderId(payment.getOrderId());
    pointService.registPoint(userId, payment.getOrderId(), payment.getTotalAmount());

    // 웹훅 처리 완료
    webhook.updateStatus(WebhookStatus.FINISHED);
  }
}
