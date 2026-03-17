package com.mandeep.blogify.user.domain.model.entity;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("User Entity")
class UserTest {

    //region Test Data
    private static final UserId A_USER_ID = new UserId(UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154378"));
    private static final UserId B_USER_ID = new UserId(UUID.fromString("019ce847-d388-79c9-a6cf-7242cc48e6d8"));
    private static final UserName A_USERNAME = new UserName("mandeep");
    private static final Email A_EMAIL = new Email("mandeep@example.com");
    private static final String A_PASSWORD = "hashed-password";
    private static final Instant A_FIXED_INSTANT = Instant.parse("2024-01-15T10:00:00Z");
    private static final Clock A_FIXED_CLOCK = Clock.fixed(A_FIXED_INSTANT, ZoneOffset.UTC);
    //endregion

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("Creates a new active user with injected dependencies")
        void should_CreateUser_When_ValidDataProvided() {
            // Act
            User user = User.register(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.USER, A_FIXED_CLOCK);

            // Assert
            assertAll(
                    () -> assertThat(user.getUserId()).isEqualTo(A_USER_ID),
                    () -> assertThat(user.getUserName()).isEqualTo(A_USERNAME),
                    () -> assertThat(user.getEmail()).isEqualTo(A_EMAIL),
                    () -> assertThat(user.getPassword()).isEqualTo(A_PASSWORD),
                    () -> assertThat(user.getRole()).isEqualTo(Role.USER),
                    () -> assertThat(user.isActive()).isTrue(), // Business default
                    () -> assertThat(user.getCreatedAt()).isEqualTo(A_FIXED_INSTANT) // From clock
            );
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("Restores a user exactly as provided from persistence")
        void should_RestoreUser_When_AllDataProvided() {
            // Act
            User user = User.reconstitute(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, false, Role.ADMIN, A_FIXED_INSTANT);

            // Assert
            assertAll(
                    () -> assertThat(user.getUserId()).isEqualTo(A_USER_ID),
                    () -> assertThat(user.getUserName()).isEqualTo(A_USERNAME),
                    () -> assertThat(user.getEmail()).isEqualTo(A_EMAIL),
                    () -> assertThat(user.getPassword()).isEqualTo(A_PASSWORD),
                    () -> assertThat(user.getRole()).isEqualTo(Role.ADMIN),
                    () -> assertThat(user.isActive()).isFalse(), // Preserves exact state
                    () -> assertThat(user.getCreatedAt()).isEqualTo(A_FIXED_INSTANT)
            );
        }
    }

    @Nested
    @DisplayName("isAdmin()")
    class IsAdmin {

        @Test
        @DisplayName("Identifies an ADMIN role")
        void should_ReturnTrue_When_RoleIsAdmin() {
            // Arrange
            User user = User.register(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.ADMIN, A_FIXED_CLOCK);

            // Act & Assert
            assertThat(user.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("Rejects a non-ADMIN role")
        void should_ReturnFalse_When_RoleIsUser() {
            // Arrange
            User user = User.register(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.USER, A_FIXED_CLOCK);

            // Act & Assert
            assertThat(user.isAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsAndHashCode {

        @Test
        @DisplayName("Equality is based solely on UserId")
        void should_BeEqual_When_UserIdIsIdentical() {
            // Arrange
            User first = User.register(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.USER, A_FIXED_CLOCK);
            User second = User.reconstitute(A_USER_ID, new UserName("different"), new Email("diff@test.com"), "pass", false, Role.ADMIN, A_FIXED_INSTANT);

            // Act & Assert
            assertAll(
                    () -> assertThat(first).isEqualTo(second),
                    () -> assertThat(first.hashCode()).isEqualTo(second.hashCode())
            );
        }

        @Test
        @DisplayName("Inequality when UserIds differ")
        void should_NotBeEqual_When_UserIdsDiffer() {
            // Arrange
            User first = User.register(A_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.USER, A_FIXED_CLOCK);
            User second = User.register(B_USER_ID, A_USERNAME, A_EMAIL, A_PASSWORD, Role.USER, A_FIXED_CLOCK);

            // Act & Assert
            assertThat(first).isNotEqualTo(second);
        }
    }
}