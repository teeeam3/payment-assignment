package sparta.paymentassignment.domain.membership.rpository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.paymentassignment.domain.membership.MembershipPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MembershipPolicyRepository extends JpaRepository<MembershipPolicy, Long> {

    @Query("SELECT p FROM MembershipPolicy p WHERE :amount BETWEEN p.minAmount " +
            "AND p.maxAmount ORDER BY p.minAmount DESC")
    List<MembershipPolicy> findPolicies(BigDecimal amount);

    Optional<MembershipPolicy> findByGrade(String grade);
}
