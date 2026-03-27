package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.command.PostCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.PostPageItemResponse;
import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.application.dto.PostResponse;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import com.mandeep.blogify.user.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("PostQueryService Integration Tests")
class PostQueryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private PostCommandService postCommandService;

    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private UserFacade userFacade;

    //region Test Data
    private UUID AUTHOR_ID;
    private UUID CATEGORY_ID;
    private static final String AUTHOR_USERNAME = "john_doe";
    private static final String CATEGORY_TITLE = "Java";
    // Must be >= 100 characters to pass the DB CHECK constraint
    private static final String VALID_CONTENT = "A".repeat(100);
    //endregion

    @BeforeEach
    public void setup() {
        AUTHOR_ID = userFacade.createUser("user@blogify.com", AUTHOR_USERNAME, "Strong@123");
        CATEGORY_ID = categoryCommandService.createCategory(new CategoryRequest(
                CATEGORY_TITLE,
                "description",
                userFacade.createAdmin("admin@blogify.com", "admin123", "Strong@123")
        ));
    }


    private UUID persistPost(String title) {

        return postCommandService.createPost(new PostRequest(
                title,
                VALID_CONTENT,
                List.of(CATEGORY_ID),
                AUTHOR_ID
        ));
    }
    //endregion


    @Nested
    @DisplayName("getPostById()")
    class GetPostById {

        @Test
        @DisplayName("Successfully retrieves and maps an existing post with valid author data")
        void should_ReturnPostResponse_When_PostAndAuthorExist() {
            // Arrange

            UUID postId = persistPost("Mastering Java");
            postCommandService.publishPost(postId, AUTHOR_ID);

            // Act
            PostResponse response = postQueryService.getPostById(postId);

            // Assert
            assertAll(
                    () -> assertThat(response).isNotNull(),
                    () -> assertThat(response.postId()).isEqualTo(postId),
                    () -> assertThat(response.title()).isEqualTo("Mastering Java"),
                    () -> assertThat(response.slug()).isEqualTo("mastering-java"),
                    () -> assertThat(response.content()).isEqualTo(VALID_CONTENT),
                    () -> assertThat(response.status()).isEqualTo(PostStatus.PUBLISHED.name()),
                    () -> assertThat(response.authorData().id()).isEqualTo(AUTHOR_ID),
                    () -> assertThat(response.authorData().userName()).isEqualTo(AUTHOR_USERNAME),
                    () -> assertThat(response.categories()).hasSize(1),
                    () -> assertThat(response.categories().iterator().next().title()).isEqualTo(CATEGORY_TITLE),
                    () -> assertThat(response.publishedAt()).isNotNull()
            );

        }

        @Test
        @DisplayName("Successfully retrieves a post regardless of its status (DRAFT/ARCHIVED)")
        void should_ReturnPostResponse_When_PostIsDraftOrArchived() {
            // Arrange
            UUID draftPostId = persistPost("Draft Post");
            UUID archivedPostId = persistPost("Archived Post");
            postCommandService.deletePost(archivedPostId, AUTHOR_ID);

            // Act & Assert
            PostResponse draftResponse = postQueryService.getPostById(draftPostId);
            PostResponse archivedResponse = postQueryService.getPostById(archivedPostId);

            assertThat(draftResponse.status()).isEqualTo(PostStatus.DRAFT.name());
            assertThat(archivedResponse.status()).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Throws PostException when post does not exist")
        void should_ThrowException_When_PostDoesNotExist() {
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postQueryService.getPostById(nonExistentPostId))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        /*
         * Note: A test for "Author Not Found" exception is omitted because the
         * 'fk_posts_author' database constraint guarantees that an author
         * must exist for a post to be created. Data integrity is enforced at the DB level.
         */
    }

    @Nested
    @DisplayName("getAllPublishedPosts()")
    class GetAllPublishedPosts {

        @Test
        @DisplayName("Successfully returns paginated data with correctly mapped authors")
        void should_ReturnPaginatedPublishedPosts() {
            // Arrange

            // Persist 3 published posts
            UUID postId1 = persistPost("Post Title 1");
            UUID postId2 = persistPost("Post Title 2");
            UUID postId3 = persistPost("Post Title 3");

            postCommandService.publishPost(postId1, AUTHOR_ID);
            postCommandService.publishPost(postId2, AUTHOR_ID);
            postCommandService.publishPost(postId3, AUTHOR_ID);


            // Act - Fetch page 0, size 2
            PaginatedResponse<PostPageItemResponse> pageZero = postQueryService.getAllPublishedPosts(0, 2);

            // Assert
            assertAll(
                    () -> assertThat(pageZero.items()).hasSize(2),
                    () -> assertThat(pageZero.pageNumber()).isEqualTo(0),
                    () -> assertThat(pageZero.pageSize()).isEqualTo(2),
                    () -> assertThat(pageZero.totalItems()).isEqualTo(3),
                    () -> assertThat(pageZero.totalPages()).isEqualTo(2),
                    () -> assertThat(pageZero.lastPage()).isFalse()
            );

            // Verify Author Mapping
            PostPageItemResponse firstItem = pageZero.items().getFirst();
            assertThat(firstItem.authorData().userName()).isEqualTo(AUTHOR_USERNAME);

            // Assert Page 1
            // Act - Fetch page 1, size 2 (The remaining 1 item)
            PaginatedResponse<PostPageItemResponse> pageOne = postQueryService.getAllPublishedPosts(1, 2);

            assertAll(
                    () -> assertThat(pageOne.items()).hasSize(1), // Only 1 item left!
                    () -> assertThat(pageOne.pageNumber()).isEqualTo(1),
                    () -> assertThat(pageOne.totalItems()).isEqualTo(3),
                    () -> assertThat(pageOne.totalPages()).isEqualTo(2),
                    () -> assertThat(pageOne.lastPage()).isTrue() // It is the last page!
            );

            // Ensure Page 1 contains a different item than Page 0
            assertThat(pageZero.items().getFirst().postId())
                    .isNotEqualTo(pageOne.items().getFirst().postId());
        }

        @Test
        @DisplayName("Strictly excludes DRAFT and ARCHIVED posts from the results")
        void should_ExcludeDraftAndArchivedPosts() {
            // Arrange;

            UUID publishedPostId = persistPost("Published Post");
            postCommandService.publishPost(publishedPostId, AUTHOR_ID);

            persistPost("Draft Post");

            UUID archivedPostId = persistPost("Archived Post");
            postCommandService.deletePost(archivedPostId, AUTHOR_ID);

            // Act
            PaginatedResponse<PostPageItemResponse> response = postQueryService.getAllPublishedPosts(0, 10);

            // Assert
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().getFirst().title()).isEqualTo("Published Post");
            assertThat(response.totalItems()).isEqualTo(1);
        }

        @Test
        @DisplayName("Returns empty paginated response when no published posts exist")
        void should_ReturnEmptyResponse_When_NoPublishedPostsExist() {
            // Arrange

            // Only DRAFT post exists, no PUBLISHED
            persistPost("Only Draft Post");

            // Act
            PaginatedResponse<PostPageItemResponse> response = postQueryService.getAllPublishedPosts(0, 10);

            // Assert
            assertAll(
                    () -> assertThat(response.items()).isEmpty(),
                    () -> assertThat(response.totalItems()).isEqualTo(0),
                    () -> assertThat(response.totalPages()).isEqualTo(0),
                    () -> assertThat(response.lastPage()).isTrue()
            );
        }

        @Test
        @DisplayName("Returns empty items array when requested page exceeds total pages")
        void should_ReturnEmptyItems_When_PageOutOfBounds() {
            // Arrange

            // 1 Post exists (so there is exactly 1 page of size 10: page 0)
            persistPost("Valid Post");

            // Act - Fetch page 1 (which is the 2nd page)
            PaginatedResponse<PostPageItemResponse> response = postQueryService.getAllPublishedPosts(1, 10);

            // Assert
            assertAll(
                    () -> assertThat(response.items()).isEmpty(),
                    () -> assertThat(response.pageNumber()).isEqualTo(1),
                    () -> assertThat(response.lastPage()).isTrue()
            );
        }


    }
}