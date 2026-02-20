package sparta.paymentassignment.domain.user.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class UserMembershipResponse {
    private final Long id;
    private final String grade;
    private final BigDecimal totalPaidAmount;
    private final BigDecimal rewardRate;

    public UserMembershipResponse(Long id, String grade, BigDecimal totalPaidAmount, BigDecimal rewardRate) {
        this.id = id;
        this.grade = grade;
        this.totalPaidAmount = totalPaidAmount;
        this.rewardRate = rewardRate;
    }
}
