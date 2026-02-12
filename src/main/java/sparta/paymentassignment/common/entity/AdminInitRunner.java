package sparta.paymentassignment.common.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.UserRole;
import sparta.paymentassignment.domain.user.repository.UserRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AdminInitRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Admin 계정 (InMemory - 데모용)
     */
    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@test.com")) {
            return;
        }
        User admin = new User(
                "관리자",
                "010-0000-0001",
                "admin@test.com",
                passwordEncoder.encode("admin"),
                UserRole.ADMIN,
                new BigDecimal(5000)
        );
        userRepository.save(admin);



    }
}