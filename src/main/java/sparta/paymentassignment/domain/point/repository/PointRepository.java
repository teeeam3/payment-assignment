package sparta.paymentassignment.domain.point.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sparta.paymentassignment.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

  @Query("select p from Point p where p.userId=:userId and p.orderId=:orderId")
  Optional<Point> findByUserIDAndOrderId(Long userId, Long orderId);

    @Query("select p from Point p where p.pointType in " +
            "('ACCUMULATED', 'RESTORED', 'ADJUSTED_PLUS') and p.expiredAt < :now")
    List<Point> findExpiredPoints(LocalDateTime now);

    @Query("select p from Point p where p.userId = :userId and p.pointType in " +
            "('ACCUMULATED', 'RESTORED', 'ADJUSTED_PLUS') and p.points > 0 order by p.expiredAt asc ")
    List<Point> findAvailablePointsOrderByExpire(Long userId);
}
