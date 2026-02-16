package sparta.paymentassignment;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.Order;
import sparta.paymentassignment.domain.order.repository.OrderRepository;
import sparta.paymentassignment.domain.point.Point;
import sparta.paymentassignment.domain.point.PointType;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.point.service.PointExpireService;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.UserRole;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.domain.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PointExpireTest {

    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private PointExpireService pointExpireService;

    @Test
    void expirePoints_mixedScenario() {
        User user = userRepository.save(new User(
                "홍길동",
                "010-1234-5678",
                "test@test.com",
                "1234",
                UserRole.USER,
                BigDecimal.ZERO
        ));

        Long userId = user.getId();
        userService.updatePointByUserId(userId, BigDecimal.valueOf(1000));

        Order order = orderRepository.save(new Order(userId, BigDecimal.ZERO));

        Long orderId = order.getId();

        // 1) 유저가 총 1000원 포인트를 보유했다고 가정
        //    - 500원 포인트: 만료일이 아직 안 지남 (ACCUMULATED)
        //    - 300원 포인트: 만료일이 이미 지남 (ACCUMULATED → 만료 대상)
        //    - 200원 포인트: 이미 사용됨 (USED)
        Point activePoint = pointRepository.save(Point.builder()
                .points(BigDecimal.valueOf(500))
                .pointType(PointType.ACCUMULATED)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .userId(userId)
                .orderId(orderId)
                .build());

        Point expiredPoint = pointRepository.save(Point.builder()
                .points(BigDecimal.valueOf(300))
                .pointType(PointType.ACCUMULATED)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .userId(userId)
                .orderId(orderId)
                .build());

        Point usedPoint = pointRepository.save(Point.builder()
                .points(BigDecimal.valueOf(200))
                .pointType(PointType.USED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .userId(userId)
                .orderId(orderId)
                .build());

        // 사용 포인트 차감
        userService.retrievePoint(userId, BigDecimal.valueOf(200));

        entityManager.flush();
        entityManager.clear();

        // 2) 스케줄러 실행
        pointExpireService.expireAll();

        entityManager.flush();
        entityManager.clear();



        // 3) 검증
        Point reloadedActive = pointRepository.findById(activePoint.getId()).orElseThrow();
        Point reloadedExpired = pointRepository.findById(expiredPoint.getId()).orElseThrow();
        Point reloadedUsed = pointRepository.findById(usedPoint.getId()).orElseThrow();

        // 500원 포인트는 여전히 ACCUMULATED 상태
        assertThat(reloadedActive.getPointType()).isEqualTo(PointType.ACCUMULATED);
        assertThat(reloadedActive.getPoints()).isEqualByComparingTo(BigDecimal.valueOf(500));

        // 300원 포인트는 EXPIRED 상태로 변경되고 잔액 0
        assertThat(reloadedExpired.getPointType()).isEqualTo(PointType.EXPIRED);
        assertThat(reloadedExpired.getPoints()).isEqualByComparingTo(BigDecimal.ZERO);

        // 200원 포인트는 이미 USED 상태라 건너뛰고 그대로 유지
        assertThat(reloadedUsed.getPointType()).isEqualTo(PointType.USED);
        assertThat(reloadedUsed.getPoints()).isEqualByComparingTo(BigDecimal.valueOf(200));

        // 유저 총 포인트는 500원만 남아야 함
        BigDecimal userBalance = userService.getUser(userId).getPointBalance();
        assertThat(userBalance).isEqualByComparingTo(BigDecimal.valueOf(500));
    }
}
