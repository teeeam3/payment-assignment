package sparta.paymentassignment.domain.webhook.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.paymentassignment.domain.webhook.Webhook;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {

  boolean existsByWebhookId(String webhookId);

  Optional<Webhook> findByWebhookId(String webhookId);
}
