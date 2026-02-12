package com.mandeep.blogify.auth.domain.exception;


import com.mandeep.blogify.shared.exceptions.AppError;
import org.springframework.http.HttpStatus;

public enum AuthError implements AppError {

    INVALID_CREDENTIALS(
            "/invalid-credentials",
            HttpStatus.UNAUTHORIZED,
            "user not found",
            "Invalid email or password, please Sign up if you haven't",
            "USER_NOT_FOUND"
    ),
    EMAIL_ALREADY_EXISTS(
            "/email-already-exits",
            HttpStatus.CONFLICT,
            "email already exists",
            "Email already exists, please try different email",
            "EMAIL_ALREADY_EXISTS"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    AuthError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }
}
