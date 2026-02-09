package sparta.paymentassignment.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final Long id;
    private final String email;
    private final String token;

    public LoginResponse(Long id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }
}
