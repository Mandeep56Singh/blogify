package com.mandeep.blogify.auth.domain.model.valueObject;

import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HashedPassword Value Object")
class HashedPasswordTest {

    @Mock
    private PasswordVerifier passwordVerifier;

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @ParameterizedTest(name = "rejects invalid hash: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void should_ThrowException_When_HashIsMissing(String raw) {
            assertThatThrownBy(() -> new HashedPassword(raw))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Hash cannot be empty");
        }

        @Test
        @DisplayName("Successfully creates instance with valid hash string")
        void should_CreateHashedPassword_When_ValueIsProvided() {
            String rawHash = "encoded_string_123";
            HashedPassword hashedPassword = new HashedPassword(rawHash);
            assertThat(hashedPassword.value()).isEqualTo(rawHash);
        }
    }

    @Nested
    @DisplayName("matches()")
    class Matches {

        @Test
        @DisplayName("Delegates matching logic to PasswordVerifier")
        void should_DelegateToVerifier_When_CheckingMatch() {
            // Arrange
            String rawInput = "plain_password";
            String storedHash = "stored_hash_val";
            HashedPassword hashedPassword = new HashedPassword(storedHash);

            when(passwordVerifier.matches(rawInput, storedHash)).thenReturn(true);

            // Act
            boolean result = hashedPassword.matches(rawInput, passwordVerifier);

            // Assert
            assertThat(result).isTrue();
            verify(passwordVerifier).matches(rawInput, storedHash);
        }
    }
}