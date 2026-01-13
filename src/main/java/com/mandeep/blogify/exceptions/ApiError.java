package com.mandeep.blogify.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {

    //    user
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
            "email not found",
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

    //    Category
    CATEGORY_ALREADY_EXITS(
            "/category-already-exists",
            HttpStatus.CONFLICT,
            "category already exists",
            "Category already exists, please try different category title",
            "CATEGORY_ALREADY_EXISTS"
    ),
    CATEGORY_NOT_FOUND(
            "/category-not-found",
            HttpStatus.NOT_FOUND,
            "category not found",
            "Category not found, please try different category",
            "CATEGORY_NOT_FOUND"
    ),
    INTERNAL_SERVER_ERROR(
            "/internal-server-error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong",
            "Please try again after some time",
            "INTERNAL_SERVER_ERROR"
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
    INVALID_PAGE_NUMBER(
            "/invalid-page-number",
            HttpStatus.BAD_REQUEST,
            "Invalid page number",
            "You have requested for invalid page number, please make sure page number is positive, non-zero and within total pages",
            "INVALID_PAGE_NUMBER"
    ),
    INVALID_PAGE_SIZE(
            "/invalid-page-size",
            HttpStatus.BAD_REQUEST,
            "Invalid page size",
            "You have requested for invalid page size, please make sure page size is positive, non-zero and within total elements",
            "INVALID_PAGE_SIZE"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorcode;

    ApiError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorcode = errorcode;
    }
}
