package com.mandeep.blogify.user.ui;

import com.jayway.jsonpath.JsonPath;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.RegistrationRequestWithRole;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.application.query.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserControllerIntegrationTest extends BaseIntegrationTest {

    private UUID USER_ID;
    private static final String EMAIL = "user@example.com";
    private static final String USERNAME = "user123";
    private static final String PASSWORD = "StrongPassword@123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void persistUser() {

        USER_ID = userCommandService.register(new RegistrationRequestWithRole(
                EMAIL,
                USERNAME,
                PASSWORD,
                Role.USER
        ));
    }

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("Returns 200 and user JSON when user exists")
        void returns200WhenUserExists() throws Exception {
            mockMvc.perform(
                            get("/api/v1/users/{id}", USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
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
    class GetUserByUserEmail {

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

    @Nested
    @DisplayName("PATCH /api/v1/users/username")
    class DeActivateUser {

        private String getAuthTokenViaHttp(String email) throws Exception {
            String loginPayload = """
                    {
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(email, UserControllerIntegrationTest.PASSWORD);

            String responseJson = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginPayload))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            return JsonPath.read(responseJson, "$.data.token");
        }

        @DisplayName("Returns 200 when admin tries to de-activate user")
        @Test
        void should_Return200_When_TargetRoleIsUser() throws Exception {

            // create admin
            String adminEmail = "admin@blogify.com";
            String adminUserName = "admin123";


            userCommandService.register(new RegistrationRequestWithRole(
                    adminEmail,
                    adminUserName,
                    passwordEncoder.encode(PASSWORD),
                    Role.ADMIN
            ));

            String token = getAuthTokenViaHttp(adminEmail);

            assertThat(token).isNotNull();
            assertThat(token).isNotBlank();

            mockMvc.perform(
                    patch("/api/v1/users/{id}/deactivate", USER_ID)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)

            ).andExpect(status().isOk());

            UserResponse updatedUser = userQueryService.getUserById(USER_ID);
            assertThat(updatedUser.isActive()).isFalse();

        }
    }


}