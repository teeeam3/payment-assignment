package sparta.paymentassignment.order.excption;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long OrderId) {
        super("해당 주문이 존재하지 않습니다. orderId=" + OrderId);
    }
}
