package sparta.paymentassignment.domain.payment.service;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.Order;
import sparta.paymentassignment.domain.order.OrderItem;
import sparta.paymentassignment.domain.order.repository.OrderRepository;
import sparta.paymentassignment.domain.payment.Payment;
import sparta.paymentassignment.domain.payment.PaymentStatus;
import sparta.paymentassignment.domain.payment.common.PortOneClient;
import sparta.paymentassignment.domain.payment.dto.PaymentDetail;
import sparta.paymentassignment.domain.payment.dto.PaymentRequest;
import sparta.paymentassignment.domain.payment.dto.PaymentResponse;
import sparta.paymentassignment.domain.payment.dto.PortOneResponse;
import sparta.paymentassignment.domain.payment.repository.PaymentRepository;
import sparta.paymentassignment.domain.point.service.PointService;
import sparta.paymentassignment.exception.PaymentAmountMismatchException;
import sparta.paymentassignment.exception.PaymentNotFoundException;
import sparta.paymentassignment.domain.product.entity.Product;
import sparta.paymentassignment.domain.product.repository.ProductRepository;


import java.math.BigDecimal;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PortOneClient portOneClient; // 공통 설정 참고
    private final PointService pointService;   // 포인트 적립 담당 연동
    private final MembershipService membershipService; // 멤버십 갱신 담당 연동
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        // 1. 주문 정보 조회 및 상품명 가공
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 ID입니다: " + request.getOrderId()));

        List<OrderItem> items = order.getOrderItems();
        String orderName = items.get(0).getProductName();
        if (items.size() > 1) {
            orderName += " 외 " + (items.size() - 1) + "건";
        }

        // 2. [포인트 처리] 포인트 사용 로직 호출 및 최종 결제 금액 계산
        if (request.getUsePoint() > 0) {
            pointService.usePoint(order.getCustomerId(),order.getId(), request.getUsePoint());
        }

        // 실제 결제 금액 = 총 금액 - 사용 포인트
        BigDecimal finalAmount = request.getAmount().subtract(BigDecimal.valueOf(request.getUsePoint()));

        // 3. 결제 엔티티 생성 (최종 금액 반영) 및 저장
        Payment payment = Payment.create(finalAmount, request.getOrderId(), request.getOrderNumber(), request.getUsePoint());
        paymentRepository.save(payment);

        return new PaymentResponse(payment.getPortonePaymentId(), payment.getTotalAmount(), orderName);
    }

    // 결제 확정 요청
    @Transactional
    public void confirmPayment(String paymentId) {
        // 1. 결제 정보 조회 (멱등성 체크 포함)
        Payment payment = paymentRepository.findByPortonePaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException());

        if (payment.getPaymentStatus() == PaymentStatus.APPROVED) return;

        // 2. 포트원 서버 조회 및 검증 (최종 금액 확인)
        PortOneResponse pgData = portOneClient.verify(paymentId);
        if (!pgData.isValid(payment.getTotalAmount())) {
            throw new PaymentAmountMismatchException();
        }

        try {
            // 3. 결제 완료 상태로 변경 (엔티티 내 approve 활용)
            payment.approve();

            // 4. [연관 포인트 적립] 멤버십 등급별 적립률 적용 로직 호출
            pointService.registPoint(payment.getId(), payment.getOrderId(), payment.getTotalAmount());

            // 5. [멤버십 등급 갱신] 총 결제 금액 합산 및 등급 업데이트
            membershipService.refreshGrade(payment.getOrderId());

        } catch (Exception e) {
            log.error("결제 확정 중 오류 발생, 보상 트랜잭션 시작: {}", e.getMessage());

            // 1. 포트원 결제 취소
            portOneClient.cancel(paymentId, e.getMessage());

            // 2.주문 정보 조회
            Order order = orderRepository.findById(payment.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

            // 3.적립 취소
            pointService.cancelPoint(order.getCustomerId(), order.getId());

            // 4.사용 포인트 복구
            if (payment.getUsedPoint() != null && payment.getUsedPoint() > 0) {
                pointService.restorePoint(order.getCustomerId(), order.getId(), payment.getUsedPoint());
            }

            // 5.재고 복구 로직
            for (OrderItem item : order.getOrderItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("상품 없음 ID: " + item.getProductId()));
                product.addStock(item.getQuantity());
            }

            throw e;
        }
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

  public Optional<Payment> findByPortonePaymentId(String portonePaymentId) {
      return paymentRepository.findByPortonePaymentId(portonePaymentId);
  }
}
