package com.mandeep.blogify.user.application.query;

import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.RegistrationRequest;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class UserQueryServiceIntegrationTest extends BaseIntegrationTest {

    //region Mock Data

    // persistent values in db
    private UUID ID;
    private static final String EMAIL = "user@231gmail.com";
    private static final String USER_NAME = "user";
    private static final String HASHED_PASSWORD = "hashed_password";

    // non existent values in db
    private static final UUID ID1 = UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154334");
    private static final String EMAIL1 = "hyper@231gmail.com";
    private static final String USER_NAME1 = "user1";
    //endregion

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserCommandService userCommandService;

    @BeforeEach
    void persistUser() {
        ID = userCommandService.register(new RegistrationRequest(
                EMAIL,
                USER_NAME,
                HASHED_PASSWORD,
                Role.USER
        ));
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("Returns response when user ID exists")
        void should_ReturnResponse_When_IdExists() {

            // get id
            UserResponse userResponse = userQueryService.getUserById(ID);

            // assert if id match
            assertThat(userResponse.id()).isEqualTo(ID);

        }

        @Test
        @DisplayName("Throws exception when user ID is not found")
        void should_ThrowException_When_IdDoesNotExist() {

            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userQueryService.getUserById(ID1)
            );

            DomainError userError = UserDomainException.userNotFound(new UserId(ID1)).getError();

            assertThat(ex.getError()).isEqualTo(userError);
        }

    }

    @Nested
    @DisplayName("getUserByEmail()")
    class GetUserByUserEmail {

        @Test
        @DisplayName("Returns response when email exists")
        void should_ReturnResponse_When_EmailExists() {

            UserResponse userResponse = userQueryService.getUserByEmail(EMAIL);

            assertThat(userResponse.email()).isEqualTo(EMAIL);
        }


        @Test
        @DisplayName("Throws exception when email is not found")
        void should_ThrowException_When_EmailDoesNotExist() {

            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userQueryService.getUserByEmail(EMAIL1)
            );
            DomainError emailError = CommonException.emailNotFound(new Email(EMAIL1)).getError();

            assertThat(ex.getError()).isEqualTo(emailError);
        }
    }

    @Nested
    @DisplayName("getUserByUserName()")
    class GetUserByUserName {

        @Test
        @DisplayName("Returns response when username exists")
        void should_ReturnResponse_When_UserNameExists() {
            UserResponse userResponse = userQueryService.getUserByUserName(USER_NAME);

            assertThat(userResponse.userName()).isEqualTo(USER_NAME);
        }

        @Test
        @DisplayName("Throws exception when username is not found")
        void should_ThrowException_When_UserNameDoesNotExist() {
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userQueryService.getUserByUserName(USER_NAME1)
            );
            DomainError usernameError = UserDomainException.usernameNotFound(new UserName(USER_NAME1)).getError();

            assertThat(ex.getError()).isEqualTo(usernameError);

        }

    }
}
