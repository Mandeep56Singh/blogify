package com.mandeep.blogify.shared.domain.exception;

import com.mandeep.blogify.shared.domain.exception.enums.CommonError;

public class CommonException extends DomainException {

    private CommonException(CommonError error, String message) {
        super(error, message);
    }

    public static CommonException invalidCredentials() {
        return new CommonException(
                CommonError.INVALID_CREDENTIALS,
                CommonError.INVALID_CREDENTIALS.detail()
        );
    }

    public static CommonException accountNotAuthenticated() {
        return new CommonException(
                CommonError.ACCOUNT_NOT_AUTHENTICATED,
                CommonError.ACCOUNT_NOT_AUTHENTICATED.detail()
        );
    }

    public static CommonException accessDenied() {
        return accessDenied("");
    }

    public static CommonException accessDenied(String message) {
        return new CommonException(
                CommonError.ACCESS_DENIED,
                message.isBlank() ? CommonError.ACCESS_DENIED.detail() : message
        );
    }

    public static CommonException unauthorizedAccess() {
        return new CommonException(
                CommonError.UNAUTHORIZED_ACCESS,
                CommonError.UNAUTHORIZED_ACCESS.detail()
        );
    }

    public static CommonException imageTooLarge() {
        return new CommonException(
                CommonError.IMAGE_TOO_LARGE,
                CommonError.IMAGE_TOO_LARGE.detail()
        );
    }

    public static CommonException resourceNotFound() {
        return new CommonException(
                CommonError.RESOURCE_NOT_FOUND,
                CommonError.RESOURCE_NOT_FOUND.detail()
        );
    }

    public static CommonException validationFailed() {
        return new CommonException(
                CommonError.VALIDATION_FAILED,
                CommonError.VALIDATION_FAILED.detail()
        );
    }

    public static CommonException typeMismatch() {
        return new CommonException(
                CommonError.TYPE_MISMATCH,
                CommonError.TYPE_MISMATCH.detail()
        );
    }

    public static CommonException internalServerError() {
        return new CommonException(
                CommonError.INTERNAL_SERVER_ERROR,
                CommonError.INTERNAL_SERVER_ERROR.detail()
        );
    }
}