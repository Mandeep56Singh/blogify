package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.UserConstants;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;

public record UserName(String value) {


    public UserName {
        if (value == null || value.isBlank()) {
            throw UserDomainException.usernameRequired();
        }

        String trimmedValue = value.trim();
        int minLen = UserConstants.USER_NAME_MIN_LENGTH.getValue();
        int maxLen = UserConstants.USER_NAME_MAX_LENGTH.getValue();

        if (trimmedValue.length() < minLen || trimmedValue.length() > maxLen) {
            throw UserDomainException.usernameInvalidLength(minLen, maxLen);
        }

        // url safe username
        if (!trimmedValue.matches("^[a-z0-9][a-z0-9._-]*$")) {
            throw UserDomainException.usernameInvalid();
        }

        value = trimmedValue;
    }
}
