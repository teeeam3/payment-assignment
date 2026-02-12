package sparta.paymentassignment.domain.point.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.paymentassignment.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

  @Query("select p from Point p where p.userId=:userId and p.orderId=:orderId")
  Optional<Point> findByUserIDAndOrderId(Long userId, Long orderId);

}
