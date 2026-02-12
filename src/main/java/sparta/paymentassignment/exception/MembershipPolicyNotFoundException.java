package sparta.paymentassignment.exception;

import org.springframework.http.HttpStatus;

public class MembershipPolicyNotFoundException extends ServiceException {
    public MembershipPolicyNotFoundException(ErrorCode errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }
}
