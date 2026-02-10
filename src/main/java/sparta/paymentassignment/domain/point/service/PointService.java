package sparta.paymentassignment.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.point.repository.PointRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

  //pointBalance
  private final PointRepository pointRepository;

//  public void addPoint()
}
