package sparta.paymentassignment.domain.order.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.order.Order;
import sparta.paymentassignment.domain.order.OrderItem;
import sparta.paymentassignment.domain.order.OrderStatus;
import sparta.paymentassignment.domain.order.dto.*;
import sparta.paymentassignment.domain.order.repository.OrderRepository;
import sparta.paymentassignment.domain.product.entity.Product;
import sparta.paymentassignment.domain.product.service.ProductService;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.exception.InvalidOrderAmountException;
import sparta.paymentassignment.exception.OrderNotFoundException;
import sparta.paymentassignment.exception.UserNotFoundException;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderAmountException(BigDecimal.ZERO);
        }

        Order order = new Order(userId, BigDecimal.ZERO);

        for (OrderItemRequest item : request.getItems()) {
            Product productForOrderItem = productService.getProductForOrderItem(item.getProductId(), item.getQuantity());
            BigDecimal subTotalPrice = productForOrderItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = new OrderItem(
                    productForOrderItem.getId(),
                    productForOrderItem.getName(),
                    productForOrderItem.getPrice(),
                    item.getQuantity(),
                    subTotalPrice
            );

            order.addOrderItem(orderItem);
        }
        String generatedOrderName = createOrderName(order);
        order.updateOrderName(generatedOrderName);

        BigDecimal totalAmount = order.calculateTotalAmount();
        order.updateTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getOrderName(),
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
                        order.getOrderName(),
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
                order.getUserId(),
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

  public String createOrderName(Order order) {
    List<OrderItem> items = order.getOrderItems();
    String orderName = items.get(0).getProductName();
    if (items.size() > 1) {
      orderName += " 외 " + (items.size() - 1) + "건";
    }
    return orderName;
  }

  public Order findById(Long orderId) {
    return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.updateStatus(orderStatus);
  }
}
