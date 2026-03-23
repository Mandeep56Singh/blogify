package com.mandeep.blogify.shared.domain.exception.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;

public enum CommonError implements DomainError {

    //region Authentication
    INVALID_CREDENTIALS(
            "Invalid credentials",
            "The username or password you entered is incorrect. Please try again.",
            DomainErrorType.UNAUTHORIZED,
            "INVALID_CREDENTIALS"
    ),
    ACCOUNT_NOT_AUTHENTICATED(
            "Account is not authenticated",
            "This action requires you to login to your account, if you haven't created account, Please Sign up.",
            DomainErrorType.UNAUTHORIZED,
            "ACCOUNT_NOT_AUTHENTICATED"
    ),
    //endregion
    
    //region Authorization
    ACCESS_DENIED(
            "Access denied",
            "You do not have permission to perform this action",
            DomainErrorType.FORBIDDEN,
            "ACCESS_DENIED"
    ),
    UNAUTHORIZED_ACCESS(
            "Trying to perform action, without authorization",
            "Please, sign up or login to access this page",
            DomainErrorType.UNAUTHORIZED,
            "UNAUTHORIZED_ACCESS"
    ),
    //endregion
    
    
    IMAGE_TOO_LARGE(
            "Image too large",
            "The uploaded image exceeds the maximum allowed size",
            DomainErrorType.INVALID_INPUT,
            "IMAGE_TOO_LARGE"
    ),
    
    RESOURCE_NOT_FOUND(
            "Resource not found",
            "The requested resource could not be found. Please check the URL or ID and try again.",
            DomainErrorType.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
    ),

    //region Validation
    VALIDATION_FAILED(
            "Validation failed",
            "Please check the input and try again",
            DomainErrorType.INVALID_INPUT,
            "VALIDATION_ERROR"
    ),
    
    TYPE_MISMATCH(
            "Invalid parameter type",
            "One or more parameters have invalid types, please check the request",
            DomainErrorType.INVALID_INPUT,
            "TYPE_MISMATCH"
    ),
    //endregion

    //region Email Address Errors
    INVALID_EMAIL(
            "Email Address is invalid",
            "Please, Provide correct email format",
            DomainErrorType.INVALID_INPUT,
            "INVALID_EMAIL"
    ),
    EMAIL_REQUIRED(
            "Email Address is required",
            "Please, provide email.",
            DomainErrorType.INVALID_INPUT,
            "BLANK_OR_NULL_EMAIL"
    ),
    EMAIL_NOT_FOUND(
            "Email Address is not found",
            "Please check your value again or sign up, if you haven't",
            DomainErrorType.NOT_FOUND,
            "EMAIL_NOT_FOUND"
    ),
    EMAIL_ALREADY_EXISTS(
            "Email Address already exists",
            "Please, provide different value",
            DomainErrorType.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
    ),
    //endregion

    //region user name
    USERNAME_ALREADY_EXISTS(
            "Username already exists",
            "Please, provide different username",
            DomainErrorType.CONFLICT,
            "USERNAME_ALREADY_EXISTS"
    ),
    //endregion
    
    INTERNAL_SERVER_ERROR(
            "Something went wrong",
            "An unexpected error occurred. Please contact support.",
            DomainErrorType.UNEXPECTED,
            "INTERNAL_SERVER_ERROR"
    ),
    ;

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;


    CommonError(String title, String detail, DomainErrorType errorType, String errorCode) {
        this.title = title;
        this.detail = detail;
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    @Override
    public DomainErrorType errorType() {
        return errorType;
    }

    @Override
    public String title() {
        return title;
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
