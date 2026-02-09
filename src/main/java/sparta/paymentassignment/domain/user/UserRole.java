package sparta.paymentassignment.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String roleName;

    public static UserRole getRole(String roleName) {
        for (UserRole role : UserRole.values()) {
            if (role.roleName.equals(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("없는 권한");
    }
}
