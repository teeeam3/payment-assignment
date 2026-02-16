package sparta.paymentassignment.domain.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.point.Point;
import sparta.paymentassignment.domain.point.PointType;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointExpireService {

    private final PointRepository pointRepository;
    private final UserService userService;

    @Transactional
    public void expireAll() {
        LocalDateTime now = LocalDateTime.now();
        List<Point> expiredPoints = pointRepository.findExpiredPoints(now);

        for (Point point : expiredPoints) {
            try {
                BigDecimal amount = point.getPoints();
                point.expire();
                userService.retrievePoint(point.getUserId(), amount);
                // 실패한 포인트는 건너띄고 계속 진행
            } catch (Exception e) {
                log.error("[POINT EXPIRE FAILED] userId={}. pointId={}, amount={}, error={}",
                        point.getUserId(), point.getId(), point.getPoints(), e.getMessage(), e);
            }
        }
    }
}
