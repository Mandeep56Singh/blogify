package com.mandeep.blogify.auth.domain.exception;

import com.mandeep.blogify.shared.domain.exception.DomainException;

public class AuthDomainException extends DomainException {

    private AuthDomainException(AuthDomainError authDomainError, String message) {
        super(authDomainError, message);
    }

    public static AuthDomainException invalidCredentials() {
        return new AuthDomainException(
                AuthDomainError.INVALID_CREDENTIALS,
                AuthDomainError.INVALID_CREDENTIALS.detail()
        );
    }

    public static AuthDomainException accountBlocked(String email) {
        return new AuthDomainException(
                AuthDomainError.ACCOUNT_BLOCKED,
                "The account with email '" + email + "' has been blocked, Please contact support for assistance"
        );
    }

    //region Password Exceptions
    public static AuthDomainException passwordRequired() {
        return new AuthDomainException(AuthDomainError.PASSWORD_REQUIRED, AuthDomainError.PASSWORD_REQUIRED.detail());
    }

    public static AuthDomainException passwordTooShort() {
        return new AuthDomainException(AuthDomainError.PASSWORD_TOO_SHORT, AuthDomainError.PASSWORD_TOO_SHORT.detail());
    }

    public static AuthDomainException weakPassword() {
        return new AuthDomainException(AuthDomainError.WEAK_PASSWORD, AuthDomainError.WEAK_PASSWORD.detail());
    }
    //endregion
}
