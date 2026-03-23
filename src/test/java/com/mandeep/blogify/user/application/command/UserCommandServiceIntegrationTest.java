package com.mandeep.blogify.user.application.command;

import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    private static UserRegistrationRequest createRandomRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        return new UserRegistrationRequest(
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

            UserRegistrationRequest request = createRandomRequest();
            userCommandService.register(request);

            boolean exists = userRepository.existsByEmail(new Email(request.email()));
            assertThat(exists).isTrue();

        }

        @Test
        @DisplayName("Rejects registration when email is already taken")
        void should_ThrowException_And_NeverSave_When_EmailIsTaken() {

            // Register new user
            UserRegistrationRequest firstRequest = createRandomRequest();
            userCommandService.register(firstRequest);

            // Request for user Registration with duplicate email

            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String newUserName = "user" + suffix;

            UserRegistrationRequest requestWithDuplicateEmail = new UserRegistrationRequest(
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
            UserRegistrationRequest firstRequest = createRandomRequest();
            userCommandService.register(firstRequest);

            // Request for user Registration with duplicate email

            String suffix = UUID.randomUUID().toString().substring(0, 8);
            String newEmail = "user" + suffix + "@blogify.com";


            UserRegistrationRequest requestWithDuplicateUsername = new UserRegistrationRequest(
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
}
