package com.mandeep.blogify.blog.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.query.CategoryQueryService;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.web.dto.CategoryWebRequest;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import com.mandeep.blogify.shared.dto.Response;
import com.mandeep.blogify.user.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.mandeep.blogify.shared.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private CategoryQueryService categoryQueryService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private UUID adminId;
    private static final String PASSWORD = "Password@123";

    private void assertCategoryResponse(CategoryResponse data) {
        assertThat(data).isNotNull();
        assertThat(data.id()).isNotNull();
        assertThat(data.title()).isNotBlank();
    }

    private <T> Response<T> extractResponse(MvcResult result, TypeReference<Response<T>> typeReference) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup Admin
        String adminEmail = "admin-" + UUID.randomUUID() + "@test.com";
        adminId = userFacade.createAdmin(adminEmail, "admin", passwordEncoder.encode(PASSWORD));
        adminToken = getAuthTokenViaHttp(adminEmail, PASSWORD, mockMvc);

        // Setup Regular User
        String userEmail = "user-" + UUID.randomUUID() + "@test.com";
        userFacade.createUser(userEmail, "user", passwordEncoder.encode(PASSWORD));
        userToken = getAuthTokenViaHttp(userEmail, PASSWORD, mockMvc);
    }

    @Nested
    @DisplayName("GET /api/v1/categories")
    class GetAllCategories {

        @Test
        @DisplayName("Returns 200 and paginated categories with specific parameters")
        void returns200AndPaginatedData() throws Exception {
            categoryCommandService.createCategory(new CategoryRequest("Tech", "Tech news", adminId));

            MvcResult result = mockMvc.perform(get("/api/v1/categories")
                            .param("pageNumber", "1")
                            .param("pageSize", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            Response<PaginatedResponse<CategoryResponse>> response = extractResponse(result, new TypeReference<>() {});

            assertSuccessResponse(response, "/api/v1/categories");
            assertThat(response.data().items()).isNotEmpty().allSatisfy(CategoryControllerIntegrationTest.this::assertCategoryResponse);
        }

        @Test
        @DisplayName("Returns 200 with default pagination when parameters are omitted")
        void returns200WithDefaultPagination() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            Response<PaginatedResponse<CategoryResponse>> response = extractResponse(result, new TypeReference<>() {});
            assertSuccessResponse(response, "/api/v1/categories");
            assertThat(response.data()).isNotNull();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/categories")
    class CreateCategory {

        @Test
        @DisplayName("Returns 201 when admin creates category")
        void returns201WhenAdminCreates() throws Exception {
            CategoryWebRequest request = new CategoryWebRequest("Health", "Health tips");

            MvcResult result = mockMvc.perform(post("/api/v1/categories")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Response<CategoryResponse> response = extractResponse(result, new TypeReference<>() {});
            assertSuccessResponse(response, "/api/v1/categories");
            assertCategoryResponse(response.data());
        }

        @Test
        @DisplayName("Returns 401 when request is unauthenticated")
        void returns401WhenUnauthenticated() throws Exception {
            CategoryWebRequest request = new CategoryWebRequest("Sports", "Sports news");

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Returns 403 when regular user attempts creation")
        void returns403WhenUserAttemptsCreation() throws Exception {
            CategoryWebRequest request = new CategoryWebRequest("Unauthorized", "Should fail");

            MvcResult result = mockMvc.perform(post("/api/v1/categories")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertErrorResponse(response, "/api/v1/categories", CommonException.accessDenied().getError());
        }

        @Test
        @DisplayName("Returns client error when category already exists")
        void returnsErrorWhenCategoryExists() throws Exception {
            String duplicateTitle = "Duplicate";
            categoryCommandService.createCategory(new CategoryRequest(duplicateTitle, "Desc 1", adminId));

            CategoryWebRequest request = new CategoryWebRequest(duplicateTitle, "Desc 2");

            MvcResult result = mockMvc.perform(post("/api/v1/categories")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError()) // Adapts to your global exception handler mapping
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertErrorResponse(response, "/api/v1/categories", CategoryException.categoryAlreadyExists(new CategoryTitle(duplicateTitle)).getError());
        }

        @Test
        @DisplayName("Returns 400 when input validation fails")
        void returns400WhenValidationFails() throws Exception {
            // Testing constraint validation with a blank title
            CategoryWebRequest request = new CategoryWebRequest("", "Valid Description");

            mockMvc.perform(post("/api/v1/categories")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/categories/{id}")
    class UpdateCategory {

        @Test
        @DisplayName("Returns 200 when admin updates existing category")
        void returns200WhenAdminUpdates() throws Exception {
            UUID categoryId = categoryCommandService.createCategory(
                    new CategoryRequest("Old Name", "Old Desc", adminId));

            CategoryWebRequest updateRequest = new CategoryWebRequest("New Name", "New Desc");

            MvcResult result = mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            Response<CategoryResponse> response = extractResponse(result, new TypeReference<>() {});
            assertSuccessResponse(response, "/api/v1/categories/" + categoryId);
            assertCategoryResponse(response.data());
            assertThat(response.data().title()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("Returns 403 when user attempts update")
        void returns403WhenUserAttemptsUpdate() throws Exception {
            UUID categoryId = categoryCommandService.createCategory(
                    new CategoryRequest("Target", "Desc", adminId));

            CategoryWebRequest updateRequest = new CategoryWebRequest("Hacked Name", "Desc");

            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Returns client error when updating to an already existing title")
        void returnsErrorWhenUpdatingToExistingTitle() throws Exception {
            categoryCommandService.createCategory(new CategoryRequest("Existing 1", "Desc", adminId));
            UUID categoryId2 = categoryCommandService.createCategory(new CategoryRequest("Existing 2", "Desc", adminId));

            // Attempt to update Category 2 to the title of Category 1
            CategoryWebRequest updateRequest = new CategoryWebRequest("Existing 1", "New Desc");

            MvcResult result = mockMvc.perform(put("/api/v1/categories/{id}", categoryId2)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().is4xxClientError())
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertErrorResponse(response, "/api/v1/categories/" + categoryId2, CategoryException.categoryAlreadyExists(new CategoryTitle("Existing 1")).getError());
        }

        @Test
        @DisplayName("Returns error when attempting to update non-existent category")
        void returnsErrorWhenCategoryNotFound() throws Exception {
            UUID randomId = UUID.randomUUID();
            CategoryWebRequest updateRequest = new CategoryWebRequest("Ghost", "Desc");

            MvcResult result = mockMvc.perform(put("/api/v1/categories/{id}", randomId)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().is4xxClientError())
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertErrorResponse(response, "/api/v1/categories/" + randomId, CategoryException.categoryNotFound(new CategoryId(randomId)).getError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/categories/{id}")
    class DeleteCategory {

        @Test
        @DisplayName("Returns 200 when admin deletes category")
        void returns200WhenAdminDeletes() throws Exception {
            UUID categoryId = categoryCommandService.createCategory(
                    new CategoryRequest("To Be Deleted", "Desc", adminId));

            MvcResult result = mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertSuccessResponse(response, "/api/v1/categories/" + categoryId);

            assertThatThrownBy(() -> categoryQueryService.getCategoryById(categoryId))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(categoryId)).getError()); // Fixed this assertion
        }

        @Test
        @DisplayName("Returns 403 when regular user attempts deletion")
        void returns403WhenUserAttemptsDeletion() throws Exception {
            UUID categoryId = categoryCommandService.createCategory(
                    new CategoryRequest("Safe Category", "Desc", adminId));

            mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Returns error when attempting to delete non-existent category")
        void returnsErrorWhenDeletingNonExistent() throws Exception {
            UUID randomId = UUID.randomUUID();

            MvcResult result = mockMvc.perform(delete("/api/v1/categories/{id}", randomId)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError())
                    .andReturn();

            Response<Void> response = extractResponse(result, new TypeReference<>() {});
            assertErrorResponse(response, "/api/v1/categories/" + randomId, CategoryException.categoryNotFound(new CategoryId(randomId)).getError());
        }
    }
}