package sparta.paymentassignment.common.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ApiResponse<T> {

    private final LocalDate timestamp = LocalDate.now();
    private final boolean success;
    private final int status;
    private final String message;
    private final T data;

    public ApiResponse(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data);
    }

}
