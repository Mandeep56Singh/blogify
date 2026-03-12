package com.mandeep.blogify.user.domain.exceptions;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum UserDomainError implements DomainError {


    //region Email Errors
    INVALID_EMAIL(
            "Email is invalid",
            "Please, Provide correct email format",
            DomainErrorType.INVALID_INPUT,
            "INVALID_EMAIL"
    ),
    EMAIL_REQUIRED(
            "Email is required",
            "Please, provide email.",
            DomainErrorType.INVALID_INPUT,
            "BLANK_OR_NULL_EMAIL"
    ),
    EMAIL_NOT_FOUND(
            "Email is not found",
            "Please check your value again or sign up, if you haven't",
            DomainErrorType.NOT_FOUND,
            "EMAIL_NOT_FOUND"
    ),
    EMAIL_ALREADY_EXISTS(
            "Email already exists",
            "Please, provide different value",
            DomainErrorType.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
    ),
    //endregion




    //region UserName Errors
    USERNAME_REQUIRED (
            "Username is required",
            "Please, Provide Username",
            DomainErrorType.INVALID_INPUT,
            "USERNAME_REQUIRED"
    ),
    USERNAME_INVALID_LENGTH(
            "Username length is invalid",
            "Username must be between 2 and 100 characters",
            DomainErrorType.INVALID_INPUT,
            "USERNAME_INVALID_LENGTH"
    ),
    INVALID_USERNAME(
            "Username is invalid",
            "Username must not contain special characters, numbers",
            DomainErrorType.INVALID_INPUT,
            "INVALID_USERNAME"
    ),
    USERNAME_ALREADY_EXISTS(
            "Username already exists",
            "Please, provide different username",
            DomainErrorType.CONFLICT,
            "USERNAME_ALREADY_EXISTS"
    ),
    USERNAME_NOT_FOUND(
            "Username not found",
            "Please check your value again or sign up, if you haven't",
            DomainErrorType.NOT_FOUND,
            "USERNAME_NOT_FOUND"
    ),
    //endregion


    //region User Errors
    USER_NOT_FOUND(
            "User not found",
            "User not found, please try again",
            DomainErrorType.NOT_FOUND,
            "USER_NOT_FOUND"
    ),
    //endregion


    ;
    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;


    UserDomainError(String title, String detail, DomainErrorType errorType, String errorCode) {
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
