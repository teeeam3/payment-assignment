package sparta.paymentassignment.domain.webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.webhook.Webhook;
import sparta.paymentassignment.domain.webhook.WebhookStatus;
import sparta.paymentassignment.domain.webhook.repository.WebhookRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookTransactionService {

  private final WebhookRepository webhookRepository;

  /**
   * 웹훅 처리 전, ID를 DB에 기록하여 멱등성 확보
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recordWebhook(String webhookId) {
    try {
      webhookRepository.save(new Webhook(webhookId, WebhookStatus.PROCESSING));
    } catch (DataIntegrityViolationException e) {
      log.warn("이미 처리되었거나 처리 중인 웹훅입니다: {}", webhookId);
      throw e;
    }
  }

  /**
   * 웹훅 처리 상태를 별도 트랜잭션으로 업데이트
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateWebhookStatus(String webhookId, WebhookStatus webhookStatus) {
    Webhook webhook = webhookRepository.findByWebhookId(webhookId)
        .orElseThrow(() -> new IllegalArgumentException("해당하는 웹훅이 존재하지 않습니다."));
    webhook.updateStatus(webhookStatus);
  }
}
