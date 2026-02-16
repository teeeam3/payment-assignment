package sparta.paymentassignment.domain.user.repository;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sparta.paymentassignment.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update User u set u.pointBalance = u.pointBalance+:point where u.id=:userId")
  int incrementPoint(@Param("userId") Long userId, @Param("point") BigDecimal point);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update User u set u.pointBalance = u.pointBalance-:point where u.id=:userId and u.pointBalance>=:point")
  int decrementPoint(@Param("userId") Long userId, @Param("point") BigDecimal point);

  @Query("select u.pointBalance from User u where u.id = :userId")
  BigDecimal findPointByUserId(@Param("userId") Long userId);
}
