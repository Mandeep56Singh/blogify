package com.mandeep.blogify.blog.domain.exceptions;

import com.mandeep.blogify.blog.domain.exceptions.enums.AccountError;
import com.mandeep.blogify.shared.domain.exception.DomainException;

public class AccountException extends DomainException {

    private AccountException(AccountError error, String message) {
        super(error, message);
    }

    public static AccountException accountNotFound() {
        return new AccountException(
                AccountError.ACCOUNT_NOT_FOUND,
                AccountError.ACCOUNT_NOT_FOUND.detail()
        );
    }

    public static AccountException accountNotActive() {
        return new AccountException(
                AccountError.ACCOUNT_NOT_ACTIVE,
                AccountError.ACCOUNT_NOT_ACTIVE.detail()
        );
    }

    public static AccountException unauthorized() {
        return new AccountException(
                AccountError.UNAUTHORIZED,
                AccountError.UNAUTHORIZED.detail()
        );
    }

    public static AccountException accountNotAuthenticated() {
        return new AccountException(
                AccountError.ACCOUNT_NOT_AUTHENTICATED,
                AccountError.ACCOUNT_NOT_AUTHENTICATED.detail()
        );
    }

}
