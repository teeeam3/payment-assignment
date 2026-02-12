package sparta.paymentassignment.domain.membership;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "membership_policy")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String grade;

    @Column(nullable = false)
    private BigDecimal minAmount;

    @Column(nullable = false)
    private BigDecimal maxAmount;

    @Column(nullable = false)
    private BigDecimal rewardRate;

    public MembershipPolicy(String grade, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal rewardRate) {
        this.grade = grade;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.rewardRate = rewardRate;
    }

}
