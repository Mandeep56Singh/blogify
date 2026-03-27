package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.application.query.PostQueryService;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.user.UserFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private UserFacade userFacade;

    //region Test Data
    private static final String VALID_TITLE = "My First Post About Java";
    private static final String VALID_CONTENT = "A".repeat(100);
    //endregion

    //region Helper Methods

    private UUID persistUser() {
        return userFacade.createUser("user@blogify.com", "user123", "Strong@123");
    }

    private UUID persistAdmin() {
        return userFacade.createAdmin("admin@blogify.com", "admin", "Strong@123");
    }

    private UUID persistCategory(UUID adminId) {
        return categoryCommandService.createCategory(new CategoryRequest(
                "title",
                "description",
                adminId
        ));
    }

    private UUID persistPost(UUID authorId, List<UUID> categories) {

        return postCommandService.createPost(new PostRequest(
                VALID_TITLE,
                VALID_CONTENT,
                categories,
                authorId
        ));
    }

    private PostRequest buildRequest(String title, List<UUID> categoryIds, UUID authorId) {
        return new PostRequest(title, VALID_CONTENT, categoryIds, authorId);
    }

    private String getPostStatus(UUID postId) {
        return postQueryService.getPostById(postId).status();
    }

    private String getPostTitle(UUID postId) {
        return postQueryService.getPostById(postId).title();
    }

    private String getPostSlug(UUID postId) {
        return postQueryService.getPostById(postId).slug();
    }

    private UUID getPostAuthorId(UUID postId) {
        return postQueryService.getPostById(postId).authorData().id();
    }

    private boolean postExists(UUID postId) {
        try {
            postQueryService.getPostById(postId);
            return true;
        } catch (Exception ex) {
            return false;
        }
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
            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);

            UUID postId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), adminId)
            );

            assertAll(
                    () -> assertThat(postExists(postId)).isTrue(),
                    () -> assertThat(getPostTitle(postId)).isEqualTo(VALID_TITLE),
                    () -> assertThat(getPostStatus(postId)).isEqualTo(PostStatus.DRAFT.name()),
                    () -> assertThat(getPostAuthorId(postId)).isEqualTo(adminId)
            );
        }

        @Test
        @DisplayName("Admin can also create a post")
        void should_CreatePost_When_AuthorIsAdmin() {
            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);

            UUID postId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), adminId)
            );

            assertThat(postExists(postId)).isTrue();
        }

        @Test
        @DisplayName("Generates unique slug when a post with the same title already exists")
        void should_GenerateUniqueSlug_When_PostWithSameTitleExists() {

            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);

            UUID postId = persistPost(adminId, List.of(categoryId));

            UUID newPostId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), adminId)
            );

            String oldSlug = getPostSlug(postId);
            String newSlug = getPostSlug(newPostId);

            assertAll(
                    () -> assertThat(postExists(newPostId)).isTrue(),
                    () -> assertThat(newSlug).isNotEqualTo(oldSlug),
                    () -> assertThat(newSlug).contains(oldSlug)
            );
        }

        @Test
        @DisplayName("Generates unique slug even when archived post with same title exists")
        void should_GenerateUniqueSlug_When_ArchivedPostWithSameTitleExists() {

            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);

            UUID postId = persistPost(adminId, List.of(categoryId));
            postCommandService.deletePost(postId, adminId);

            UUID newPostId = postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), adminId)
            );

            assertThat(postExists(newPostId)).isTrue();
        }

        @Test
        @DisplayName("Throws AccountException when author is not found")
        void should_ThrowException_When_AuthorNotFound() {
            UUID categoryId = persistCategory(persistAdmin());

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), UUID.randomUUID())
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when author is inactive")
        void should_ThrowException_When_AuthorIsInactive() {
            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);
            UUID userId = persistUser();

            userFacade.deactivateUser(userId, adminId);

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), userId)
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws PostException when some categories do not exist")
        void should_ThrowException_When_CategoryNotFound() {

            UUID categoryId = UUID.randomUUID();
            UUID userId = persistUser();

            assertThatThrownBy(() -> postCommandService.createPost(
                    buildRequest(VALID_TITLE, List.of(categoryId), userId)
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCategoriesNotFound(
                            Set.of(new CategoryId(categoryId))
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

            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(adminId, List.of(categoryId));

            String newTitle = "Updated Post Title Here";
            postCommandService.updatePost(postId, buildRequest(newTitle, List.of(categoryId), adminId));

            assertThat(getPostTitle(postId)).isEqualTo(newTitle);
        }

        @Test
        @DisplayName("Admin can update post regardless of who created it")
        void should_UpdatePost_When_RequesterIsActiveAdmin() {

            UUID originalAuthorId = persistUser();

            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);

            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            String newTitle = "Admin Updated This Title";
            postCommandService.updatePost(postId, buildRequest(newTitle, List.of(categoryId), adminId));

            assertThat(getPostTitle(postId)).isEqualTo(newTitle);
        }

        @Test
        @DisplayName("Updating to a title that already exists succeeds since slug is not regenerated on update")
        void should_UpdatePost_When_TitleAlreadyExistsInAnotherPost() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);

            UUID postId = persistPost(authorId, List.of(categoryId));

            String newTitle = "Another Post Title";
            postCommandService.createPost(buildRequest(newTitle, List.of(categoryId), authorId));

            postCommandService.updatePost(postId, buildRequest(newTitle, List.of(categoryId), authorId));

            assertThat(getPostTitle(postId)).isEqualTo(newTitle);
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.updatePost(nonExistentPostId,
                    buildRequest(VALID_TITLE, List.of(categoryId), authorId)
            ))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws PostException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID adminId = persistAdmin();
            UUID originalAuthorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            UUID otherUserId = userFacade.createUser("other@blogify.com", "otheruser", "Strong@123");

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest("New Title For Update", List.of(categoryId), otherUserId)
            ))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            UUID adminId = persistAdmin();
            UUID categoryId = persistCategory(adminId);
            UUID nonExistentUserId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.updatePost(UUID.randomUUID(),
                    buildRequest(VALID_TITLE, List.of(categoryId), nonExistentUserId)
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            UUID adminId = persistAdmin();
            UUID inactiveUserId = persistUser();
            UUID categoryId = persistCategory(adminId);
            userFacade.deactivateUser(inactiveUserId, adminId);

            assertThatThrownBy(() -> postCommandService.updatePost(UUID.randomUUID(),
                    buildRequest(VALID_TITLE, List.of(categoryId), inactiveUserId)
            ))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throws PostException when some categories do not exist")
        void should_ThrowException_When_CategoryNotFound() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));
            UUID nonExistentCategoryId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest(VALID_TITLE, List.of(nonExistentCategoryId), authorId)
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
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.deletePost(postId, authorId); // Archives the post

            assertThatThrownBy(() -> postCommandService.updatePost(postId,
                    buildRequest("Some New Title Here", List.of(categoryId), authorId)
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
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.deletePost(postId, authorId);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Active admin can delete post they did not author")
        void should_DeletePost_When_RequesterIsActiveAdmin() {
            UUID adminId = persistAdmin();
            UUID originalAuthorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            postCommandService.deletePost(postId, adminId);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Deleting an already archived post is idempotent")
        void should_Succeed_When_PostIsAlreadyArchived() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.deletePost(postId, authorId); // First delete
            postCommandService.deletePost(postId, authorId); // Idempotent delete

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.ARCHIVED.name());
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            UUID authorId = persistUser();
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.deletePost(nonExistentPostId, authorId))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws PostException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID adminId = persistAdmin();
            UUID originalAuthorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            UUID otherUserId = userFacade.createUser("other@blogify.com", "otheruser", "Strong@123");

            assertThatThrownBy(() -> postCommandService.deletePost(postId, otherUserId))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            UUID nonExistentUserId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.deletePost(UUID.randomUUID(), nonExistentUserId))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            UUID adminId = persistAdmin();
            UUID inactiveUserId = persistUser();
            userFacade.deactivateUser(inactiveUserId, adminId);

            assertThatThrownBy(() -> postCommandService.deletePost(UUID.randomUUID(), inactiveUserId))
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
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.publishPost(postId, authorId);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Active admin can publish post they did not author")
        void should_PublishPost_When_RequesterIsActiveAdmin() {
            UUID adminId = persistAdmin();
            UUID originalAuthorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            postCommandService.publishPost(postId, adminId);

            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Publishing an already published post is idempotent and succeeds")
        void should_Succeed_When_PostIsAlreadyPublished() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.publishPost(postId, authorId); // Publish first time
            postCommandService.publishPost(postId, authorId); // Publish second time

            // Status should remain published without throwing any exceptions
            assertThat(getPostStatus(postId)).isEqualTo(PostStatus.PUBLISHED.name());
        }

        @Test
        @DisplayName("Throws PostException when attempting to publish an archived post")
        void should_ThrowException_When_PostIsArchived() {
            UUID adminId = persistAdmin();
            UUID authorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(authorId, List.of(categoryId));

            postCommandService.deletePost(postId, authorId); // Archive it

            assertThatThrownBy(() -> postCommandService.publishPost(postId, authorId))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCannotBePublishedWhenArchived().getError());
        }

        @Test
        @DisplayName("Throws CommonException when requester is not the author and not admin")
        void should_ThrowException_When_RequesterIsNotAuthorAndNotAdmin() {
            UUID adminId = persistAdmin();
            UUID originalAuthorId = persistUser();
            UUID categoryId = persistCategory(adminId);
            UUID postId = persistPost(originalAuthorId, List.of(categoryId));

            UUID otherUserId = userFacade.createUser("other@blogify.com", "otheruser", "Strong@123");

            assertThatThrownBy(() -> postCommandService.publishPost(postId, otherUserId))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied("You are not authorized to publish a post you did not author").getError());
        }

        @Test
        @DisplayName("Throws PostException when post is not found")
        void should_ThrowException_When_PostNotFound() {
            UUID authorId = persistUser();
            UUID nonExistentPostId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.publishPost(nonExistentPostId, authorId))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postNotFound(new PostId(nonExistentPostId)).getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is not found")
        void should_ThrowException_When_RequesterNotFound() {
            UUID nonExistentUserId = UUID.randomUUID();

            assertThatThrownBy(() -> postCommandService.publishPost(UUID.randomUUID(), nonExistentUserId))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }

        @Test
        @DisplayName("Throws AccountException when requester is inactive")
        void should_ThrowException_When_RequesterIsInactive() {
            UUID adminId = persistAdmin();
            UUID inactiveUserId = persistUser();
            userFacade.deactivateUser(inactiveUserId, adminId);

            assertThatThrownBy(() -> postCommandService.publishPost(UUID.randomUUID(), inactiveUserId))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }
    }
}