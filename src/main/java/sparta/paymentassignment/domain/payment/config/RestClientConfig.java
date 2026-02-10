package sparta.paymentassignment.domain.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        // 1. 타임아웃 설정을 위한 팩토리 직접 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5초
        factory.setReadTimeout(5000);    // 5초

        // 2. builder() 대신 create()를 사용하거나, 수동으로 빌더를 시작
        // 이 방식이 버전 호환성에서 가장 안전하다는 내용
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}