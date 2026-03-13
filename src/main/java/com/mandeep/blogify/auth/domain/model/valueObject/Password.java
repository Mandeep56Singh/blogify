package com.mandeep.blogify.auth.domain.model.valueObject;

import com.mandeep.blogify.auth.domain.exception.AuthDomainException;

import java.util.regex.Pattern;

public record Password(String value) {

    public Password {

        if (value == null || value.isBlank()) {
            throw AuthDomainException.passwordRequired();
        }

        if (value.length() < 8) {
            throw AuthDomainException.passwordTooShort();
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,72}$";

        if (!Pattern.matches(passwordRegex, value.strip())) {
            throw AuthDomainException.weakPassword();
        }
    }
}
