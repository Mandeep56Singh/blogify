package com.mandeep.blogify.user.application.command;

import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.dto.RegistrationRequest;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class UserCommandServiceIntegrationTest extends BaseIntegrationTest {

    //region Dependencies
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCommandService userCommandService;
    //endregion

    private static RegistrationRequest createRandomRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        return new RegistrationRequest(
                "user" + suffix + "@blogify.com",
                "user" + suffix,
                "StrongPassword@123!",
                Role.USER
        );
    }

    @Nested
    @DisplayName("Registration test")
    class Register {

        @Test
        @DisplayName("Successfully registers and persists a new user")
        void should_SaveUser_When_RequestIsValid() {

            RegistrationRequest request = createRandomRequest();
            userCommandService.register(request);

            boolean exists = userRepository.existsByEmail(new Email(request.email()));
            assertThat(exists).isTrue();

        }

        @Test
        @DisplayName("Rejects registration when email is already taken")
        void should_ThrowException_And_NeverSave_When_EmailIsTaken() {

            // Register new user
            RegistrationRequest firstRequest = createRandomRequest();
            userCommandService.register(firstRequest);

            // Request for user Registration with duplicate email

            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String newUserName = "user" + suffix;

            RegistrationRequest requestWithDuplicateEmail = new RegistrationRequest(
                    firstRequest.email(),
                    newUserName,
                    firstRequest.password(),
                    firstRequest.role()
            );

            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userCommandService.register(requestWithDuplicateEmail)
            );

            DomainError emailError = CommonException.emailAlreadyExists(new Email(requestWithDuplicateEmail.email())).getError();

            assertThat(ex.getError()).isEqualTo(emailError);
        }

        @Test
        @DisplayName("Rejects registration when username is already taken")
        void should_ThrowException_And_NeverSave_When_UserNameIsTaken() {
            // Register new user
            RegistrationRequest firstRequest = createRandomRequest();
            userCommandService.register(firstRequest);

            // Request for user Registration with duplicate email

            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String newEmail = "user" + suffix + "@blogify.com";


            RegistrationRequest requestWithDuplicateUsername = new RegistrationRequest(
                    newEmail,
                    firstRequest.userName(),
                    firstRequest.password(),
                    firstRequest.role()
            );

            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userCommandService.register(requestWithDuplicateUsername)
            );

            DomainError usernameError = CommonException
                    .usernameAlreadyExists(requestWithDuplicateUsername.userName()).getError();

            assertThat(ex.getError()).isEqualTo(usernameError);
        }
    }

    @Nested
    @DisplayName("Deactivate User Tests")
    class DeActivateUser {

        @Test
        @DisplayName("Should successfully deactivate user when called by an ADMIN")
        void should_DeactivateUser_When_ActorIsAdmin() {
            // Arrange: Setup data using the service itself
            UUID adminId = userCommandService.register(
                    new RegistrationRequest("admin@test.com", "adminuser", "Pass@1234!", Role.ADMIN));
            UUID targetId = userCommandService.register(
                    new RegistrationRequest("user@test.com", "targetuser", "Pass@1234!", Role.USER));

            // Act
            userCommandService.deActiveUser(targetId, adminId);

            // Assert: Verify state via repository
            User updatedUser = userRepository.findById(new UserId(targetId)).orElseThrow();
            assertThat(updatedUser.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when target user ID does not exist")
        void should_ThrowException_When_TargetNotFound() {
            // Arrange
            UUID adminId = userCommandService.register(
                    new RegistrationRequest("admin2@test.com", "admin2", "Pass@1234!", Role.ADMIN));
            UUID nonExistentId = UUID.randomUUID();

            // Act
            var ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userCommandService.deActiveUser(nonExistentId, adminId)
            );

            // Assert
            assertThat(ex.getError().errorCode()).isEqualTo("USER_NOT_FOUND");
        }

        @Test
        @DisplayName("Should throw exception when actor (caller) ID does not exist")
        void should_ThrowException_When_ActorNotFound() {
            // Arrange
            UUID targetId = userCommandService.register(
                    new RegistrationRequest("user2@test.com", "user2", "Pass@1234!", Role.USER));
            UUID nonExistentActorId = UUID.randomUUID();

            // Act
            var ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userCommandService.deActiveUser(targetId, nonExistentActorId)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainException.userNotFound(new UserId(targetId)).getError());
        }

        @Test
        @DisplayName("Should fail when a regular USER tries to deactivate someone")
        void should_Fail_When_ActorIsRegularUser() {
            // Arrange
            UUID userActorId = userCommandService.register(
                    new RegistrationRequest("actor@test.com", "actoruser", "Pass@1234!", Role.USER));
            UUID targetId = userCommandService.register(
                    new RegistrationRequest("victim@test.com", "victimuser", "Pass@1234!", Role.USER));

            // Act
            var ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userCommandService.deActiveUser(targetId, userActorId)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainException.forbiddenToDeactivate().getError());

            // Verify user is still active
            User target = userRepository.findById(new UserId(targetId)).orElseThrow();
            assertThat(target.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should fail when an ADMIN tries to deactivate themselves (Immortal Rule)")
        void should_Fail_When_AdminTriesToDeactivateAdmin() {
            // Arrange
            UUID adminId = userCommandService.register(
                    new RegistrationRequest("boss@test.com", "theboss", "Pass@1234!", Role.ADMIN));

            // Act
            var ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userCommandService.deActiveUser(adminId, adminId)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainException.forbiddenToDeactivate().getError());

            // Verify Admin is still active
            User admin = userRepository.findById(new UserId(adminId)).orElseThrow();
            assertThat(admin.isActive()).isTrue();
        }
    }
}
