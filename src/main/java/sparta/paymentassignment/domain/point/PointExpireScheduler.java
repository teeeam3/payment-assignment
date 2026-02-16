package sparta.paymentassignment.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sparta.paymentassignment.domain.point.repository.PointRepository;
import sparta.paymentassignment.domain.point.service.PointExpireService;

@Service
@RequiredArgsConstructor
public class PointExpireScheduler {
    private final PointRepository pointRepository;
    private final PointExpireService pointExpireService;


    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시 (초, 분, 시, 매일, 매월, 요일 무시)
    public void expirePoints() {
        pointExpireService.expireAll();
    }
}
