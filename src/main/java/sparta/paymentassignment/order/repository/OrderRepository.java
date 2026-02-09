package sparta.paymentassignment.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.order.entity.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
