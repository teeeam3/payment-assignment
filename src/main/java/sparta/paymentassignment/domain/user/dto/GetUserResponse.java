package sparta.paymentassignment.domain.user.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class GetUserResponse {
    private final Long id;
    private final String email;
    private final String customerUid;
    private final String name;
    private final String phone;
    private final BigDecimal pointBalance;

    public GetUserResponse(Long id, String email, String customerUid, String name, String phone, BigDecimal pointBalance) {
        this.id = id;
        this.email = email;
        this.customerUid = customerUid;
        this.name = name;
        this.phone = phone;
        this.pointBalance = pointBalance;
    }
}
