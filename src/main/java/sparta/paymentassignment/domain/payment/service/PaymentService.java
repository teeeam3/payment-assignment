//package sparta.paymentassignment.domain.payment.service;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import sparta.paymentassignment.domain.payment.Payment;
//import sparta.paymentassignment.domain.payment.PaymentStatus;
//import sparta.paymentassignment.domain.payment.common.PortOneClient;
//import sparta.paymentassignment.domain.payment.dto.PaymentDetail;
//import sparta.paymentassignment.domain.payment.dto.PaymentRequest;
//import sparta.paymentassignment.domain.payment.dto.PaymentResponse;
//import sparta.paymentassignment.domain.payment.dto.PortOneResponse;
//import sparta.paymentassignment.domain.payment.repository.PaymentRepository;
//import sparta.paymentassignment.domain.point.service.PointService;
//import sparta.paymentassignment.exception.PaymentAmountMismatchException;
//import sparta.paymentassignment.exception.PaymentNotFoundException;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//    private final PaymentRepository paymentRepository;
//    private final PortOneClient portOneClient; // 공통 설정 참고
//    private final PointService pointService;   // 포인트 적립 담당 연동
//    private final MembershipService membershipService; // 멤버십 갱신 담당 연동
//
//    //결제 시도 시작 기록
//    @Transactional
//    public PaymentResponse initiatePayment(PaymentRequest request) {
//        // 1. 엔티티의 static factory 메서드 호출
//        Payment payment = Payment.create(request.getAmount(), request.getOrderId(), request.getOrderNumber());
//
//        // 2. 결제 대기(PENDING) 상태로 저장
//        paymentRepository.save(payment);
//
//        return new PaymentResponse(payment.getPortonePaymentId(), payment.getTotalAmount());
//    }
//
//    // 결제 확정 요청
//    @Transactional
//    public void confirmPayment(String paymentId) {
//        // 1. 결제 정보 조회 (멱등성 체크 포함)
//        Payment payment = paymentRepository.findByPortonePaymentId(paymentId)
//                .orElseThrow(() -> new PaymentNotFoundException());
//
//        if (payment.getPaymentStatus() == PaymentStatus.APPROVED) return;
//
//        // 2. 포트원 서버 조회 및 검증 (최종 금액 확인)
//        PortOneResponse pgData = portOneClient.verify(paymentId);
//        if (!pgData.isValid(payment.getTotalAmount())) {
//            throw new PaymentAmountMismatchException();
//        }
//
//        try {
//            // 3. 결제 완료 상태로 변경 (엔티티 내 approve 활용)
//            payment.approve();
//
//            // 4. [연관 포인트 적립] 멤버십 등급별 적립률 적용 로직 호출
//            pointService.accumulate(payment.getOrderId(), payment.getTotalAmount());
//
//            // 5. [멤버십 등급 갱신] 총 결제 금액 합산 및 등급 업데이트
//            membershipService.refreshGrade(payment.getOrderId());
//
//        } catch (Exception e) {
//            // [보상 트랜잭션] 실패 시 포트원 결제 취소 API 호출
//            portOneClient.cancel(paymentId, "서버 내부 처리 실패: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public List<PaymentDetail> getMyPaymentList(Long customerId) {
//        // 1. 파라미터명을 customerId로 통일하여 리포지토리 호출
//        List<Payment> payments = paymentRepository.findAllByCustomerId(customerId);
//
//        // 2. 엔티티를 PaymentDetail DTO로 변환하여 반환
//        // portonePaymentId, totalAmount, paymentStatus, paidAt 포함
//        return payments.stream()
//                .map(PaymentDetail::from)
//                .toList();
//    }
//
//  public BigDecimal getTotalAmount(String portonePaymentId) {
//    return paymentRepository.findTotalAmountByPortonePaymentId(portonePaymentId);
//  }
//
//  public Optional<Payment> findByPortonePaymentIdWithLock(String portonePaymentId) {
//      return paymentRepository.findByPortonePaymentIdWithLock(portonePaymentId);
//  }
//
//  public Optional<Payment> findByPortonePaymentId(String portonePaymentId) {
//      return paymentRepository.findByPortonePaymentId(portonePaymentId);
//  }
//}
