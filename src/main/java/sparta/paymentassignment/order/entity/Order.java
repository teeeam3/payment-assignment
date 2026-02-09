package sparta.paymentassignment.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import sparta.paymentassignment.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name="orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false, unique = true, updatable = false)
    private String orderNumber; // 주문 번호 13자리 문자열 생성

    @Column(nullable = false, updatable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long totalAmount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private String canceledReason;
    private LocalDateTime deletedAt;

    public Order(Long customerId, Long totalAmount) {
        this.orderNumber = UUID.randomUUID().toString();// 주문번호 자동 생성
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderStatus = OrderStatus.PENDING;
    }

    public void complete() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public void refund(String reason) {
        this.orderStatus = OrderStatus.REFUNDED;
        this.canceledReason = reason;
    }
}
