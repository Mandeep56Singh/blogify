package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("PostCommandService Integration Tests")
class PostCommandServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PostCommandService postCommandService;

    //region Test Data
    private static final UUID ACTOR_ID = UUID.fromString("019d0253-ef34-7361-ba13-0516d43c4ab8");
    private static final UUID CATEGORY_ID = UUID.fromString("019d0253-ef34-7361-ba13-0516d43c4ab9");
    private static final String VALID_TITLE = "My First Post About Java";
    private static final String VALID_CONTENT = "A".repeat(100);
    //endregion

    //region Helper Methods

    private void persistUser(UUID userId, boolean isActive, Role role) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        jdbcTemplate.update("""
                INSERT INTO users (id, email, user_name, password, is_active, role, created_at, last_modified_at, version)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                userId + "@blogify.com",
                "user_" + userId.toString().substring(0, 8),
                "HashedPass@123",
                isActive,
                role.name(),
                now, now, 0L
        );
    }

    private CategoryEntity persistCategory() {
        CategoryEntity category = CategoryEntity.builder()
                .id(CATEGORY_ID)
                .title("Category " + PostCommandServiceIntegrationTest.CATEGORY_ID.toString().substring(0, 8))
                .description("Some description for category")
                .status(CategoryStatus.ACTIVE)
                .build();
        persist(category);
        return category;
    }

    private UUID persistPost(UUID authorId, String title, String slug, PostStatus status, Set<CategoryEntity> categories) {
        UUID postId = UUID.randomUUID();
        PostEntity post = PostEntity.builder()
                .id(postId)
                .title(title)
                .slug(slug)
                .content(VALID_CONTENT)
                .authorId(authorId)
                .categories(categories)
                .status(status)
                .build();
        persist(post);
        return postId;
    }

    private PostRequest buildRequest(String title, List<UUID> categoryIds) {
        return new PostRequest(title, VALID_CONTENT, categoryIds, ACTOR_ID);
    }

    private void givenActiveUser() {
        persistUser(ACTOR_ID, true, Role.USER);
    }

    private void givenActiveAdmin() {
        persistUser(ACTOR_ID, true, Role.ADMIN);
    }

    private void givenInactiveUser() {
        persistUser(ACTOR_ID, false, Role.USER);
    }

    // Direct DB reads — no session, no lazy loading, no lies
    private String getPostStatus(UUID postId) {
        return jdbcTemplate.queryForObject(
                "SELECT status FROM posts WHERE id = ?",
                String.class, postId
        );
    }

    private String getPostTitle(UUID postId) {
        return jdbcTemplate.queryForObject(
                "SELECT title FROM posts WHERE id = ?",
                String.class, postId
        );
    }

    private String getPostSlug(UUID postId) {
        return jdbcTemplate.queryForObject(
                "SELECT slug FROM posts WHERE id = ?",
                String.class, postId
        );
    }

    private UUID getPostAuthorId(UUID postId) {
        return jdbcTemplate.queryForObject(
                "SELECT author_id FROM posts WHERE id = ?",
                UUID.class, postId
        );
    }

    private boolean postExists(UUID postId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE id = ?",
                Integer.class, postId
        );
        return count != null && count > 0;
    }

    //endregion

    // -------------------------------------------------------------------------
    // Create Post
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("createPost()")
    class CreatePost {

        @Test
        @DisplayName("Successfully creates post and persists it")
        void should_CreatePost_When_RequestIsValid() {
            givenActiveUser();
            persistCategory();

            UUID postId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            );

            assertAll(
                    () -> assertThat(postExists(postId)).isTrue(),
                    () -> assertThat(getPostTitle(postId)).isEqualTo(VALID_TITLE),
                    () -> assertThat(getPostStatus(postId)).isEqualTo(PostStatus.DRAFT.name()),
                    () -> assertThat(getPostAuthorId(postId)).isEqualTo(ACTOR_ID)
            );
        }

        @Test
        @DisplayName("Admin can also create a post")
        void should_CreatePost_When_AuthorIsAdmin() {
            givenActiveAdmin();
            persistCategory();

            UUID postId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            );

            assertThat(postExists(postId)).isTrue();
        }

        @Test
        @DisplayName("Generates unique slug when a post with the same title already exists")
        void should_GenerateUniqueSlug_When_PostWithSameTitleExists() {
            givenActiveUser();
            CategoryEntity category = persistCategory();

            persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            UUID newPostId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            );

            String newSlug = getPostSlug(newPostId);

            assertAll(
                    () -> assertThat(postExists(newPostId)).isTrue(),
                    () -> assertThat(newSlug).isNotEqualTo("my-first-post-about-java"),
                    () -> assertThat(newSlug).contains("my-first-post-about-java")
            );
        }

        @Test
        @DisplayName("Generates unique slug even when archived post with same title exists")
        void should_GenerateUniqueSlug_When_ArchivedPostWithSameTitleExists() {
            givenActiveUser();
            CategoryEntity category = persistCategory();

            persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.ARCHIVED, Set.of(category));

            UUID newPostId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            );

            assertThat(postExists(newPostId)).isTrue();
        }

        @Test
        @DisplayName("Throws AccountException when author is not found")
        void should_ThrowException_When_AuthorNotFound() {
            persistCategory();

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when author is inactive")
        void should_ThrowException_When_AuthorIsInactive() {
            givenInactiveUser();
            persistCategory();

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws PostException when some categories do not exist")
        void should_ThrowException_When_CategoryNotFound() {
            givenActiveUser();

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCategoriesNotFound(
                            Set.of(new CategoryId(CATEGORY_ID))
                    ).getError());
        }
    }

    // -------------------------------------------------------------------------
    // Update Post
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("updatePost()")
    class UpdatePost {

        @Test
        @DisplayName("Successfully updates post when requester is the author")
        void should_UpdatePost_When_RequesterIsAuthor() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            String newTitle = "Updated Post Title Here";
            postCommandService.updatePost(postId, buildRequest(newTitle, List.of(CATEGORY_ID)));

            assertThat(getPostTitle(postId)).isEqualTo(newTitle);
        }

        @Test
        @DisplayName("Admin can update post regardless of who created it")
        void should_UpdatePost_When_RequesterIsActiveAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveAdmin();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            String newTitle = "Admin Updated This Title";
            postCommandService.updatePost(postId, buildRequest(newTitle, List.of(CATEGORY_ID)));

            assertThat(getPostTitle(postId)).isEqualTo(newTitle);
        }

        @Test
        @DisplayName("Updating to a title that already exists succeeds since slug is not regenerated on update")
        void should_UpdatePost_When_TitleAlreadyExistsInAnotherPost() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));
            persistPost(ACTOR_ID, "Another Post Title", "another-post-title", PostStatus.DRAFT, Set.of(category));

            postCommandService.updatePost(postId, buildRequest("Another Post Title", List.of(CATEGORY_ID)));

            assertThat(getPostTitle(postId)).isEqualTo("Another Post Title");
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            givenActiveUser();
            persistCategory();
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.updatePost(nonExistentPostId,
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws PostException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest("New Title For Update", List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.unauthorized("").getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            assertThatThrownBy(() -> postCommandService.updatePost(UUID.randomUUID(),
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            givenInactiveUser();

            assertThatThrownBy(() -> postCommandService.updatePost(UUID.randomUUID(),
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws AccountException when inactive admin tries to update")
        void should_ThrowException_When_AdminIsInactive() {
            persistUser(ACTOR_ID, false, Role.ADMIN);

            assertThatThrownBy(() -> postCommandService.updatePost(UUID.randomUUID(),
                    buildRequest(VALID_TITLE, List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws PostException when some categories do not exist")
        void should_ThrowException_When_CategoryNotFound() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));
            UUID nonExistentCategoryId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest(VALID_TITLE, List.of(nonExistentCategoryId))
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCategoriesNotFound(
                            Set.of(new CategoryId(nonExistentCategoryId))
                    ).getError());
        }

        @Test
        @DisplayName("Throws PostException when updating an archived post")
        void should_ThrowException_When_PostIsArchived() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.ARCHIVED, Set.of(category));

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest("Some New Title Here", List.of(CATEGORY_ID))
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCannotBeUpdatedWhenArchived().getError());
        }
    }

    // -------------------------------------------------------------------------
    // Delete Post
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("deletePost()")
    class DeletePost {

        @Test
        @DisplayName("Successfully archives post when requester is the author")
        void should_DeletePost_When_RequesterIsAuthor() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            postCommandService.deletePost(postId, ACTOR_ID);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Active admin can delete post they did not author")
        void should_DeletePost_When_RequesterIsActiveAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveAdmin();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            postCommandService.deletePost(postId, ACTOR_ID);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Deleting an already archived post is idempotent")
        void should_Succeed_When_PostIsAlreadyArchived() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.ARCHIVED, Set.of(category));

            postCommandService.deletePost(postId, ACTOR_ID);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            givenActiveUser();
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.deletePost(nonExistentPostId, ACTOR_ID))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws PostException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            assertThatThrownBy(() -> postCommandService.deletePost(postId, ACTOR_ID))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.unauthorized("").getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            assertThatThrownBy(() -> postCommandService.deletePost(UUID.randomUUID(), ACTOR_ID))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            givenInactiveUser();

            assertThatThrownBy(() -> postCommandService.deletePost(UUID.randomUUID(), ACTOR_ID))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws AccountException when inactive admin tries to delete")
        void should_ThrowException_When_AdminIsInactive() {
            persistUser(ACTOR_ID, false, Role.ADMIN);

            assertThatThrownBy(() -> postCommandService.deletePost(UUID.randomUUID(), ACTOR_ID))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }
    }

    // -------------------------------------------------------------------------
    // Publish Post
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("publishPost()")
    class PublishPost {

        @Test
        @DisplayName("Successfully publishes post when requester is the author and post is in draft")
        void should_PublishPost_When_RequesterIsAuthor() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            postCommandService.publishPost(postId, ACTOR_ID);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Active admin can publish post they did not author")
        void should_PublishPost_When_RequesterIsActiveAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveAdmin();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            postCommandService.publishPost(postId, ACTOR_ID);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Publishing an already published post is idempotent and succeeds")
        void should_Succeed_When_PostIsAlreadyPublished() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            // Post is already published
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.PUBLISHED, Set.of(category));

            postCommandService.publishPost(postId, ACTOR_ID);

            // Status should remain published without throwing any exceptions
            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Throws PostException when attempting to publish an archived post")
        void should_ThrowException_When_PostIsArchived() {
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(ACTOR_ID, VALID_TITLE, "my-first-post-about-java", PostStatus.ARCHIVED, Set.of(category));

            assertThatThrownBy(() -> postCommandService.publishPost(postId, ACTOR_ID))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCannotBePublishedWhenArchived().getError());
        }

        @Test
        @DisplayName("Throws CommonException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID originalAuthorId = UUID.randomUUID();
            persistUser(originalAuthorId, true, Role.USER);
            givenActiveUser();
            CategoryEntity category = persistCategory();
            UUID postId = persistPost(originalAuthorId, VALID_TITLE, "my-first-post-about-java", PostStatus.DRAFT, Set.of(category));

            assertThatThrownBy(() -> postCommandService.publishPost(postId, ACTOR_ID))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied("You are not authorized to publish a post you did not author").getError());
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            givenActiveUser();
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.publishPost(nonExistentPostId, ACTOR_ID))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            assertThatThrownBy(() -> postCommandService.publishPost(UUID.randomUUID(), ACTOR_ID))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            givenInactiveUser();

            assertThatThrownBy(() -> postCommandService.publishPost(UUID.randomUUID(), ACTOR_ID))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }
    }
}