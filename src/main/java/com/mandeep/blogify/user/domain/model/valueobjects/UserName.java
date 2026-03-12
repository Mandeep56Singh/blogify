package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.exceptions.UserDomainException;

public record UserName(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public UserName {
        if (value == null || value.isBlank()) {
            throw UserDomainException.usernameRequired();
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() < MIN_LENGTH || trimmedValue.length() > MAX_LENGTH) {
            throw UserDomainException.usernameInvalidLength(MIN_LENGTH, MAX_LENGTH);
        }

        // You could even add regex here: No special characters, no numbers, etc.
        if (!trimmedValue.matches("^[\\p{L} .'-]+$")) {
            throw UserDomainException.usernameInvalid();
        }

        // Value Objects are immutable. We store the cleaned/trimmed version.
        value = trimmedValue;
    }
}
