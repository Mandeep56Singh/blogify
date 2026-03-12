package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.exceptions.UserDomainException;

import java.util.regex.Pattern;

public record Email(String value) {

    public Email {
        if (value == null || value.isBlank()) {
            throw UserDomainException.emailRequired();
        }

        String normalizedEmail = value.strip().toLowerCase();
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        if (!Pattern.matches(emailRegex, normalizedEmail)) {
            throw UserDomainException.invalidEmail();
        }

        value = normalizedEmail;
    }

}
