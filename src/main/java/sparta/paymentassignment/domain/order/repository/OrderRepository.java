package sparta.paymentassignment.domain.order.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.paymentassignment.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

  @Query("select o.userId from Order o where o.id=:orderId")
  Optional<Long> findUserIdByOrderId(Long orderId);
}
