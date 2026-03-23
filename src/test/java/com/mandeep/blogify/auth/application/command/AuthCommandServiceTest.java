package com.mandeep.blogify.auth.application.command;

import com.mandeep.blogify.auth.application.dto.LoginRequest;
import com.mandeep.blogify.auth.application.dto.LoginResponse;
import com.mandeep.blogify.auth.application.dto.SignUpRequest;
import com.mandeep.blogify.auth.domain.exception.AuthDomainException;
import com.mandeep.blogify.auth.domain.repository.AuthRepository;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AuthCommandServiceTest extends BaseIntegrationTest {


    @Autowired
    private AuthCommandService authCommandService;

    private SignUpRequest createRandomSignUpRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return new SignUpRequest(
                "auth_" + suffix + "@blogify.com",
                "auth_user_" + suffix,
                "Password@123!"
        );
    }

    @Nested
    @DisplayName("SignUp Integration Tests")
    class SignUp {

        @Test
        @DisplayName("Successfully signs up and returns a valid token response")
        void should_RegisterUserAndReturnToken_When_SignUpRequestIsValid() {
            // Arrange
            SignUpRequest request = createRandomSignUpRequest();

            // Act
            LoginResponse response = authCommandService.signUp(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.token()).isNotBlank();
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.user().role()).isEqualTo("USER");

        }

        @Test
        @DisplayName("Fails when email is already taken")
        void should_ThrowException_When_EmailIsDuplicate() {

            // Arrange
            SignUpRequest request = createRandomSignUpRequest();

            // sign up fist time
            authCommandService.signUp(request);

            SignUpRequest requestWithDuplicateEmail = new SignUpRequest(
                    request.email(),
                    "user random",
                    request.password()
            );

            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> authCommandService.signUp(requestWithDuplicateEmail) // duplicate email
            );

            assertThat(ex.getError()).isEqualTo(CommonError.EMAIL_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("Fails when username is already taken")
        void should_ThrowException_When_UserNameIsDuplicate() {

            // Arrange
            SignUpRequest request = createRandomSignUpRequest();

            // sign up fist time
            authCommandService.signUp(request);

            SignUpRequest requestWithDuplicateUsername = new SignUpRequest(
                    "random@gmail.com",
                    request.userName(),
                    request.password()
            );

            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> authCommandService.signUp(requestWithDuplicateUsername) // duplicate username
            );

            assertThat(ex.getError()).isEqualTo(CommonError.USERNAME_ALREADY_EXISTS);
        }



    }

    @Nested
    @DisplayName("Login Tests")
    class Login {

        @Test
        @DisplayName("Successfully logs in an existing user")
        void should_ReturnToken_When_CredentialsAreValid() {
            // Arrange: First, sign up a user
            SignUpRequest signUpRequest = createRandomSignUpRequest();
            authCommandService.signUp(signUpRequest);

            LoginRequest loginRequest = new LoginRequest(signUpRequest.email(), signUpRequest.password());

            // Act
            LoginResponse response = authCommandService.login(loginRequest);

            // Assert
            assertThat(response.token()).isNotBlank();
            assertThat(response.user().role()).isEqualTo("USER");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "plainaddress",          // No @
                "#@%^%#$@#$@#.com",      // Garbage
                "@example.com",          // No mailbox
                "Joe Smith <email@example.com>", // Extra characters
                " "                      // Blank
        })
        @DisplayName("Fails login with invalid email format without leaking reason")
        void should_ThrowInvalidCredentials_When_EmailFormatIsInvalid(String invalidEmail) {
            // Arrange
            LoginRequest loginRequest = new LoginRequest(invalidEmail, "SomePassword123!");
            DomainError genericError = AuthDomainException.invalidCredentials().getError();

            // Act
            AuthDomainException ex = catchThrowableOfType(
                    AuthDomainException.class,
                    () -> authCommandService.login(loginRequest)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(genericError);
            // This confirms your 'try-catch' block in the service is working
        }

        @Test
        @DisplayName("Fails login with incorrect password")
        void should_ThrowException_When_PasswordIsWrong() {
            // Arrange
            SignUpRequest signUpRequest = createRandomSignUpRequest();
            authCommandService.signUp(signUpRequest);

            LoginRequest wrongPasswordRequest = new LoginRequest(signUpRequest.email(), "Wrong_Password_123");

            DomainError passwordError = AuthDomainException.invalidCredentials().getError();

            AuthDomainException ex = catchThrowableOfType(
                    AuthDomainException.class,
                    () -> authCommandService.login(wrongPasswordRequest)
            );

            assertThat(ex.getError()).isEqualTo(passwordError);
        }

        @Test
        @DisplayName("Fails login when user does not exist")
        void should_ThrowException_When_UserDoesNotExist() {
            // Arrange
            LoginRequest nonExistentRequest = new LoginRequest("ghost@blogify.com", "SomePassword123!");

            // Act & Assert
            assertThatThrownBy(() -> authCommandService.login(nonExistentRequest))
                    .isInstanceOf(AuthDomainException.class);
        }
    }
}