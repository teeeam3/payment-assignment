package sparta.paymentassignment.config;

import io.portone.sdk.server.payment.PaymentClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentClientConfig {

  @Value("${portone.api.secret}")
  private String apiSecret;
  @Value("${portone.api.base-url}")
  private String apiBase;
  @Value("${portone.store.id}")
  private String storeId;

  @Bean
  public PaymentClient paymentClient() {
    return new PaymentClient(apiBase, apiSecret, storeId);
  }

}
