package com.mandeep.blogify.shared.utils;

import com.jayway.jsonpath.JsonPath;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.dto.Response;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TestUtils {

    private TestUtils() {
    }

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
        assertThat(response.error()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    public static String getAuthTokenViaHttp(String email, String password, MockMvc mockMvc) throws Exception {
        String loginPayload = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        String responseJson = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(responseJson, "$.data.token");
    }
}
