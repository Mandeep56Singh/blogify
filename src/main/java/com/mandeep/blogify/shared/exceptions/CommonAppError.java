package com.mandeep.blogify.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonAppError implements AppError{

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
            "Please try again after some time",
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
}
