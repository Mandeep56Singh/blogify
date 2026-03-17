package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.exceptions.UserDomainError;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("Username Value Object")
class UserNameTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @ParameterizedTest(name = "rejects blank input: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void should_ThrowException_When_UsernameIsMissing(String raw) {
            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> new UserName(raw)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.USERNAME_REQUIRED);
        }

        @ParameterizedTest(name = "rejects invalid length: ''{0}''")
        @ValueSource(strings = {"ab", "a", "this_username_is_way_too_long_for_the_system"})
        void should_ThrowException_When_LengthIsInvalid(String raw) {
            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> new UserName(raw)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.USERNAME_INVALID_LENGTH);
        }

        @ParameterizedTest(name = "rejects invalid characters: ''{0}''")
        @ValueSource(strings = {"HelloWorld", "hello!world", "hello world", "user@name"})
        void should_ThrowException_When_FormatIsInvalid(String raw) {
            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> new UserName(raw)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.INVALID_USERNAME);
        }

        @ParameterizedTest(name = "accepts valid usernames: ''{0}''")
        @ValueSource(strings = {"mandeep", "mandeep42", "mandeep_singh", "mandeep-singh"})
        void should_CreateInstance_When_InputIsValid(String raw) {
            // Act
            UserName userName = new UserName(raw);

            // Assert
            assertThat(userName.value()).isEqualTo(raw);
        }
    }

    @Nested
    @DisplayName("equals()")
    class Equals {

        @Test
        @DisplayName("Identifies equality based on value")
        void should_BeEqual_When_ValuesMatch() {
            // Arrange
            UserName user1 = new UserName("mandeep_dev");
            UserName user2 = new UserName("mandeep_dev");

            // Act & Assert
            assertThat(user1).isEqualTo(user2);
        }

        @Test
        @DisplayName("Identifies inequality for different values")
        void should_NotBeEqual_When_ValuesAreDifferent() {
            // Arrange
            UserName user1 = new UserName("mandeep_dev");
            UserName user2 = new UserName("other_user");

            // Act & Assert
            assertThat(user1).isNotEqualTo(user2);
        }
    }
}