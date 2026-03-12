package com.mandeep.blogify.auth.domain.model.valueObject;


import com.mandeep.blogify.auth.domain.exception.AuthDomainException;

import java.util.regex.Pattern;

public record Email(String value) {
    public Email {
        if (value == null || value.isBlank()) {
            throw AuthDomainException.invalidCredentials();
        }

        String normalizedEmail = value.strip().toLowerCase();
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        if (!Pattern.matches(emailRegex, normalizedEmail)) {
            throw AuthDomainException.invalidCredentials();
        }

        value = normalizedEmail;
    }
}
