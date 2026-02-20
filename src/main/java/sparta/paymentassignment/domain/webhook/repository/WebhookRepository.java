package sparta.paymentassignment.domain.webhook.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.webhook.Webhook;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    Optional<Webhook> findByWebhookId(String webhookId);
}
