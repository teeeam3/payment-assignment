package sparta.paymentassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.order.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
