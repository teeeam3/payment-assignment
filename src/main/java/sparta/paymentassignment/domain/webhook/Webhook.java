package sparta.paymentassignment.domain.webhook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Webhook extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String webhookId;

  @Enumerated(value = EnumType.STRING)
  private WebhookStatus webhookStatus;

  public Webhook(String webhookId, WebhookStatus webhookStatus) {
    this.webhookId = webhookId;
    this.webhookStatus = webhookStatus;
  }

  public void updateStatus(WebhookStatus webhookStatus) {
    this.webhookStatus = webhookStatus;
  }
}
