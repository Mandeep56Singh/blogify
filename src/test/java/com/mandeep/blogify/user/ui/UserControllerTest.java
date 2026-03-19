package com.mandeep.blogify.user.ui;

import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class UserControllerFullTest extends BaseIntegrationTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "user@example.com";
    private static final String USERNAME = "user123";
    private static final String HASHED_PASSWORD = "hashed_password";
    private static final Role ROLE = Role.USER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void persistUser() {
        UserEntity user = UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .userName(USERNAME)
                .password(HASHED_PASSWORD)
                .isActive(true)
                .role(ROLE)
                .build();

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("Returns 200 and user JSON when user exists")
        void returns200WhenUserExists() throws Exception {
            mockMvc.perform(get("/api/v1/users/{id}", USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(USER_ID.toString()))
                    .andExpect(jsonPath("$.data.userName").value(USERNAME))
                    .andExpect(jsonPath("$.data.email").value(EMAIL))
                    .andExpect(jsonPath("$.data.password").doesNotExist());
        }

        @Test
        @DisplayName("Returns 404 when user does not exist")
        void returns404WhenUserNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users/email")
    class GetUserByEmail {

        @Test
        @DisplayName("Returns 200 and user JSON when email exists")
        void returns200WhenEmailExists() throws Exception {
            mockMvc.perform(get("/api/v1/users/email")
                            .param("email", EMAIL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(USER_ID.toString()))
                    .andExpect(jsonPath("$.data.userName").value(USERNAME))
                    .andExpect(jsonPath("$.data.email").value(EMAIL))
                    .andExpect(jsonPath("$.data.password").doesNotExist());
        }

        @Test
        @DisplayName("Returns 400 when email format is invalid")
        void returns400ForInvalidEmail() throws Exception {
            mockMvc.perform(get("/api/v1/users/email")
                            .param("email", "not-an-email")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 404 when email not found")
        void returns404WhenEmailNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/users/email")
                            .param("email", "missing@example.com")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users/username")
    class GetUserByUsername {

        @Test
        @DisplayName("Returns 200 and user JSON when username exists")
        void returns200WhenUsernameExists() throws Exception {
            mockMvc.perform(get("/api/v1/users/username")
                            .param("username", USERNAME)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(USER_ID.toString()))
                    .andExpect(jsonPath("$.data.userName").value(USERNAME))
                    .andExpect(jsonPath("$.data.email").value(EMAIL))
                    .andExpect(jsonPath("$.data.password").doesNotExist());
        }

        @Test
        @DisplayName("Returns 400 when username is blank")
        void returns400ForBlankUsername() throws Exception {
            mockMvc.perform(get("/api/v1/users/username")
                            .param("username", "")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Returns 404 when username not found")
        void returns404WhenUsernameNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/users/username")
                            .param("username", "unknown")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}