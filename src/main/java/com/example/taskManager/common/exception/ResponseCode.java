package com.example.taskManager.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    // ======= System Errors =======
    SYSTEM("ERR_501", "System error. Please try again later!", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("ERR_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_CODE("ERR_000", "No error code specified", HttpStatus.INTERNAL_SERVER_ERROR),
    CACHE_FAILED("VAL_500", "Cache failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("ERR_404", "Resource not found", HttpStatus.NOT_FOUND),
    NO_BODY("ERR_400", "No body in request", HttpStatus.BAD_REQUEST),

    // ======= Auth Errors =======
    UNAUTHORIZED("ERR_401", "Authentication failed", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_403", "Access denied", HttpStatus.FORBIDDEN),

    // ======= Custom Example =======
    USER_NOT_FOUND("ERR_404", "Không tìm thấy user", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID("ERR_404", "Mật khẩu không đúng", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("ERR_409", "Email đã tồn tại", HttpStatus.CONFLICT),




    BAD_REQUEST("ERR_400", "Bad request", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ResponseCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
