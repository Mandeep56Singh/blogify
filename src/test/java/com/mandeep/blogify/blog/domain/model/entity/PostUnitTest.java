package com.mandeep.blogify.blog.domain.model.entity;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Post Entity")
class PostUnitTest {

    //region Test Data
    private static final PostId A_POST_ID = new PostId(UUID.randomUUID());
    private static final PostId B_POST_ID = new PostId(UUID.randomUUID());
    private static final PostTitle A_TITLE = new PostTitle("Clean Architecture in Java");
    private static final PostSlug A_SLUG = new PostSlug("clean-architecture-java");
    private static final PostContent A_CONTENT = new PostContent("a".repeat(150)); // Valid > 100
    private static final UserId AN_AUTHOR_ID = new UserId(UUID.randomUUID());
    private static final PostCategories SOME_CATEGORIES = new PostCategories(Set.of(new CategoryId(UUID.randomUUID())));
    private static final Instant A_FIXED_INSTANT = Instant.parse("2026-03-23T10:00:00Z");
    //endregion

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Creates a new Post as DRAFT with current timestamp")
        void should_CreatePost_When_ValidDataProvided() {
            // Act
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);

            // Assert
            assertAll(
                    () -> assertThat(post.getPostId()).isEqualTo(A_POST_ID),
                    () -> assertThat(post.getPostStatus()).isEqualTo(PostStatus.DRAFT),
                    () -> assertThat(post.getCreatedAt()).isBeforeOrEqualTo(Instant.now()),
                    () -> assertThat(post.getPublishedAt()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("publish()")
    class Publish {

        @Test
        @DisplayName("Transitions DRAFT to PUBLISHED and sets published timestamp")
        void should_PublishPost_When_CurrentlyDraft() {
            // Arrange
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);

            // Act
            post.publish();

            // Assert
            assertAll(
                    () -> assertThat(post.getPostStatus()).isEqualTo(PostStatus.PUBLISHED),
                    () -> assertThat(post.getPublishedAt()).isNotNull()
            );
        }

        @Test
        @DisplayName("Throws exception when attempting to publish an ARCHIVED post")
        void should_ThrowException_When_PublishingArchivedPost() {
            // Arrange
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);
            post.archive();

            // Act & Assert
            assertThatThrownBy(post::publish)
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCannotBePublishedWhenArchived().getError());
        }

        @Test
        @DisplayName("Does nothing if post is already PUBLISHED")
        void should_DoNothing_When_AlreadyPublished() {
            // Arrange
            Post post = Post.reconstitute(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES, A_FIXED_INSTANT, A_FIXED_INSTANT, PostStatus.PUBLISHED);

            // Act
            post.publish();

            // Assert (PublishedAt shouldn't change to a newer time)
            assertThat(post.getPublishedAt()).isEqualTo(A_FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("updateContent()")
    class UpdateContent {

        @Test
        @DisplayName("Updates title and content successfully")
        void should_UpdateContent_When_NotArchived() {
            // Arrange
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);
            PostTitle newTitle = new PostTitle("Updated Title");
            PostContent newContent = new PostContent("b".repeat(200));

            // Act
            post.updateContent(newTitle, newContent, SOME_CATEGORIES);

            // Assert
            assertAll(
                    () -> assertThat(post.getPostTitle()).isEqualTo(newTitle),
                    () -> assertThat(post.getPostContent()).isEqualTo(newContent)
            );
        }

        @Test
        @DisplayName("Throws exception when updating an ARCHIVED post")
        void should_ThrowException_When_UpdatingArchivedPost() {
            // Arrange
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);
            post.archive();

            // Act & Assert
            assertThatThrownBy(() -> post.updateContent(A_TITLE, A_CONTENT, SOME_CATEGORIES))
                    .isInstanceOf(PostException.class)
                    .extracting(ex -> ((PostException) ex).getError())
                    .isEqualTo(PostException.postCannotBeUpdatedWhenArchived().getError());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Transitions post to ARCHIVED status")
        void should_ArchivePost_When_Deleted() {
            // Arrange
            Post post = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);

            // Act
            post.delete();

            // Assert
            assertThat(post.getPostStatus()).isEqualTo(PostStatus.ARCHIVED);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class Equality {

        @Test
        @DisplayName("Equality is based solely on PostId")
        void should_BeEqual_When_PostIdIsIdentical() {
            // Arrange
            Post first = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);
            Post second = Post.reconstitute(A_POST_ID, new PostTitle("Different"), A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES, A_FIXED_INSTANT, null, PostStatus.DRAFT);

            // Act & Assert
            assertAll(
                    () -> assertThat(first).isEqualTo(second),
                    () -> assertThat(first.hashCode()).isEqualTo(second.hashCode())
            );
        }

        @Test
        @DisplayName("Inequality when PostIds differ")
        void should_NotBeEqual_When_PostIdsDiffer() {
            // Arrange
            Post first = Post.create(A_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);
            Post second = Post.create(B_POST_ID, A_TITLE, A_SLUG, A_CONTENT, AN_AUTHOR_ID, SOME_CATEGORIES);

            // Act & Assert
            assertThat(first).isNotEqualTo(second);
        }
    }
}