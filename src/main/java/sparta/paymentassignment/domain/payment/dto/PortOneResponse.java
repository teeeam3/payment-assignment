package sparta.paymentassignment.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PortOneResponse {
    // 포트원은 데이터를 항상 response라는 키 안에 담아서 보내온다
    private ResponseData response;

    @Getter
    @NoArgsConstructor
    public static class ResponseData {
        @JsonProperty("imp_uid") // 포트원의 imp_uid를 impUid로 매핑
        private String impUid;

        @JsonProperty("merchant_uid") // 포트원의 merchant_uid를 paymentId로 매핑
        private String paymentId;

        private BigDecimal amount;
        private String status;
    }

    public boolean isValid(BigDecimal expectedAmount) {
        if (this.response == null) return false;
        return "paid".equals(this.response.status) &&
                this.response.amount.compareTo(expectedAmount) == 0;
    }
}
