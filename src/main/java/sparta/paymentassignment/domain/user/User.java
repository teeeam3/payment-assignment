package sparta.paymentassignment.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;

@Getter
@Entity
@Table (name = "users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    @Column(unique = true)
    private String email;
    private String password;
    private UserRole role;

    public User(String name, String phone, String email, String password, UserRole role) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }


}
