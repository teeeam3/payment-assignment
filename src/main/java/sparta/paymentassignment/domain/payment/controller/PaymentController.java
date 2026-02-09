package sparta.paymentassignment.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    // 결제 확정 요청
    @PostMapping("/{paymentId}")
    public ResponseEntity<Void> confirmPayment(@PathVariable String paymentId) {
        paymentService.confirmPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    // 결제 내역 조회
    @GetMapping
    public ResponseEntity<List<PaymentDetail>> getPaymentList(@AuthenticationPrincipal UserPrincipal user) {
        // 인증 세션이나 토큰에서 추출한 userId를 넘긴다
        List<PaymentDetail> response = paymentService.getMyPaymentList(user.getId());
        return ResponseEntity.ok(response);
    }
}