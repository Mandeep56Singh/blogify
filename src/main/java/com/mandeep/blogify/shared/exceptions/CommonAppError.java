package com.mandeep.blogify.shared.exceptions;

import org.springframework.http.HttpStatus;

public enum CommonAppError implements AppError{

    INVALID_CREDENTIALS(
            "/invalid-credentials",
            HttpStatus.UNAUTHORIZED,
            "email or password is wrong",
            "Please enter correct email or password",
            "INVALID_CREDENTIAL"
    ),
    IMAGE_TOO_LARGE(
            "/image-too-large",
            HttpStatus.BAD_REQUEST,
            "Image too large",
            "The uploaded image exceeds the maximum allowed size",
            "IMAGE_TOO_LARGE"
    ),
    RESOURCE_NOT_FOUND(
            "/resource-not-found",
            HttpStatus.NOT_FOUND,
            "Resource not found",
            "The requested resource could not be found. Please check the URL or ID and try again.",
            "RESOURCE_NOT_FOUND"
    ),
    VALIDATION_FAILED(
            "/validation-failed",
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            "Please check the input and try again",
            "VALIDATION_ERROR"
    ),
    TYPE_MISMATCH(
            "/type-mismatch",
            HttpStatus.BAD_REQUEST,
            "Invalid parameter type",
            "One or more parameters have invalid types, please check the request",
            "TYPE_MISMATCH"
    ),
    INTERNAL_SERVER_ERROR(
            "/internal-server-error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong",
            "An unexpected error occurred. Please contact support.",
            "INTERNAL_SERVER_ERROR"
    ),
    ;
    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    CommonAppError(String type, HttpStatus status, String title, String detail, String errorcode) {
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
