package sparta.paymentassignment.domain.order;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import sparta.paymentassignment.common.entity.BaseEntity;
import sparta.paymentassignment.domain.webhook.exception.TotalAmountNotEqualException;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.InvalidOrderAmountException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private Long userId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private String canceledReason;

    private String orderName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Long userId, BigDecimal totalAmount) {

        this.orderNumber = NanoIdUtils.randomNanoId();// 주문번호 자동 생성
        this.userId = userId;
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

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        item.assignOrder(this);
    }

    public void updateTotalAmount(BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderAmountException(totalAmount);
        }
        this.totalAmount = totalAmount;
    }

    public void updateOrderName(String orderName) {
        this.orderName = orderName;
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getSubTotalPrice());
        }
        return totalAmount;
    }
}
