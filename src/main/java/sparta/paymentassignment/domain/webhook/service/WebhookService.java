package sparta.paymentassignment.domain.webhook.service;

import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.CancelledPayment;
import io.portone.sdk.server.payment.FailedPayment;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.ReadyPayment;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sparta.paymentassignment.domain.order.OrderStatus;
import sparta.paymentassignment.domain.order.service.OrderService;
import sparta.paymentassignment.domain.payment.PaymentStatus;
import sparta.paymentassignment.domain.payment.service.PaymentService;
import sparta.paymentassignment.domain.webhook.WebhookStatus;
import sparta.paymentassignment.domain.webhook.exception.NotYetPaidException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

  private final PortOneClient portOneClient;
  private final PaymentService paymentService;
  private final OrderService orderService;
  private final WebhookTransactionService webhookTransactionService;


  // 결제가 승인되었을 때의 웹훅 처리
  public void processPaid(String webhookId, String paymentId) {
    try {
      webhookTransactionService.recordWebhook(webhookId);
    } catch (DataIntegrityViolationException e) {
      return; // 중복 웹훅이므로 처리 종료
    }
    try {
      // PortOne 서버 검증
      portOneClient.getPayment().getPayment(paymentId).thenAccept(payment -> {
        if (!(payment instanceof ReadyPayment)) {
          // paid 웹훅을 받았는데 PortOne 서버에서 Ready 상태가 아니면 문제
          log.error("Ready 웹훅을 받았으나 Portone 서버에서는 Ready 상태가 아님 paymentId={}", paymentId);
          throw new NotYetPaidException("아직 포트원이 준비되지 않음");
        }
      }).join();
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FINISHED);
    } catch (CompletionException e) {
      throw new RuntimeException("결제 준비중 오류 발생");
    }
  }

  // 결제 확정되었을때의 웹훅 처리
  public void processConfirmed(String webhookId, String paymentId) {
    try {
      webhookTransactionService.recordWebhook(webhookId);
    } catch (DataIntegrityViolationException e) {
      return; // 중복 웹훅이므로 처리 종료
    }
    try {
      // 포트원 검증
      portOneClient.getPayment().getPayment(paymentId).thenAccept(payment -> {
        if (!(payment instanceof PaidPayment)) {
          log.error("Paid 웹훅을 받았으나 Portone 서버에서는 Paid 상태가 아님. paymentId={}", paymentId);
          throw new IllegalStateException("포트원 서버상 취소된 결제가 아님");
        }
      }).join();

      // 검증 통과, Payment approvePayment 로직 호출
      paymentService.confirmPayment(paymentId);
      //paymentService에서 orderId 뽑아낸다.
      long orderId = paymentService.getOrderId(paymentId);
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FINISHED);
      orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
    } catch (CompletionException e) {
      if (e.getCause() instanceof NotYetPaidException) {
        log.warn("PAID 웹훅이 도착했으나 아직 Portone 서버에 미반영 상태: {}", paymentId);
        webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
      } else {
        log.error("Portone 검증 중 알 수 없는 오류 발생", e);
        webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
        throw e;
      }
    } catch (Exception e) {
      log.error("결제 승인 처리 중 심각한 오류 발생. paymentId={}", paymentId, e);
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
      // 필요시, 심각한 오류는 다시 throw하여 트랜잭션을 롤백시키거나 추가 처리를 할 수 있다.
      throw new RuntimeException("결제 승인 처리 중 오류 발생", e);
    }
  }

  // 환불 되었을때의 웹훅 처리
  public void processRefund(String webhookId, String paymentId) {
    try {
      webhookTransactionService.recordWebhook(webhookId);
    } catch (DataIntegrityViolationException e) {
      return;
    }

    try {
      // 포트원 검증
      portOneClient.getPayment().getPayment(paymentId).thenAccept(payment -> {
        if (!(payment instanceof CancelledPayment)) {
          log.error("CANCELLED 웹훅을 받았으나 Portone 서버에서는 CANCELLED 상태가 아님. paymentId={}", paymentId);
          throw new IllegalStateException("포트원 서버상 취소된 결제가 아님");
        }
      }).join();
      // 서비스 로직 호출
      paymentService.refundPayment(paymentId);
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FINISHED);
    } catch (CompletionException e) {
      log.error("Portone 검증 중 알 수 없는 오류 발생 (환불)", e);
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
      throw e;
    }catch (Exception e) {
      log.error("결제 환불 처리 중 심각한 오류 발생. paymentId={}", paymentId, e);
      webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
      throw new RuntimeException("결제 환불 처리 중 오류 발생", e);
    }
  }

  // 결제가 실패되었을 때의 웹훅 처리
  public void processFailed(String webhookId, String paymentId) {
    try {
      webhookTransactionService.recordWebhook(webhookId);
    } catch (DataIntegrityViolationException e) {
      return;
    }

    // 포트원 검증
    portOneClient.getPayment().getPayment(paymentId).thenAccept(payment -> {
      if (!(payment instanceof FailedPayment)) {
        log.error("FAILED 웹훅을 받았으나 Portone 서버에서는 FAILED 상태가 아님. paymentId={}", paymentId);
        throw new IllegalStateException("포트원 서버상 실패한 결제가 아님");
      }
    }).join();

    // 중앙화된 서비스 로직 호출
    paymentService.failPayment(paymentId);
    webhookTransactionService.updateWebhookStatus(webhookId, WebhookStatus.FAILED);
  }
}
