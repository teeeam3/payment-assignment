package sparta.paymentassignment.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.order.dto.CreateOrderRequest;
import sparta.paymentassignment.order.dto.CreateOrderResponse;
import sparta.paymentassignment.order.entity.Order;
import sparta.paymentassignment.order.excption.InvalidOrderAmountException;
import sparta.paymentassignment.order.repository.OrderRepository;

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
}
