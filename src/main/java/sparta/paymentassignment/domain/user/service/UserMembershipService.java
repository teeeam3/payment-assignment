package sparta.paymentassignment.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.membership.MembershipPolicy;
import sparta.paymentassignment.domain.membership.rpository.MembershipPolicyRepository;
import sparta.paymentassignment.domain.payment.repository.PaymentRepository;
import sparta.paymentassignment.domain.user.UserMembership;
import sparta.paymentassignment.domain.user.dto.UserMembershipResponse;
import sparta.paymentassignment.domain.user.repository.UserMembershipRepository;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.MembershipPolicyNotFoundException;
import sparta.paymentassignment.exception.UserNotFoundException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserMembershipService {
    private final UserMembershipRepository userMembershipRepository;
    private final MembershipPolicyRepository membershipPolicyRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void updateMembership(Long userId) {
        // 총 결제 금액 계산
        BigDecimal totalPaid = paymentRepository.sumPaidAmount(userId);
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        // 멤버쉽 정책 조회
        MembershipPolicy policy = membershipPolicyRepository.findPolicies(totalPaid)
                .stream()
                .findFirst()
                .orElseThrow(
                        () -> new MembershipPolicyNotFoundException(ErrorCode.MEMBERSHIP_POLICY_NOT_FOUND)
                );

        // 멤버쉽 조회
        UserMembership membership = userMembershipRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 멤버쉽 업데이트(총 결제 금액 및 등급)
        membership.updateTotalPaidAmount(totalPaid);
        membership.updateGrade(policy.getGrade());
    }

    @Transactional(readOnly = true)
    public UserMembershipResponse getMyMembership(Long userId) {
        UserMembership membership = userMembershipRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        MembershipPolicy policy = membershipPolicyRepository.findByGrade(membership.getGrade()).orElseThrow(
                () -> new MembershipPolicyNotFoundException(ErrorCode.MEMBERSHIP_POLICY_NOT_FOUND)
        );

        return new UserMembershipResponse(
                membership.getId(),
                membership.getGrade(),
                membership.getTotalPaidAmount(),
                policy.getRewardRate()
        );
    }
}
