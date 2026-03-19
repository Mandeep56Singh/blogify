package com.mandeep.blogify.user.infrastructure.persistence.repository;

import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserJpaRepositoryIntegrationTest extends BaseIntegrationTest {

    private static final UUID ID = UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154378");
    private static final String EMAIL = "user@231gmail.com";
    private static final String USER_NAME = "user";
    private static final String HASHED_PASSWORD = "hashed_password";
    private static final Role USER_ROLE = Role.USER;

    private static final UUID ID1 = UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154334");
    private static final String EMAIL1 = "hyper@231gmail.com";
    private static final String USER_NAME1 = "user1";


    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setup() {
        UserEntity persistedUser = new UserEntity();
        persistedUser.setId(ID);
        persistedUser.setEmail(EMAIL);
        persistedUser.setUserName(USER_NAME);
        persistedUser.setPassword(HASHED_PASSWORD);
        persistedUser.setActive(true);
        persistedUser.setRole(USER_ROLE);

        entityManager.persist(persistedUser); // save the user

        entityManager.flush(); // force JPA to store user in db
        entityManager.clear(); // clear first level cache, so to make sure we call db everytime

    }


    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmail {

        @Test
        @DisplayName("Return True when email exists")
        void should_ReturnTrue_When_EmailExists() {
            boolean exists = userJpaRepository.existsByEmail(EMAIL);
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Return False when email don't exists")
        void should_ReturnFalse_When_EmailNotExists() {
            boolean exists = userJpaRepository.existsByEmail(EMAIL1);
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUserName()")
    class ExistsByUserName {

        @Test
        @DisplayName("Return True when username exists")
        void should_ReturnTrue_When_UserNameExists() {
            boolean exists = userJpaRepository.existsByUserName(USER_NAME);
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Return False when username don't exists")
        void should_ReturnFalse_When_UserNameNotExists() {
            boolean exists = userJpaRepository.existsByUserName(USER_NAME1);
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findUserResponseById()")
    class FindUserResponseById {

        @Test
        @DisplayName("should return response when user exists")
        void should_ReturnResponse_When_UserExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseById(ID);
            assertThat(userResponse).isPresent();
            assertThat(userResponse.get().id()).isEqualTo(ID);
        }

        @Test
        @DisplayName("should return empty when user doesn't exists")
        void should_ReturnEmpty_When_UserNotExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseById(ID1);
            assertThat(userResponse).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserResponseByEmail()")
    class FindUserResponseByEmail {

        @Test
        @DisplayName("should return response when user exists")
        void should_ReturnResponse_When_EmailExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseByEmail(EMAIL);
            assertThat(userResponse).isPresent();
            assertThat(userResponse.get().id()).isEqualTo(ID);
        }

        @Test
        @DisplayName("should return empty when user doesn't exists")
        void should_ReturnEmpty_When_EmailNotExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseByEmail(EMAIL1);
            assertThat(userResponse).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserResponseByUserName()")
    class FindUserResponseByUserName {

        @Test
        @DisplayName("should return response when username exists")
        void should_ReturnResponse_When_UserNameExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseByUserName(USER_NAME);
            assertThat(userResponse).isPresent();
            assertThat(userResponse.get().id()).isEqualTo(ID);
        }

        @Test
        @DisplayName("should return empty when username doesn't exists")
        void should_ReturnEmpty_When_UserNameNotExists() {
            Optional<UserResponse> userResponse = userJpaRepository.findUserResponseByUserName(USER_NAME1);
            assertThat(userResponse).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUsersById()")
    class FindUsersById {

        @ParameterizedTest(name = "Input IDs: {0}")
        @DisplayName("Return only existing users for various input combinations")
        @MethodSource("idListsProvider")
        void should_ReturnOnlyExistingUsers(List<UUID> inputIds) {
            List<UserResponse> result = userJpaRepository.findUsersById(inputIds);

            List<UUID> expectedIds = inputIds.stream()
                    .filter(id -> id.equals(ID))
                    .toList();

            assertThat(result.stream().map(UserResponse::id).toList())
                    .containsExactlyInAnyOrderElementsOf(expectedIds);
        }

        static Stream<List<UUID>> idListsProvider() {
            return Stream.of(
                    List.of(),          // Empty list
                    List.of(ID1),       // Non-existent
                    List.of(ID, ID1),   // Mixed
                    List.of(ID)         // Existent
            );
        }
    }


}