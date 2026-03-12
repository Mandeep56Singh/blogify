package com.mandeep.blogify.auth.domain.model.valueObject;

import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;

public record HashedPassword(String value) {

    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hash cannot be empty");
        }
    }

    public boolean matches(String rawPassword, PasswordVerifier encoder) {
        return encoder.matches(rawPassword, this.value);
    }
}
