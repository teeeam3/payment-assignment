package sparta.paymentassignment.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다."
    )
    private String phone;
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상 입력해야 합니다.")
    private String password;
    @NotBlank(message = "권한을 선택해 주세요.")
    private String role;
}
