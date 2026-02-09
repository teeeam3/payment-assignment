package sparta.paymentassignment.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.order.dto.CreateOrderRequest;
import sparta.paymentassignment.order.dto.CreateOrderResponse;
import sparta.paymentassignment.order.dto.OrderDetailResponse;
import sparta.paymentassignment.order.dto.OrderSummaryResponse;
import sparta.paymentassignment.order.entity.Order;
import sparta.paymentassignment.order.excption.InvalidOrderAmountException;
import sparta.paymentassignment.order.excption.OrderNotFoundException;
import sparta.paymentassignment.order.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        if (request.getTotalAmount() == null || request.getTotalAmount() <= 0) {
            throw new InvalidOrderAmountException(request.getTotalAmount());
        }

        Order order = new Order(
                request.getCustomerId(),
                request.getTotalAmount()
        );

        Order savedOrder = orderRepository.save(order);

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getOrderStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getTotalAmount(),
                        order.getOrderStatus()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCreatedAt()   // BaseEntity 기준
        );

    }
}
