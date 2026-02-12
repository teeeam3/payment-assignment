//package sparta.paymentassignment.domain.webhook.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.nio.charset.StandardCharsets;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RestController;
//import sparta.paymentassignment.domain.webhook.TransactionType;
//import sparta.paymentassignment.domain.webhook.dto.PortoneWebhookPayload;
//import sparta.paymentassignment.domain.webhook.service.WebhookService;
//import sparta.paymentassignment.domain.webhook.util.PortOneWebhookVerifier;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class WebhookController {
//
//  private final PortOneWebhookVerifier verifier;
//  private final ObjectMapper objectMapper;
//  private final WebhookService webhookService;
//
//  @PostMapping(value = "/portone-webhook", consumes = "application/json")
//  public ResponseEntity<Void> handlePortoneWebhook(
//
//      // 1. 검증용 원문
//      @RequestBody byte[] rawBody,
//
//      // 2. PortOne V2 필수 헤더
//      @RequestHeader("webhook-id") String webhookId,
//      @RequestHeader("webhook-timestamp") String webhookTimestamp,
//      @RequestHeader("webhook-signature") String webhookSignature
//  ) {
//
//    // 로그
//    log.info(
//        "[PORTONE_WEBHOOK] id={} ts={} body={}",
//        webhookId,
//        webhookTimestamp,
//        new String(rawBody, StandardCharsets.UTF_8)
//    );
//
//    // 시그니처 검증
//    boolean verified = verifier.verify(
//        rawBody,
//        webhookId,
//        webhookTimestamp,
//        webhookSignature
//    );
//
//    if(!verified) {
//      log.warn("[PORTONE_WEBHOOK] signature verification failed");
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
//
//
//    // 검증 통과 후 DTO 변환
//    PortoneWebhookPayload payload;
//    try {
//      payload = objectMapper.readValue(rawBody, PortoneWebhookPayload.class);
//    }catch (Exception e) {
//      log.error("[PORTONE_WEBHOOK] payload parsing failed", e);
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }
//
//    // 이후부터는 신뢰 가능한 데이터
//    log.info(
//        "[PORTONE_WEBHOOK] VERIFIED type={} timestamp={} transactionId={} paymentId={} storeId={}",
//        payload.getType(),
//        payload.getTimestamp(),
//        payload.getData().getTransactionId(),
//        payload.getData().getPaymentId(),
//        payload.getData().getStoreId()
//    );
//
//    String type = payload.getType();
//    TransactionType transactionType = null;
//
//    if (type.contains("Paid")) {
//      transactionType = TransactionType.PAID;
//    }else if(type.contains("Cancelled")) {
//      transactionType = TransactionType.CANCELLED;
//    }else if(type.contains("Failed")) {
//      transactionType = TransactionType.FAILED;
//    }
//
//    if(transactionType.equals(TransactionType.PAID)) {
//      webhookService.processPaid(webhookId, payload.getData().getPaymentId());
//    } else if (transactionType.equals(TransactionType.CANCELLED) || transactionType.equals(
//        TransactionType.FAILED)) {
//      webhookService.processRefund(webhookId, payload.getData().getPaymentId());
//    }
//
//    // 있으면 포인트 적립, 멤버십 등급 갱신
//    return ResponseEntity.ok().build();
//  }
//}
