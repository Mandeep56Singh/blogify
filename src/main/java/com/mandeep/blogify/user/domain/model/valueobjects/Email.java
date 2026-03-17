package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.exceptions.UserDomainException;

import java.util.regex.Pattern;

public record Email(String value) {

    // RFC 5321 compliant local-part characters (unquoted form):
    // alphanumeric + ! # $ % & ' * + - / = ? ^ _ ` { | } ~ and dot (with restrictions)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)" +
                    "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                    "@" +
                    "[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*" +
                    "\\.[a-z]{2,}$"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw UserDomainException.emailRequired();
        }

        String normalizedEmail = value.strip().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw UserDomainException.invalidEmail();
        }

        value = normalizedEmail;
    }
}