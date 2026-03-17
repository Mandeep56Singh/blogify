package com.mandeep.blogify.user.domain.model.valueobjects;

import com.mandeep.blogify.user.domain.exceptions.UserDomainError;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("User ID Value Object")
class UserIdTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Rejects null UUID on construction")
        void should_ThrowException_When_IdIsNull() {
            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> new UserId(null)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.USER_ID_REQUIRED);
        }

        @Test
        @DisplayName("Successfully creates instance with valid UUID")
        void should_CreateInstance_When_UuidIsValid() {
            // Arrange
            UUID validUuid = UUID.randomUUID();

            // Act
            UserId userId = new UserId(validUuid);

            // Assert
            assertThat(userId.value()).isEqualTo(validUuid);
        }
    }

    @Nested
    @DisplayName("equals()")
    class Equals {

        @Test
        @DisplayName("Identifies equality based on UUID value")
        void should_BeEqual_When_UuidsMatch() {
            // Arrange
            UUID commonUuid = UUID.randomUUID();
            UserId id1 = new UserId(commonUuid);
            UserId id2 = new UserId(commonUuid);

            // Act & Assert
            assertThat(id1).isEqualTo(id2);
        }
    }
}