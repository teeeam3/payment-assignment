package sparta.paymentassignment.domain.payment.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import sparta.paymentassignment.domain.payment.dto.PortOneResponse;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PortOneClientImpl implements PortOneClient {
    private final RestClient restClient;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    @Value("${portone.api.base-url}")
    private String baseUrl;

    @Override
    public PortOneResponse verify(String impUid) {
        // GET 요청
        return restClient.get()
                .uri(baseUrl + "/payments/{impUid}", impUid)
                .header("Authorization", getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    // 에러 발생 시 즉각 처리
                    throw new RuntimeException("포트원 조회 실패: " + response.getStatusCode());
                })
                .body(PortOneResponse.class); // 결과를 바로 DTO로 변환
    }

    @Override
    public void cancel(String impUid, String reason) {
        // 보상 트랜잭션
        restClient.post()
                .uri(baseUrl + "/payments/cancel")
                .header("Authorization", getAccessToken())
                .body(Map.of("imp_uid", impUid, "reason", reason))
                .retrieve()
                .toBodilessEntity(); // 응답 본문이 필요 없을 때 사용
    }

    private String getAccessToken() {
        //인증 토큰 발급 (Map을 활용한 빠른 데이터 교환)
        Map<String, Object> response = restClient.post()
                .uri(baseUrl + "/users/getToken")
                .body(Map.of("imp_key", apiKey, "imp_secret", apiSecret))
                .retrieve()
                .body(Map.class);

        // 안전한 응답 처리
        if (response == null || !response.containsKey("response")) {
            throw new RuntimeException("포트원 인증 토큰 발급 실패");
        }

        Map<String, String> res = (Map<String, String>) response.get("response");
        return res.get("access_token");
    }
}