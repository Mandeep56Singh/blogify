package com.mandeep.blogify.blog.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.command.PostCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.PostPageItemResponse;
import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.application.dto.PostResponse;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.web.dto.PostWebRequest;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
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
import java.util.List;
import java.util.UUID;

import static com.mandeep.blogify.shared.utils.TestUtils.getAuthTokenViaHttp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PostControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostCommandService postCommandService;



    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String authorToken;
    private String otherUserToken;
    private UUID adminId;
    private UUID authorId;
    private UUID categoryId;
    
    private static final String content = "a".repeat(101);
    private static final String PASSWORD = "Password@123";

    private <T> Response<T> extractResponse(MvcResult result, TypeReference<Response<T>> type) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), type);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup Users
        String adminEmail = "admin@test.com";
        String adminUserName = "admin";
        adminId = userFacade.createAdmin(adminEmail, adminUserName, passwordEncoder.encode(PASSWORD));
        adminToken = getAuthTokenViaHttp(adminEmail, PASSWORD, mockMvc);

        String authorEmail = "author@test.com";
        String authorEmail2 = "author2@test.com";
        authorId = userFacade.createUser(authorEmail, "author", passwordEncoder.encode(PASSWORD));
        authorToken = getAuthTokenViaHttp(authorEmail, PASSWORD, mockMvc);

        UUID otherId = userFacade.createUser(authorEmail2, "author2", passwordEncoder.encode(PASSWORD));
        otherUserToken = getAuthTokenViaHttp(authorEmail2, PASSWORD, mockMvc);

        // Set up a Category
        categoryId = categoryCommandService.createCategory(new CategoryRequest("Java", "Java Programming", adminId));
    }

    @Nested
    @DisplayName("GET /api/v1/posts")
    class GetAllPosts {

        @Test
        @DisplayName("Returns only published posts")
        void returnsOnlyPublishedPosts() throws Exception {
            // Correct order: title, content, categoryIds, authorId
            UUID postId = postCommandService.createPost(new PostRequest("Title", content, List.of(categoryId), authorId));
            postCommandService.publishPost(postId, authorId);

            postCommandService.createPost(new PostRequest("Draft", content, List.of(categoryId), authorId));

            MvcResult result = mockMvc.perform(get("/api/v1/posts")
                            .param("pageNumber", "1")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andReturn();

            var response = extractResponse(result, new TypeReference<Response<PaginatedResponse<PostPageItemResponse>>>() {
            });

            assertThat(response.data().items()).hasSize(1);
            assertThat(response.data().items().get(0).title()).isEqualTo("Title");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/posts")
    class CreatePost {

        @Test
        @DisplayName("Returns 201 and creates a draft post")
        void returns201WhenAuthorCreatesPost() throws Exception {
            PostWebRequest request = new PostWebRequest("Integration Test Post", content, List.of(categoryId));

            MvcResult result = mockMvc.perform(post("/api/v1/posts")
                            .header("Authorization", "Bearer " + authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var response = extractResponse(result, new TypeReference<Response<PostResponse>>() {
            });
            assertThat(response.data().title()).isEqualTo("Integration Test Post");
            assertThat(response.data().status()).isEqualTo(PostStatus.DRAFT.name());
        }

        @Test
        @DisplayName("Returns error when category IDs do not exist")
        void returns400WhenCategoriesMissing() throws Exception {
            UUID fakeCategory = UUID.randomUUID();
            PostWebRequest request = new PostWebRequest("Invalid Post", content, List.of(fakeCategory));

            mockMvc.perform(post("/api/v1/posts")
                            .header("Authorization", "Bearer " + authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/posts/{id}")
    class UpdatePost {

        @Test
        @DisplayName("Returns 200 when author updates their own post")
        void returns200WhenAuthorUpdatesOwnPost() throws Exception {
            UUID postId = postCommandService.createPost(new PostRequest("Old Title", content, List.of(categoryId), authorId));
            PostWebRequest updateRequest = new PostWebRequest("New Title", content, List.of(categoryId));

            mockMvc.perform(put("/api/v1/posts/{id}", postId)
                            .header("Authorization", "Bearer " + authorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("New Title"));
        }

        @Test
        @DisplayName("Returns 403 when another user tries to update")
        void returns403WhenOtherUserUpdates() throws Exception {
            UUID postId = postCommandService.createPost(new PostRequest("Author Post", content, List.of(categoryId), authorId));
            PostWebRequest updateRequest = new PostWebRequest("Hacker Title", content, List.of(categoryId));

            mockMvc.perform(put("/api/v1/posts/{id}", postId)
                            .header("Authorization", "Bearer " + otherUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/posts/{id}/publish")
    class PublishPost {

        @Test
        @DisplayName("Successfully changes status to PUBLISHED")
        void returns200OnPublish() throws Exception {
            UUID postId = postCommandService.createPost(new PostRequest("Draft Post", content, List.of(categoryId), authorId));

            mockMvc.perform(patch("/api/v1/posts/{id}/publish", postId)
                            .header("Authorization", "Bearer " + authorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/posts/{id}")
    class DeletePost {

        @Test
        @DisplayName("Soft deletes the post successfully")
        void returns200OnDelete() throws Exception {
            UUID postId = postCommandService.createPost(new PostRequest("To Delete", content, List.of(categoryId), authorId));

            mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                            .header("Authorization", "Bearer " + authorToken))
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("Returns 403 when unauthorized user tries delete")
        void returns403OnUnauthorizedDelete() throws Exception {
            UUID postId = postCommandService.createPost(new PostRequest("Author Post", content, List.of(categoryId), authorId));

            mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                            .header("Authorization", "Bearer " + otherUserToken))
                    .andExpect(status().isForbidden());
        }
    }
}