package com.mandeep.blogify.user.application.command;

import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.domain.exceptions.UserDomainError;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserIdentityGenerator;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Command Service")
class UserCommandServiceUnitTest {

    //region Test Data (Constants remain at the top)
    private static final UUID ID = UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154378");
    private static final String EMAIL = "mandeepraj@231gmail.com";
    private static final String USER_NAME = "user";
    private static final String HASHED_PASSWORD = "hashed_password";
    private static final Role USER_ROLE = Role.USER;
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneOffset.UTC);

    private static final UserRegistrationRequest VALID_REQUEST = new UserRegistrationRequest(
            EMAIL, USER_NAME, HASHED_PASSWORD, USER_ROLE
    );
    //endregion

    //region Mocks
    @Mock private UserRepository userRepository;
    @Mock private UserIdentityGenerator userIdentityGenerator;
    @Mock private Clock clock;

    @InjectMocks
    private UserCommandService userCommandService;
    //endregion

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("Successfully registers and persists a new user")
        void should_SaveUser_When_RequestIsValid() {
            // Arrange
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(userRepository.existsByUserName(any())).thenReturn(false);
            when(userIdentityGenerator.nextUserId()).thenReturn(new UserId(ID));
            when(clock.instant()).thenReturn(FIXED_CLOCK.instant());

            // Act
            userCommandService.register(VALID_REQUEST);

            // Assert
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();

            assertAll(
                    () -> assertThat(saved.getUserId().value()).isEqualTo(ID),
                    () -> assertThat(saved.getUserName().value()).isEqualTo(USER_NAME),
                    () -> assertThat(saved.getEmail().value()).isEqualTo(EMAIL),
                    () -> assertThat(saved.isActive()).isTrue(),
                    () -> assertThat(saved.getCreatedAt()).isEqualTo(FIXED_CLOCK.instant())
            );
        }

        @Test
        @DisplayName("Rejects registration when email is already taken")
        void should_ThrowException_And_NeverSave_When_EmailIsTaken() {
            // Arrange
            when(userRepository.existsByEmail(new Email(EMAIL))).thenReturn(true);

            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userCommandService.register(VALID_REQUEST)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(CommonError.EMAIL_ALREADY_EXISTS);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rejects registration when username is already taken")
        void should_ThrowException_And_NeverSave_When_UserNameIsTaken() {
            // Arrange
            when(userRepository.existsByEmail(new Email(EMAIL))).thenReturn(false);
            when(userRepository.existsByUserName(new UserName(USER_NAME))).thenReturn(true);

            // Act
            CommonException ex = catchThrowableOfType(
                    CommonException.class,
                    () -> userCommandService.register(VALID_REQUEST)
            );

            // Assert
            assertThat(ex.getError()).isEqualTo(CommonError.USERNAME_ALREADY_EXISTS);
            verify(userRepository, never()).save(any());
        }
    }
}