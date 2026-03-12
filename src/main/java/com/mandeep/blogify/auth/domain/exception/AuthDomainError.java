package com.mandeep.blogify.auth.domain.exception;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum AuthDomainError implements DomainError {

    INVALID_CREDENTIALS(
            "Invalid credentials",
            "The provided email or password is incorrect.",
            DomainErrorType.UNAUTHORIZED,
            "AUTH_INVALID_CREDENTIALS"
    ),
    ACCOUNT_BLOCKED(
            "Account blocked",
            "Your account has been blocked. Please contact support for assistance.",
            DomainErrorType.FORBIDDEN,
            "AUTH_ACCOUNT_BLOCKED"
    ),

    //region Password Errors
    PASSWORD_REQUIRED(
            "Password is required",
                    "Please, provide value",
            DomainErrorType.INVALID_INPUT,
            "PASSWORD_REQUIRED"
    ),
    PASSWORD_TOO_SHORT(
            "Password is not valid",
                    "Password must be at-least 8 character long",
            DomainErrorType.INVALID_INPUT,
            "PASSWORD_TOO_SHORT"
    ),
    WEAK_PASSWORD(
            "Password is weak",
                    "Password must contain uppercase, lowercase, numbers, and symbols",
            DomainErrorType.INVALID_INPUT,
            "WEAK_PASSWORD"
    ),
        //endregion

    ;

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;

    AuthDomainError(String title, String detail, DomainErrorType errorType, String errorCode) {
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
