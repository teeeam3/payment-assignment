package sparta.paymentassignment.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.user.UserMembership;
import java.util.Optional;

public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    Optional<UserMembership> findByUserId(Long userId);
}
