package com.mandeep.blogify.auth.domain.model.valueObject;

import com.mandeep.blogify.auth.domain.exception.AuthDomainError;
import com.mandeep.blogify.auth.domain.exception.AuthDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("Password Value Object")
class PasswordTest {

    @Nested
    @DisplayName("Validation Logic")
    class Validation {

        @ParameterizedTest
        @NullAndEmptySource
        void should_ThrowException_When_Empty(String raw) {
            AuthDomainException ex = catchThrowableOfType(AuthDomainException.class, () -> new Password(raw));
            assertThat(ex.getError()).isEqualTo(AuthDomainError.PASSWORD_REQUIRED);
        }

        @Test
        void should_ThrowException_When_TooShort() {
            AuthDomainException ex = catchThrowableOfType(AuthDomainException.class, () -> new Password("Ab1@"));
            assertThat(ex.getError()).isEqualTo(AuthDomainError.PASSWORD_TOO_SHORT);
        }

        @ParameterizedTest(name = "rejects weak password: {0}")
        @ValueSource(strings = {
                "onlylowercase1!",
                "ONLYUPPERCASE1!",
                "NoDigits@@",
                "NoSpecialChar123"
        })
        void should_ThrowException_When_PasswordIsWeak(String raw) {
            AuthDomainException ex = catchThrowableOfType(AuthDomainException.class, () -> new Password(raw));
            assertThat(ex.getError()).isEqualTo(AuthDomainError.WEAK_PASSWORD);
        }

        @Test
        void should_CreatePassword_When_Valid() {
            String raw = "StrongPass123!";
            Password password = new Password(raw);
            assertThat(password.value()).isEqualTo(raw);
        }
    }
}