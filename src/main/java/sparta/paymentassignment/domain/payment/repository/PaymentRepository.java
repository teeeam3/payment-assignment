package sparta.paymentassignment.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sparta.paymentassignment.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 팀 ERD의 portone_payment_id 컬럼과 매핑
    Optional<Payment> findByPortonePaymentId(String portonePaymentId);

    // 사용자의 결제 내역 조회
    // Payment 테이블의 orderId와 Order 테이블의 id를 조인하여 userId를 필터링
    @Query("SELECT p FROM Payment p " +
            "JOIN Order o ON p.orderId = o.id " +
            "WHERE o.customerId = :customerId " +
            "ORDER BY p.id DESC")
    List<Payment> findAllByCustomerId(@Param("customerId") Long customerId);
}