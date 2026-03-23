package com.mandeep.blogify.shared.domain.model.valueObject;

import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class EmailTest {
    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @ParameterizedTest(name = "rejects blank input: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void should_ThrowException_When_EmailIsMissing(String raw) {
            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> new Email(raw)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(CommonError.EMAIL_REQUIRED);
        }

        @ParameterizedTest(name = "rejects invalid format: ''{0}''")
        @ValueSource(strings = {
                "userexample.com",       // missing @
                "@example.com",          // missing local
                "user@",                 // missing domain
                ".user@example.com",     // leading dot
                "user.@example.com",     // trailing dot
                "user..name@example.com",// double dot
                "us er@example.com",     // space
                "user@-example.com",     // domain hyphen start
                "user@example.c"         // TLD too short
        })
        void should_ThrowException_When_FormatIsInvalid(String raw) {
            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> new Email(raw)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(CommonError.INVALID_EMAIL);
        }

        @ParameterizedTest(name = "normalizes ''{0}'' → ''{1}''")
        @CsvSource({
                "USER@EXAMPLE.COM, user@example.com",
                "'  user@example.com  ', user@example.com",
                "user.name+tag@domain.com, user.name+tag@domain.com"
        })
        void should_NormalizeValue_When_InputIsMessy(String raw, String expected) {
            // Act
            Email email = new Email(raw);

            // Assert
            assertThat(email.value()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("equals()")
    class Equals {

        @Test
        @DisplayName("Identifies equality based on normalized value")
        void should_BeEqual_When_NormalizedValuesMatch() {
            // Arrange
            Email email1 = new Email("USER@example.com");
            Email email2 = new Email("user@EXAMPLE.com");

            // Act & Assert
            assertThat(email1).isEqualTo(email2);
        }

        @Test
        @DisplayName("Identifies inequality for different emails")
        void should_NotBeEqual_When_ValuesAreDifferent() {
            // Arrange
            Email email1 = new Email("first@example.com");
            Email email2 = new Email("second@example.com");

            // Act & Assert
            assertThat(email1).isNotEqualTo(email2);
        }
    }
}