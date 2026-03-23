package com.mandeep.blogify.shared.domain.exception;

import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;

public class CommonException extends DomainException {

    private CommonException(CommonError error, String message) {
        super(error, message);
    }

    //region Authentication
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
    //endregion

    //region Email Exceptions
    public static CommonException invalidEmail() {
        return new CommonException(
                CommonError.INVALID_EMAIL,
                CommonError.INVALID_EMAIL.detail()
        );
    }

    public static CommonException emailRequired() {
        return new CommonException(CommonError.EMAIL_REQUIRED, CommonError.EMAIL_REQUIRED.detail());
    }

    public static CommonException emailAlreadyExists(Email email) {
        return new CommonException(
                CommonError.EMAIL_ALREADY_EXISTS,
                "'" + email.value() + "' already exists!, Please provide different email"
        );
    }

    public static CommonException emailNotFound(Email email) {
        return new CommonException(
                CommonError.EMAIL_NOT_FOUND,
                "User with email Address '" + email.value() + "' not found"
        );
    }
    //endregion

    public static CommonException usernameAlreadyExists(String userName) {
        return new CommonException(
                CommonError.USERNAME_ALREADY_EXISTS,
                "'" + userName + "' already exists!, Please provide different username"
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