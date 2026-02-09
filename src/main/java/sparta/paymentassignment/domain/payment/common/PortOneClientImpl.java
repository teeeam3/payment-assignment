package sparta.paymentassignment.domain.payment.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sparta.paymentassignment.domain.payment.dto.PortOneResponse;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PortOneClientImpl implements PortOneClient {
    private final RestTemplate restTemplate;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    @Override
    public PortOneResponse verify(String impUid) {
        // 1. 포트원 조회 API URL
        String url = "https://api.iamport.kr/payments/" + impUid;

        // 2. 인증 헤더 설정 (실무 필수)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAccessToken()); // 토큰 발급 메서드 호출

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            // 3. 민우님이 만든 PortOneResponse로 결과 매핑 [cite: 2026-01-16]
            return restTemplate.exchange(url, HttpMethod.GET, entity, PortOneResponse.class).getBody();
        } catch (Exception e) {
            // [cite: 2026-01-16]
            throw new RuntimeException("포트원 조회 실패: " + e.getMessage());
        }
    }

    @Override
    public void cancel(String impUid, String reason) {
        String url = "https://api.iamport.kr/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAccessToken());

        // 취소 요청 바디 구성
        Map<String, String> body = Map.of(
                "imp_uid", impUid,
                "reason", reason
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("포트원 결제 취소 완료: " + impUid + ", 사유: " + reason);
        } catch (Exception e) {
            System.err.println("포트원 취소 호출 실패: " + e.getMessage());
        }
    }

    // 포트원 API 사용을 위한 Access Token 발급 (내부 메서드)
    private String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";
        Map<String, String> body = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
            Map<String, String> res = (Map<String, String>) response.get("response");
            return res.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("포트원 인증 토큰 발급 실패");
        }
    }
}
