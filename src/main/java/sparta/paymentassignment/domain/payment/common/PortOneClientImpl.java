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

    // application.yml에 정의된 설정값 주입
    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    @Value("${portone.api.base-url}")
    private String baseUrl;

    @Override
    public PortOneResponse verify(String impUid) {
        // 1. 단건 조회 API 경로 조립
        String url = baseUrl + "/payments/" + impUid;

        // 2. 인증 헤더에 발급받은 Access Token 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            // 3. 외부 API 호출 및 민우님이 만든 DTO로 결과 수신
            return restTemplate.exchange(url, HttpMethod.GET, entity, PortOneResponse.class).getBody();
        } catch (Exception e) {
            throw new RuntimeException("포트원 조회 실패: " + e.getMessage());
        }
    }

    @Override
    public void cancel(String impUid, String reason) {
        // 결제 취소 API 경로 조립
        String url = baseUrl + "/payments/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAccessToken());

        // 포트원 규격에 맞는 취소 요청 데이터 구성
        Map<String, String> body = Map.of(
                "imp_uid", impUid,
                "reason", reason
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // 보상 트랜잭션 성격이므로 실패해도 로그만 남기고 예외는 최소화
            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("포트원 결제 취소 완료: " + impUid + ", 사유: " + reason);
        } catch (Exception e) {
            System.err.println("포트원 취소 호출 실패: " + e.getMessage());
        }
    }


     //포트원 API 사용을 위한 인증 토큰 획득 (모든 요청 전 필수 수행)
    private String getAccessToken() {
        String url = baseUrl + "/users/getToken";
        Map<String, String> body = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        try {
            // API 키와 시크릿으로 토큰 발급 요청
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
            Map<String, String> res = (Map<String, String>) response.get("response");
            return res.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("포트원 인증 토큰 발급 실패");
        }
    }
}
