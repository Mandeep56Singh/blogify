package com.mandeep.blogify.user.application.query;

import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.domain.exceptions.UserDomainError;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Query Service")
class UserQueryServiceUnitTest {

    //region Test Data
    private static final UUID ID = UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154378");
    private static final String EMAIL = "mandeepraj@231gmail.com";
    private static final String USER_NAME = "user";

    private static final UserResponse USER_RESPONSE = new UserResponse(
            ID, USER_NAME, EMAIL, Role.USER, true, Instant.now(), Instant.now()
    );
    //endregion

    @Mock
    private UserQueryRepository userQueryRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("Returns response when user ID exists")
        void should_ReturnResponse_When_IdExists() {
            // Arrange
            when(userQueryRepository.findResponseById(any(UserId.class))).thenReturn(Optional.of(USER_RESPONSE));

            // Act
            UserResponse result = userQueryService.getUserById(ID);

            // Assert
            assertThat(result).isEqualTo(USER_RESPONSE);
        }

        @Test
        @DisplayName("Throws exception when user ID is not found")
        void should_ThrowException_When_IdDoesNotExist() {
            // Arrange
            when(userQueryRepository.findResponseById(any(UserId.class))).thenReturn(Optional.empty());

            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userQueryService.getUserById(ID)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getUserByEmail()")
    class GetUserByUserEmail {

        @Test
        @DisplayName("Returns response when email exists")
        void should_ReturnResponse_When_EmailExists() {
            // Arrange
            when(userQueryRepository.findResponseByEmail(any(Email.class))).thenReturn(Optional.of(USER_RESPONSE));

            // Act
            UserResponse result = userQueryService.getUserByEmail(EMAIL);

            // Assert
            assertThat(result).isEqualTo(USER_RESPONSE);
        }

        @Test
        @DisplayName("Throws exception when email is not found")
        void should_ThrowException_When_EmailDoesNotExist() {
            // Arrange
            when(userQueryRepository.findResponseByEmail(any(Email.class))).thenReturn(Optional.empty());

            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userQueryService.getUserByEmail(EMAIL)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(CommonError.EMAIL_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getUserByUserName()")
    class GetUserByUserName {

        @Test
        @DisplayName("Returns response when username exists")
        void should_ReturnResponse_When_UserNameExists() {
            // Arrange
            when(userQueryRepository.findResponseByUserName(any(UserName.class))).thenReturn(Optional.of(USER_RESPONSE));

            // Act
            UserResponse result = userQueryService.getUserByUserName(USER_NAME);

            // Assert
            assertThat(result).isEqualTo(USER_RESPONSE);
        }

        @Test
        @DisplayName("Throws exception when username is not found")
        void should_ThrowException_When_UserNameDoesNotExist() {
            // Arrange
            when(userQueryRepository.findResponseByUserName(any(UserName.class))).thenReturn(Optional.empty());

            // Act
            UserDomainException ex = catchThrowableOfType(
                    UserDomainException.class,
                    () -> userQueryService.getUserByUserName(USER_NAME)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(UserDomainError.USERNAME_NOT_FOUND);
        }
    }
}