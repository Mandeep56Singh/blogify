package com.mandeep.blogify.user.domain.exceptions;

import com.mandeep.blogify.shared.exceptions.AppError;
import org.springframework.http.HttpStatus;

public enum UserError implements AppError {

    USER_NOT_FOUND(
            "/user-not-found",
            HttpStatus.NOT_FOUND,
            "user not found",
            "We are unable to find the user, please try different user",
            "USER_NOT_FOUND"
    ),
    EMAIL_NOT_FOUND(
            "/email-not-found",
            HttpStatus.NOT_FOUND,
            "user not found",
            "We are unable to find the user, please try different email",
            "EMAIL_NOT_FOUND"
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

    UserError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String type() {
        return type;
    }
}
