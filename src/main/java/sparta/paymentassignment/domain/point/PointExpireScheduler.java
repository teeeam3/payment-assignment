package sparta.paymentassignment.domain.point;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointExpireScheduler {
    private final PointRepository pointRepository;
    private final UserService userService;

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시 (초, 분, 시, 매일, 매월, 요일 무시)
    public void expirePoints() {
        LocalDateTime now = LocalDateTime.now();
        List<Point> expiredPoints = pointRepository.findExpiredPoints(now);

        for (Point point : expiredPoints) {
            // 이미 사용된 포인트는 제외
            if (point.getPoints().compareTo(BigDecimal.ZERO) <= 0) continue;

            // User 총 포인트 차감
            userService.retrievePoint(point.getUserId(), point.getPoints());

            // 포인트 상태 변경 및 잔액 처리
            point.updateType(PointType.EXPIRED);
            point.reduce(PointType.EXPIRED, point.getPoints());

            log.info("[POINT EXPIRED] userId={}, pointID={}, amount={}",
                    point.getUserId(), point.getId(), point.getPoints());
        }

    }
}
