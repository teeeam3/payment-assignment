package sparta.paymentassignment.order.excption;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long OrderId) {
        super("잘못된 주문 금액 입니다." + OrderId);
    }
}
