package com.mandeep.blogify.auth.domain.model.entity;

import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticatedUser Entity")
class AuthenticatedUserTest {

    @Mock
    private PasswordVerifier passwordVerifier;

    private final AuthUserId aUserId = new AuthUserId(UUID.randomUUID());
    private final Email anEmail = new Email("test@blogify.com");
    private final HashedPassword aHashedPassword = new HashedPassword("hashed_val");
    private final String aUserName = "user";

    @Nested
    @DisplayName("Unit testing factory :'load()' ")
    class Load {

        @Test
        @DisplayName("Correctly maps all fields from the load method")
        void should_CreateAuthenticatedUserWithAllFields_When_Loaded() {
            // Act
            AuthenticatedUser user = AuthenticatedUser.load(
                    aUserId, aUserName, anEmail, aHashedPassword, Role.USER, true
            );

            // Assert
            assertThat(user.getAuthUserId()).isEqualTo(aUserId);
            assertThat(user.getUserName()).isEqualTo(aUserName);
            assertThat(user.getEmail()).isEqualTo(anEmail);
            assertThat(user.getHashedPassword()).isEqualTo(aHashedPassword);
            assertThat(user.getRole()).isEqualTo(Role.USER);
            assertThat(user.isActive()).isTrue();
        }

    }

    @Nested
    @DisplayName("Unit testing factory :'authenticate()' ")
    class Authenticate {

        @Test
        @DisplayName("Throws ACCOUNT_BLOCKED exception when user is inactive")
        void should_ThrowAccountBlocked_When_UserIsInactive() {
            // Arrange
            AuthenticatedUser blockedUser = AuthenticatedUser.load(
                    aUserId, aUserName, anEmail, aHashedPassword, Role.USER, false
            );

            // Generate the exact domain error using your factory method
            DomainError expectedError = CommonException.accountBlocked(anEmail.value()).getError();

            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> blockedUser.authenticate("any_password", passwordVerifier)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(expectedError);
            assertThat(ex.getError()).isEqualTo(expectedError);
        }

        @Test
        @DisplayName("Throws INVALID_CREDENTIALS when password verification fails")
        void should_ThrowInvalidCredentials_When_PasswordMismatch() {
            // Arrange
            AuthenticatedUser user = AuthenticatedUser.load(aUserId, aUserName, anEmail, aHashedPassword, Role.USER, true);
            when(passwordVerifier.matches("wrong_pass", aHashedPassword.value())).thenReturn(false);

            DomainError expectedError = CommonException.invalidCredentials().getError();

            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> user.authenticate("wrong_pass", passwordVerifier)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(expectedError);
        }

        @Nested
        @DisplayName("Equality & Identity")
        class Equality {

            @Test
            @DisplayName("Users with same AuthUserId should be equal")
            void should_BeEqual_When_AuthUserIdIsSame() {
                // Arrange
                AuthenticatedUser user1 = AuthenticatedUser.load(aUserId, "user1", anEmail, aHashedPassword, Role.USER, true);
                AuthenticatedUser user2 = AuthenticatedUser.load(aUserId, "user2", new Email("other@test.com"), aHashedPassword, Role.ADMIN, false);

                // Assert
                assertThat(user1).isEqualTo(user2);
                assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
            }

            @Test
            @DisplayName("Users with different AuthUserId should not be equal")
            void should_NotBeEqual_When_AuthUserIdIsDifferent() {
                // Arrange
                AuthenticatedUser user1 = AuthenticatedUser.load(aUserId, aUserName, anEmail, aHashedPassword, Role.USER, true);
                AuthenticatedUser user2 = AuthenticatedUser.load(new AuthUserId(UUID.randomUUID()), aUserName, anEmail, aHashedPassword, Role.USER, true);

                // Assert
                assertThat(user1).isNotEqualTo(user2);
            }

            @Test
            @DisplayName("Should return false when comparing with null or different class")
            void should_ReturnFalse_When_ComparingWithNullOrDifferentType() {
                AuthenticatedUser user = AuthenticatedUser.load(aUserId, aUserName, anEmail, aHashedPassword, Role.USER, true);

                assertThat(user.equals(null)).isFalse();
                assertThat(user.equals("NotAUserObject")).isFalse();
            }
        }
    }

}