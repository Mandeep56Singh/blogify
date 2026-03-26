package com.mandeep.blogify.auth.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandeep.blogify.auth.infrastructure.security.RsaKeyProperties;
import com.mandeep.blogify.auth.web.dto.LoginWebRequest;
import com.mandeep.blogify.auth.web.dto.LoginWebResponse;
import com.mandeep.blogify.auth.web.dto.SignUpWebRequest;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.AppConstants;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import com.mandeep.blogify.shared.dto.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RsaKeyProperties rsaKeyProperties;

    @Autowired
    private ObjectMapper objectMapper;


    private static final String email = "user@example.com";
    private static final String username = "user123";
    private static final String password = "StrongPass2!";
    private static final String sign_up_url = "/api/v1/auth/signup";
    private static final String login_url = "/api/v1/auth/login";


    public static <T> void assertErrorResponse(
            Response<T> response,
            String endpoint,
            DomainError domainError
    ) {
        assertThat(response.success()).isFalse();
        assertThat(response.data()).isNull();
        assertThat(response.metaData()).isNull();
        assertThat(response.timestamp()).isNotNull();
        assertThat(response.instance()).endsWith(endpoint);
        assertThat(response.error()).isNotNull();
        assertThat(response.error().status()).isEqualTo(AppUtils.resolveStatus(domainError.errorType()));
        assertThat(response.error().errorCode()).isEqualTo(domainError.errorCode());
    }

    public static <T> void assertSuccessResponse(
            Response<T> response,
            String expectedEndpoint
    ) {

        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.instance()).isNotBlank();
        assertThat(response.instance()).endsWith(expectedEndpoint);
        assertThat(response.data()).isNotNull();
        assertThat(response.metaData()).isNull();
        assertThat(response.error()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    public static void assertLoginResponse(
            LoginWebResponse loginResponse,
            long expiresIn
    ) {

        assertThat(loginResponse).isNotNull();

        // token
        assertThat(loginResponse.token()).isNotBlank();
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");
        assertThat(loginResponse.expiresIn()).isEqualTo(expiresIn);

        // user
        assertThat(loginResponse.user()).isNotNull();
        assertThat(loginResponse.user().id()).isNotNull();
        assertThat(loginResponse.user().role()).isEqualTo("USER");
    }

    @Nested
    @DisplayName("Test for logging in user")
    class LoginUser {

        @Test
        @DisplayName("Return 200 http response when user logs in successfully")
        void should_Return200_When_UserLoggedIn() throws Exception {

            // First register the user
            mockMvc.perform(post(sign_up_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new SignUpWebRequest(email, username, password)
                            )))
                    .andExpect(status().isCreated());

            // Then login
            LoginWebRequest loginRequest = new LoginWebRequest(email, password);

            MvcResult result = mockMvc.perform(post(login_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {}
            );

            assertSuccessResponse(response, login_url);
            assertLoginResponse(response.data(), rsaKeyProperties.tokenExpireIn().getSeconds());
        }

        @ParameterizedTest
        @MethodSource("invalidLoginRequestProvider")
        @DisplayName("Return 400 http response when login validation fails")
        void should_Return400_When_LoginValidationFailed(LoginWebRequest request) throws Exception {

            MvcResult result = mockMvc.perform(post(login_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {}
            );

            assertErrorResponse(response, login_url, CommonError.VALIDATION_FAILED);
        }

        @Test
        @DisplayName("Return 401 when password is incorrect")
        void should_Return401_When_PasswordIsWrong() throws Exception {

            mockMvc.perform(post(sign_up_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new SignUpWebRequest(email, username, password)
                            )))
                    .andExpect(status().isCreated());

            LoginWebRequest loginRequest = new LoginWebRequest(email, "WrongPass1!");

            MvcResult result = mockMvc.perform(post(login_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {}
            );

            DomainError error = CommonException.invalidCredentials().getError();

            assertErrorResponse(response, login_url, error);
        }

        @Test
        @DisplayName("Return 401 when email does not exist")
        void should_Return401_When_EmailDoesNotExist() throws Exception {

            LoginWebRequest loginRequest = new LoginWebRequest("nonexistent@example.com", password);

            MvcResult result = mockMvc.perform(post(login_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {}
            );

            DomainError error = CommonException.invalidCredentials().getError();

            assertErrorResponse(response, login_url, error);
        }

        private static Stream<LoginWebRequest> invalidLoginRequestProvider() {
            return Stream.of(
                    new LoginWebRequest("", password),
                    new LoginWebRequest(null, password),
                    new LoginWebRequest("notanemail", password),
                    new LoginWebRequest(email, ""),
                    new LoginWebRequest(email, null)
            );
        }
    }

    @Nested
    @DisplayName("Test for signing up user")
    class SignupUser {

        @Test
        @DisplayName("Return 201 http response when user signed up successfully")
        void should_Return201_When_UserSignedIn() throws Exception {

            SignUpWebRequest signUpWebRequest = new SignUpWebRequest(
                    email,
                    username,
                    password
            );

            // response from sign up
            MvcResult result = mockMvc.perform(post(sign_up_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpWebRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            // De-serialization
            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson,
                    new TypeReference<>() {
                    }
            );


            // assertion response structure checks
            assertSuccessResponse(response, sign_up_url);

            // asserting response data check
            LoginWebResponse loginResponse = response.data();
            assertLoginResponse(loginResponse, rsaKeyProperties.tokenExpireIn().getSeconds());

        }

        @ParameterizedTest
        @MethodSource("invalidationRequestProvider")
        @DisplayName("Return 400 http response when validation failed")
        void should_Return400_When_UserSignUpValidationFailed(SignUpWebRequest request) throws Exception {


            MvcResult result = mockMvc.perform(post(sign_up_url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {
                    }
            );

            assertErrorResponse(response, sign_up_url, CommonError.VALIDATION_FAILED);

        }

        @Test
        @DisplayName("Return 409 when request email already exists")
        void should_Return409_When_EmailAlreadyExists() throws Exception {

            var firstRequest = new SignUpWebRequest(
                    email,
                    username,
                    password
            );

            mockMvc.perform(
                            post(sign_up_url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(firstRequest))
                    )
                    .andExpect(status().isCreated());

            var requestWithDuplicateEmail = new SignUpWebRequest(
                    firstRequest.email(),
                    "duplicate user",
                    password
            );

            MvcResult result = mockMvc.perform(
                            post(sign_up_url)
                                    .content(objectMapper.writeValueAsString(requestWithDuplicateEmail))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isConflict())
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {
                    }
            );

            assertErrorResponse(response, sign_up_url, CommonError.EMAIL_ALREADY_EXISTS);

        }

        @Test
        @DisplayName("Return 409 when request username already exists")
        void should_Return409_When_UserNameAlreadyExists() throws Exception {

            var firstRequest = new SignUpWebRequest(
                    email,
                    username,
                    password
            );

            mockMvc.perform(
                            post(sign_up_url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(firstRequest))
                    )
                    .andExpect(status().isCreated());

            var requestWithDuplicateUsername = new SignUpWebRequest(
                    "random@gmail.com",
                    username,
                    password
            );

            MvcResult result = mockMvc.perform(
                            post(sign_up_url)
                                    .content(objectMapper.writeValueAsString(requestWithDuplicateUsername))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isConflict())
                    .andReturn();

            String rawJson = result.getResponse().getContentAsString();

            Response<LoginWebResponse> response = objectMapper.readValue(
                    rawJson, new TypeReference<>() {
                    }
            );

            assertErrorResponse(response, sign_up_url, CommonError.USERNAME_ALREADY_EXISTS);

        }

        private static Stream<SignUpWebRequest> invalidationRequestProvider() {
            return Stream.of(
                    new SignUpWebRequest("", username, password),
                    new SignUpWebRequest(null, username, password),
                    new SignUpWebRequest("email", username, password),

                    new SignUpWebRequest(email, "", password),
                    new SignUpWebRequest(email, null, password),
                    new SignUpWebRequest(email, "a".repeat(AppConstants.USER_NAME_MIN_LENGTH - 1), password),
                    new SignUpWebRequest(email, "a".repeat(AppConstants.USER_NAME_MAX_LENGTH + 1), password),

                    new SignUpWebRequest(email, username, ""),
                    new SignUpWebRequest(email, username, null),
                    new SignUpWebRequest(email, username, "42fsaf")

            );
        }


    }

}