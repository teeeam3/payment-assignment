package sparta.paymentassignment.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.paymentassignment.common.entity.BaseEntity;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "user_membership")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMembership extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private BigDecimal totalPaidAmount;

    public UserMembership(User user, String grade, BigDecimal totalPaidAmount) {
        this.user = user;
        this.grade = grade;
        this.totalPaidAmount = totalPaidAmount;
    }

    public void updateTotalPaidAmount(BigDecimal amount) {
        this.totalPaidAmount = amount;
    }

    public void updateGrade(String grade) {
        this.grade = grade;
    }
}
