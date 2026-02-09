package sparta.paymentassignment.domain.order.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
