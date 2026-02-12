package sparta.paymentassignment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    TOKEN_EXPIRED("TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    TOKEN_MALFORMED("TOKEN_MALFORMED", "토큰 형식이 올바르지 않습니다."),
    TOKEN_INVALID_SIGNATURE("TOKEN_INVALID_SIGNATURE", "토큰 서명이 유효하지 않습니다."),
    TOKEN_INVALID("TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자가 존재하지 않습니다."),
    MEMBERSHIP_POLICY_NOT_FOUND("MEMBERSHIP_POLICY_NOT_FOUND", "멤버십 정책이 존재하지 않습니다.");

    private final String code;
    private final String message;
}
