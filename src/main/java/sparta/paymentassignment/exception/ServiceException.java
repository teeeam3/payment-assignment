package sparta.paymentassignment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode errorCode;

    public ServiceException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }
}
