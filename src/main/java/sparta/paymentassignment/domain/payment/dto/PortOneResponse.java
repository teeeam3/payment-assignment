package sparta.paymentassignment.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PortOneResponse {
    // 포트원에서 관리하는 고유 결제 번호 (imp_uid)
    private String impUid;

    // 우리 서버에서 보냈던 식별자 (merchant_uid)
    private String paymentId;

    // 포트원 서버에 기록된 실제 결제 금액
    private BigDecimal amount;

    // 결제 상태 (paid, ready, cancelled 등)
    private String status;

    // 실제 결제 완료 시각
    private String paidAt;

     //서버의 결제 기록과 포트원의 실제 결제 금액이 일치하는지 확인
    public boolean isValid(BigDecimal expectedAmount) {
        return "paid".equals(this.status) && this.amount.compareTo(expectedAmount) == 0;
    }
}
