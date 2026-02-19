package sparta.paymentassignment.domain.payment.repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 사용자의 결제 내역 조회
    // Payment 테이블의 orderId와 Order 테이블의 id를 조인하여 userId를 필터링
    @Query("SELECT p FROM Payment p " +
            "JOIN Order o ON p.orderId = o.id " +
            "WHERE o.userId = :customerId " +
            "ORDER BY p.id DESC")
    List<Payment> findAllByCustomerId(@Param("customerId") Long customerId);

    // PortoneId를 기준으로하는 주문 총 결제 금액 조회
    @Query("select p.totalAmount from Payment p where p.paymentId=:portonePaymentId")
    BigDecimal getTotalAmount(@Param("portonePaymentId") String portonePaymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.paymentId=:portonePaymentId")
    Optional<Payment> findByPortonePaymentIdWithLock(@Param("portonePaymentId") String portonePaymentId);

    @Query("select p.totalAmount from Payment p where p.paymentId=:portonePaymentId")
    BigDecimal findTotalAmountByPortonePaymentId(String portonePaymentId);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
            "WHERE p.userId = :userId AND p.paymentStatus = 'APPROVED' ")
    BigDecimal sumPaidAmount(Long userId);

    @Query("select p.orderId from Payment p where p.paymentId=:paymentId")
    long getOrderIdByPaymentId(String paymentId);

    @Query("select p.userId from Payment p where p.paymentId=:paymentId")
    long getUserIdByPaymentId(String paymentId);
}