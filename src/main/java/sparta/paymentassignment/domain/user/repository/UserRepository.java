package sparta.paymentassignment.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<Object> findByEmail(String email);
}
