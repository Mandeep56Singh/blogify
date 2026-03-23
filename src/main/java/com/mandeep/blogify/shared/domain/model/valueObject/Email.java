package com.mandeep.blogify.shared.domain.model.valueObject;

import com.mandeep.blogify.shared.AppConstants;
import com.mandeep.blogify.shared.domain.exception.CommonException;

import java.util.regex.Pattern;

public record Email(String value) {

    // RFC 5321 compliant local-part characters (unquoted form):
    // alphanumeric + ! # $ % & ' * + - / = ? ^ _ ` { | } ~ and dot (with restrictions)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            AppConstants.EMAIL_REGREX
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw CommonException.emailRequired();
        }

        String normalizedEmail = value.strip().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw CommonException.invalidEmail();
        }

        value = normalizedEmail;
    }
}
