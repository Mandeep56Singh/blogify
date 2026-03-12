package com.mandeep.blogify.user.domain.exceptions;

import com.mandeep.blogify.shared.domain.exception.DomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;

public class UserDomainException extends DomainException {

    private UserDomainException(UserDomainError userError, String message) {
        super(userError, message);
    }


    //region Email Exceptions
    public static UserDomainException invalidEmail() {
        return new UserDomainException(
                UserDomainError.INVALID_EMAIL,
                UserDomainError.INVALID_EMAIL.detail()
        );
    }

    public static UserDomainException emailRequired() {
        return new UserDomainException(UserDomainError.EMAIL_REQUIRED, UserDomainError.EMAIL_REQUIRED.detail());
    }

    public static UserDomainException emailAlreadyExists(Email email) {
        return new UserDomainException(
                UserDomainError.EMAIL_ALREADY_EXISTS,
                "'"+ email.value() +"' already exists!, Please provide different email"
        );
    }

    public static UserDomainException emailNotFound(Email email) {
        return new UserDomainException(
                UserDomainError.EMAIL_NOT_FOUND,
                "User with email '"+ email.value() +"' not found"
        );
    }
    //endregion


    //region UserName Exceptions
    public static UserDomainException usernameRequired() {
        return new UserDomainException(
                UserDomainError.USERNAME_REQUIRED,
                UserDomainError.USERNAME_REQUIRED.detail());
    }

    public static UserDomainException usernameInvalidLength(int min, int max) {
        return new UserDomainException(
                UserDomainError.USERNAME_INVALID_LENGTH,
                "Name must be between " + min + " and " + max + "characters");
    }

    public static UserDomainException usernameInvalid() {
        return new UserDomainException(
                UserDomainError.INVALID_USERNAME,
                UserDomainError.INVALID_USERNAME.detail()
        );
    }

    public static UserDomainException usernameAlreadyExists(UserName userName) {
        return new UserDomainException(
                UserDomainError.USERNAME_ALREADY_EXISTS,
                "'"+ userName.value() +"' already exists!, Please provide different username"
        );
    }

    public static UserDomainException usernameNotFound(UserName name) {
        return new UserDomainException(
                UserDomainError.USERNAME_NOT_FOUND,
                "User with user name '"+ name.value() +"' not found"
        );
    }
    //endregion


    //region User Exceptions
    public static UserDomainException userNotFound(UserId id) {
        return new UserDomainException(
                UserDomainError.USER_NOT_FOUND,
                "User with Id '" + id.value() + "' not found"
        );
    }
    //endregion


}