package sparta.paymentassignment.domain.payment.service;

import io.portone.sdk.server.PortOneClient;
import io.portone.sdk.server.payment.PaidPayment;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.Order;
import sparta.paymentassignment.domain.order.OrderStatus;
import sparta.paymentassignment.domain.order.service.OrderService;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;
import sparta.paymentassignment.domain.payment.dto.PaymentDetail;
import sparta.paymentassignment.domain.payment.dto.PaymentRequest;
import sparta.paymentassignment.domain.payment.dto.PaymentResponse;
import sparta.paymentassignment.domain.payment.repository.PaymentRepository;
import sparta.paymentassignment.domain.point.service.PointService;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.domain.user.service.UserMembershipService;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.PaymentAmountMismatchException;
import sparta.paymentassignment.exception.PaymentNotFoundException;
import sparta.paymentassignment.domain.product.repository.ProductRepository;
import sparta.paymentassignment.exception.UserNotFoundException;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PortOneClient portOneClient; // portone sdk 사용
    private final PointService pointService;   // 포인트 적립 담당 연동
    private final UserMembershipService membershipService; // 멤버십 갱신 담당 연동
    private final OrderService orderService;
    private final UserRepository userRepository;


    @Transactional
  public PaymentResponse initiatePayment(PaymentRequest request, Long userId) {
    // 1. 주문 정보 조회 및 상품명 가공
    // OrderService를 통해서 order를 가져오도록 수정
    Order order = orderService.findById(request.getOrderId());

    // OrderService에서 주문 이름 생성하도록 수정
    String orderName = orderService.createOrderName(order);

    // 2. [포인트 처리] 포인트 사용 로직 호출 및 최종 결제 금액 계산
    if (request.getUsedPoint().compareTo(BigDecimal.ZERO) > 0) {
      pointService.usePoint(order.getUserId(), order.getId(), request.getUsedPoint());
    }

    // 실제 결제 금액 = 총 금액 - 사용 포인트
    BigDecimal finalAmount = request.getAmount().subtract(request.getUsedPoint());

    // 3. 결제 엔티티 생성 (최종 금액 반영) 및 저장
    Payment payment = Payment.create(finalAmount, request.getOrderId(), order.getOrderNumber(),
        request.getUsedPoint(), userId);
    paymentRepository.save(payment);

    return new PaymentResponse(payment.getPaymentId(), payment.getTotalAmount(), orderName,
        true, "성공", "KRW");
  }

    // 결제 확정 요청
    @Transactional
    public void confirmPayment(String paymentId) {
        // 1. 결제 정보 조회 (멱등성 체크 포함)
      Payment payment = paymentRepository.findByPortonePaymentIdWithLock(paymentId)
          .orElseThrow(() -> new PaymentNotFoundException());

      if (payment.getPaymentStatus() == PaymentStatus.APPROVED) {
        log.info("이미 승인된 결제입니다: {}", paymentId);
        return;
      }

        // 2. 포트원 서버 조회 및 검증 (최종 금액 확인)
      io.portone.sdk.server.payment.Payment portoneSdkPayment;
      try {
        portoneSdkPayment = portOneClient.getPayment().getPayment(paymentId).join();
      } catch (CompletionException e) {
        log.error("Failed to retrieve payment from PortOne SDK for paymentId: {}", paymentId, e);
        failPayment(paymentId);
        throw new RuntimeException("PortOne 결제 정보를 가져오는 중 오류 발생", e);
      }

      if(!(portoneSdkPayment instanceof PaidPayment)){
        log.error("PortOne 서버에서 결제 상태가 PAID가 아님. paymentId={}", paymentId);
        failPayment(paymentId);
        throw new PaymentAmountMismatchException("PortOne 서버에서 결제 상태가 PAID가 아님");
      }

      long totalAmount = ((PaidPayment) portoneSdkPayment).getAmount().getTotal();
      if (payment.getTotalAmount().compareTo(BigDecimal.valueOf(totalAmount)) != 0) {
        log.error("결제 금액 불일치: 시스템 금액={} 포트원 금액={} paymentId={}", payment, totalAmount,
            paymentId);
        throw new PaymentAmountMismatchException();
      }

      // 상태 변경 (approved)
      payment.approve();

      // 포인트 적립
      pointService.registPoint(payment.getUserId(), payment.getOrderId(),
          payment.getTotalAmount());

      // 멤버십 갱신
      membershipService.updateMembership(payment.getUserId());
    }

    // 결제 실패에 대한 모든 비즈니스 로직을 처리하는 메서드
    @Transactional
    public void failPayment(String paymentId) {
      Payment payment = paymentRepository.findByPortonePaymentIdWithLock(paymentId)
          .orElseThrow(() -> new PaymentNotFoundException());

      if (payment.getPaymentStatus() == PaymentStatus.CANCELLED ||
          payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
        log.info("이미 취소/환불된 결제입니다: {}", paymentId);
        return;
      }

      // 상태 변경 (CANCELLED)
      payment.cancel();

      // 사용 포인트가 있다면 복구
      if (payment.getUsedPoint() != null &&
          payment.getUsedPoint().compareTo(BigDecimal.ZERO) > 0) {
        pointService.restorePoint(payment.getUserId(),
            payment.getOrderId(), payment.getUsedPoint());
      }

      // 상품 재고 복구
      orderService.restoreStock(payment.getOrderId());
    }

    // 환불에 대한 모든 비즈니스 로직을 처리하는 메서드
    @Transactional
    public void refundPayment(String paymentId) {
      Payment payment = paymentRepository.findByPortonePaymentIdWithLock(paymentId)
          .orElseThrow(() -> new PaymentNotFoundException());
      Long orderId = payment.getOrderId();

      if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
        log.info("이미 환불된 결제입니다: {}", paymentId);
        return;
      }

      // 상태 변경 REFUNDED
      payment.refund();

      // 적립되었던 포인트 회수
      pointService.cancelPoint(payment.getUserId(), payment.getOrderId());

      // 상품 재고 복구
      orderService.restoreStock(payment.getOrderId());

      // 주문 상태 변경
      orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);
    }

    @Transactional(readOnly = true)
    public List<PaymentDetail> getMyPaymentList(Long customerId) {
        // 1. 파라미터명을 customerId로 통일하여 리포지토리 호출
        List<Payment> payments = paymentRepository.findAllByCustomerId(customerId);

        // 2. 엔티티를 PaymentDetail DTO로 변환하여 반환
        // portonePaymentId, totalAmount, paymentStatus, paidAt 포함
        return payments.stream()
                .map(PaymentDetail::from)
                .toList();
    }

  public BigDecimal getTotalAmount(String portonePaymentId) {
    return paymentRepository.findTotalAmountByPortonePaymentId(portonePaymentId);
  }

  public Optional<Payment> findByPortonePaymentIdWithLock(String portonePaymentId) {
      return paymentRepository.findByPortonePaymentIdWithLock(portonePaymentId);
  }

  public long getOrderId(String paymentId) {
    return paymentRepository.getOrderIdByPaymentId(paymentId);
  }

    public long getUserId(String paymentId) {
        return paymentRepository.getUserIdByPaymentId(paymentId);
    }
}
