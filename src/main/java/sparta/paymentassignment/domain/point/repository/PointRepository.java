package sparta.paymentassignment.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.paymentassignment.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

}
