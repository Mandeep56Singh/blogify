package com.mandeep.blogify.blog.domain.model.entity;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.*;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;

@Getter
public class Post {

    private final PostId postId;
    private PostTitle postTitle;
    private final PostSlug postSlug;
    private PostContent postContent;
    private final UserId authorId;
    private PostCategories postCategories;
    private final Instant createdAt;
    private Instant publishedAt;
    private PostStatus postStatus;

    private Post(
            PostId postId,
            PostTitle postTitle,
            PostSlug postSlug,
            PostContent postContent,
            UserId authorId,
            PostCategories postCategories,
            Instant createdAt,
            Instant publishedAt,
            PostStatus postStatus
    ) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postSlug = postSlug;
        this.postContent = postContent;
        this.authorId = authorId;
        this.postCategories = postCategories;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.postStatus = postStatus;
    }


    public static Post create(
            PostId postId,
            PostTitle title,
            PostSlug slug,
            PostContent content,
            UserId userId,
            PostCategories categories
    ) {
        return new Post(
                postId,
                title,
                slug,
                content,
                userId,
                categories,
                Instant.now(Clock.systemUTC()),
                null,
                PostStatus.DRAFT
        );
    }

    public static Post reconstitute(
            PostId postId,
            PostTitle title,
            PostSlug slug,
            PostContent content,
            UserId userId,
            PostCategories categories,
            Instant createdAt,
            Instant publishedAt,
            PostStatus status
    ) {
        return new Post(
                postId,
                title,
                slug,
                content,
                userId,
                categories,
                createdAt,
                publishedAt,
                status
        );
    }

    public void publish() {
        if (postStatus.isPublished()) {
            return;
        }
        if (postStatus.isArchived()) {
            throw PostException.postCannotBePublishedWhenArchived();
        }
        this.postStatus = PostStatus.PUBLISHED;
        this.publishedAt = Instant.now(Clock.systemUTC());
    }

    public void archive() {
        this.postStatus = PostStatus.ARCHIVED;
    }

    public void updateContent(
            PostTitle newTitle,
            PostContent newContent,
            PostCategories newCategories
    ) {

        if (postStatus.isArchived()) {
            throw PostException.postCannotBeUpdatedWhenArchived();
        }

        this.postTitle = newTitle;
        this.postContent = newContent;
        this.postCategories = newCategories;

    }

    public void delete() {
        if (postStatus.isArchived()) {
            return;
        }

        this.postStatus = PostStatus.ARCHIVED;
    }

}
