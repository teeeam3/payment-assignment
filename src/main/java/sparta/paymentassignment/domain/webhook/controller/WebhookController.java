package sparta.paymentassignment.domain.webhook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sparta.paymentassignment.domain.webhook.TransactionType;
import sparta.paymentassignment.domain.webhook.dto.PortoneWebhookPayload;
import sparta.paymentassignment.domain.webhook.service.WebhookService;
import sparta.paymentassignment.domain.webhook.util.PortOneWebhookVerifier;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

  private final PortOneWebhookVerifier verifier;
  private final ObjectMapper objectMapper;
  private final WebhookService webhookService;

  @PostMapping(value = "/portone-webhook", consumes = "application/json")
  public ResponseEntity<Void> handlePortoneWebhook(

      // 1. 검증용 원문
      @RequestBody byte[] rawBody,

      // 2. PortOne V2 필수 헤더
      @RequestHeader("webhook-id") String webhookId,
      @RequestHeader("webhook-timestamp") String webhookTimestamp,
      @RequestHeader("webhook-signature") String webhookSignature
  ) {

    // 로그
    log.info(
        "[PORTONE_WEBHOOK] id={} ts={} body={}",
        webhookId,
        webhookTimestamp,
        new String(rawBody, StandardCharsets.UTF_8)
    );

    // 시그니처 검증
    boolean verified = verifier.verify(
        rawBody,
        webhookId,
        webhookTimestamp,
        webhookSignature
    );

    if(!verified) {
      log.warn("[PORTONE_WEBHOOK] signature verification failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // 검증 통과 후 DTO 변환
    PortoneWebhookPayload payload;
    try {
      payload = objectMapper.readValue(rawBody, PortoneWebhookPayload.class);
    }catch (Exception e) {
      log.error("[PORTONE_WEBHOOK] payload parsing failed", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 이후부터는 신뢰 가능한 데이터
    log.info(
        "[PORTONE_WEBHOOK] VERIFIED type={} timestamp={} transactionId={} paymentId={} storeId={}",
        payload.getType(),
        payload.getTimestamp(),
        payload.getData().getTransactionId(),
        payload.getData().getPaymentId(),
        payload.getData().getStoreId()
    );

    TransactionType transactionType = TransactionType.fromPortOneTransactionType(payload.getType());
    String paymentId = payload.getData().getPaymentId();

    try {
      switch (transactionType) {
        case READY:
          webhookService.processPaid(webhookId, paymentId);
          break;
        case PAID:
          webhookService.processConfirmed(webhookId, paymentId);
          break;
        case CANCELLED:
          webhookService.processRefund(webhookId, paymentId);
          break;
        case FAILED:
          webhookService.processFailed(webhookId, paymentId);
          break;
        case UNKNOWN:
          log.warn("[PORTONE_WEBHOOK] 웹훅에서 처리하지 않는 타입: {}", payload.getType());
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    }catch (Exception e) {
      log.error("[PORTONE WEBHOOK] 처지되지 않은 심각한 에러 발생. webhookId={}", webhookId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.ok().build();
  }
}
