package sparta.paymentassignment.exception;

import org.springframework.http.HttpStatus;

public class EmailDuplicationException extends ServiceException {
    public EmailDuplicationException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }
}
