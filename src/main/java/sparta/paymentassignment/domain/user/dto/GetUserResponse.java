package sparta.paymentassignment.domain.user.dto;

import lombok.Getter;

@Getter
public class GetUserResponse {
    private final String email;
    private final String customerUid;
    private final String name;
    private final String phone;
    private final Long pointBalance;

    public GetUserResponse(String email, String customerUid, String name, String phone, Long pointBalance) {
        this.email = email;
        this.customerUid = customerUid;
        this.name = name;
        this.phone = phone;
        this.pointBalance = pointBalance;
    }




}
