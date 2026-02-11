package sparta.paymentassignment.domain.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.Order;
import sparta.paymentassignment.domain.order.OrderItem;
import sparta.paymentassignment.domain.order.dto.*;
import sparta.paymentassignment.domain.order.repository.OrderRepository;
import sparta.paymentassignment.domain.product.service.ProductService;
import sparta.paymentassignment.exception.InvalidOrderAmountException;
import sparta.paymentassignment.exception.OrderNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderAmountException(0L);
        }

        long totalAmount = request.getItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        if (totalAmount <= 0) {
            throw new InvalidOrderAmountException(totalAmount);
        }

        Order order = new Order(
                request.getCustomerId(),
                totalAmount
        );

        request.getItems().forEach(itemRequest -> {
            OrderItem orderItem = new OrderItem(
                    itemRequest.getProductId(),
                    itemRequest.getProductName(),
                    itemRequest.getPrice(),
                    itemRequest.getQuantity()
            );

            order.addOrderItem(orderItem);
        });

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
                .orElseThrow(() -> new OrderNotFoundException(orderId));
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

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                items
        );
    }

  public Long findUserIdByOrderId(Long orderId) {
    return orderRepository.findUserIdByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  @Transactional
  public void restoreStock(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.getOrderItems().forEach(orderItem -> {
      Long productId = orderItem.getProductId();
      Integer quantity = orderItem.getQuantity();
      productService.refillProduct(productId, quantity);
    });
  }
}
