package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.dto.PostPageItemResponse;
import com.mandeep.blogify.blog.application.dto.PostResponse;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("PostQueryService Integration Tests")
class PostQueryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PostQueryService postQueryService;

    //region Test Data
    private static final UUID AUTHOR_ID = UUID.fromString("019d0253-ef34-7361-ba13-0516d43c4a11");
    private static final String AUTHOR_USERNAME = "john doe";
    // Must be >= 100 characters to pass the DB CHECK constraint
    private static final String VALID_CONTENT = "A".repeat(100);
    //endregion

    //region Helper Methods

    private void persistUser() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        jdbcTemplate.update("""
                INSERT INTO users (id, email, user_name, password, is_active, role, created_at, last_modified_at, version)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """,
                AUTHOR_ID,
                AUTHOR_USERNAME + "@blogify.com",
                AUTHOR_USERNAME,
                "HashedPass@123",
                true,
                Role.USER.name(),
                now, now, 0L
        );
    }

    private CategoryEntity persistCategory(String title) {
        CategoryEntity category = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description("Description for " + title)
                .status(CategoryStatus.ACTIVE)
                .build();
        persist(category);
        return category;
    }

    private UUID persistPost(UUID authorId, String title, String slug, PostStatus status, Set<CategoryEntity> categories) {
        return persistPost(authorId, title, slug, status, categories, Instant.now());
    }

    private UUID persistPost(UUID authorId, String title, String slug, PostStatus status, Set<CategoryEntity> categories, Instant publishedAt) {
        PostEntity post = PostEntity.builder()
                .id(UUID.randomUUID())
                .title(title)
                .slug(slug)
                .content(VALID_CONTENT)
                .authorId(authorId)
                .categories(categories)
                .status(status)
                .publishAt(status == PostStatus.PUBLISHED ? publishedAt : null)
                .build();
        persist(post);
        return post.getId();
    }
    //endregion

    // -------------------------------------------------------------------------
    // Get Post By Id
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getPostById()")
    class GetPostById {

        @Test
        @DisplayName("Successfully retrieves and maps an existing post with valid author data")
        void should_ReturnPostResponse_When_PostAndAuthorExist() {
            // Arrange
            persistUser();
            CategoryEntity category = persistCategory("Java Programming");
            UUID postId = persistPost(AUTHOR_ID, "Mastering Java", "mastering-java", PostStatus.PUBLISHED, Set.of(category));

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
                    () -> assertThat(response.categories().iterator().next().title()).isEqualTo("Java Programming"),
                    () -> assertThat(response.publishedAt()).isNotNull()
            );

        }

        @Test
        @DisplayName("Successfully retrieves a post regardless of its status (DRAFT/ARCHIVED)")
        void should_ReturnPostResponse_When_PostIsDraftOrArchived() {
            // Arrange
            persistUser();
            CategoryEntity category = persistCategory("Spring Boot");
            UUID draftPostId = persistPost(AUTHOR_ID, "Draft Post", "draft-post", PostStatus.DRAFT, Set.of(category));
            UUID archivedPostId = persistPost(AUTHOR_ID, "Archived Post", "archived-post", PostStatus.ARCHIVED, Set.of(category));

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
            persistUser();
            CategoryEntity category = persistCategory("Cloud Computing");

            // Persist 3 published posts
            persistPost(AUTHOR_ID, "Post Title 1", "post-1", PostStatus.PUBLISHED, Set.of(category));
            persistPost(AUTHOR_ID, "Post Title 2", "post-2", PostStatus.PUBLISHED, Set.of(category));
            persistPost(AUTHOR_ID, "Post Title 3", "post-3", PostStatus.PUBLISHED, Set.of(category));

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
            // Arrange
            persistUser();
            CategoryEntity category = persistCategory("Testing");

            persistPost(AUTHOR_ID, "Published Post", "pub-1", PostStatus.PUBLISHED, Set.of(category));
            persistPost(AUTHOR_ID, "Draft Post", "draft-1", PostStatus.DRAFT, Set.of(category));
            persistPost(AUTHOR_ID, "Archived Post", "arch-1", PostStatus.ARCHIVED, Set.of(category));

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
            persistUser();
            CategoryEntity category = persistCategory("Empty Test");

            // Only DRAFT post exists, no PUBLISHED
            persistPost(AUTHOR_ID, "Only Draft", "only-draft", PostStatus.DRAFT, Set.of(category));

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
            persistUser();
            CategoryEntity category = persistCategory("Bounds Test");

            // 1 Post exists (so there is exactly 1 page of size 10: page 0)
            persistPost(AUTHOR_ID, "Valid Post", "valid-post", PostStatus.PUBLISHED, Set.of(category));

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