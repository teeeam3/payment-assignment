package sparta.paymentassignment.dto.user;

import lombok.Getter;

@Getter
public class RegisterResponse {
    private final Long id;
    private final String name;
    private final String phone;
    private final String email;
    private final String role;

    public RegisterResponse(Long id, String name, String phone, String email, String role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }
}
