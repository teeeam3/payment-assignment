package sparta.paymentassignment.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sparta.paymentassignment.common.dto.ApiResponse;
import sparta.paymentassignment.domain.payment.dto.PaymentDetail;
import sparta.paymentassignment.domain.payment.dto.PaymentRequest;
import sparta.paymentassignment.domain.payment.dto.PaymentResponse;
import sparta.paymentassignment.domain.payment.service.PaymentService;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 결제 시작 요청
    @PostMapping
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest request, Authentication authentication) {
      Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(paymentService.initiatePayment(request, userId));
    }

    // 결제 확정 요청
    @PostMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@PathVariable String paymentId) {
        paymentService.confirmPayment(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, "결제 확정 요청 성공", null));
    }

    // 결제 환불 요청
    @PostMapping("/refunds/{paymentId}")
    public ResponseEntity<Void> refundPayment(@PathVariable String paymentId) {
      paymentService.refundPayment(paymentId);
      return ResponseEntity.ok().build();
    }

    // 결제 내역 조회
    @GetMapping
    public ResponseEntity<List<PaymentDetail>> getPaymentList(Authentication authentication) {
        // 인증 세션이나 토큰에서 추출한 userId를 넘긴다
        Long userId = Long.valueOf(authentication.getName());
        List<PaymentDetail> response = paymentService.getMyPaymentList(userId);
        return ResponseEntity.ok(response);
    }
}