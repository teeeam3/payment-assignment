package sparta.paymentassignment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthentificationException extends RuntimeException {
    public HttpStatus status;
    public ErrorCode errorCode;
    public AuthentificationException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.errorCode = errorCode;
    }
}
