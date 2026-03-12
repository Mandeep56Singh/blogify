package com.mandeep.blogify.blog.domain.exceptions.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum AccountError implements DomainError {

    ACCOUNT_NOT_FOUND(
            "Account not found",
            "Author for the post is not found, Please contact the team if still not resolved",
            DomainErrorType.NOT_FOUND,
            "ACCOUNT_NOT_FOUND"
    ),
    ACCOUNT_NOT_ACTIVE(
            "Account is not active",
            "Your Account is not active, Please contact the team",
            DomainErrorType.INVALID_INPUT,
            "ACCOUNT_NOT_ACTIVE"
    ),
    ACCOUNT_NOT_AUTHENTICATED(
            "Account is not authenticated",
            "This action requires you to login to your account, if you haven't created account, Please Sign up.",
            DomainErrorType.UNAUTHORIZED,
            "ACCOUNT_NOT_AUTHENTICATED"
    ),
    UNAUTHORIZED(
            "Unauthorized to perform action",
            "You do not have permission to perform this action.",
            DomainErrorType.UNAUTHORIZED,
            "UNAUTHORIZED"
    ),

    ;

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;


    AccountError(String title, String detail, DomainErrorType errorType, String errorCode) {
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
