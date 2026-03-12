package com.mandeep.blogify.blog.domain.exceptions;

import com.mandeep.blogify.blog.domain.exceptions.enums.PostError;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.shared.domain.exception.DomainException;

import java.util.Set;

public class PostException extends DomainException {

    private PostException(PostError error, String message) {
        super(error, message);
    }

    //region Title
    public static PostException postTitleNullOrBlank() {
        return new PostException(
                PostError.POST_TITLE_NULL_OR_BLANK,
                PostError.POST_TITLE_NULL_OR_BLANK.detail()
        );
    }

    public static PostException postTitleInvalidLength() {
        return new PostException(
                PostError.POST_TITLE_INVALID_LENGTH,
                PostError.POST_TITLE_INVALID_LENGTH.detail()
        );
    }

    public static PostException postNotFound(PostId postId) {
        return new PostException(
                PostError.POST_NOT_FOUND,
                "Post with id "
                + postId.value()
                + " not found"
        );
    }
    //endregion

    //region Content
    public static PostException postContentNullOrEmpty() {
        return new PostException(
                PostError.POST_CONTENT_NULL_OR_EMPTY,
                PostError.POST_CONTENT_NULL_OR_EMPTY.detail()
        );
    }

    public static PostException postContentInvalidLength() {
        return new PostException(
                PostError.POST_CONTENT_INVALID_LENGTH,
                PostError.POST_CONTENT_INVALID_LENGTH.detail()
        );
    }
    //endregion

    //region Post Category
    public static PostException postCategoriesNullOrEmpty() {
        return new PostException(
                PostError.POST_CATEGORIES_NULL_OR_EMPTY,
                PostError.POST_CATEGORIES_NULL_OR_EMPTY.detail()
        );
    }

    public static PostException postCategoryNotFound(CategoryId id) {
        return new PostException(
                PostError.CATEGORY_NOT_FOUND,
                "Category with id "+ id.value() +" not found, Please provide valid category"
        );
    }

    public static PostException postCategoriesNotFound(Set<CategoryId> missingCategoryIds) {

        return new PostException(
                PostError.CATEGORIES_NOT_FOUND,
                "Categories with these ids "
                        + missingCategoryIds.stream().map(CategoryId::value).toList()
                        + " not found, Please provide valid categories."
        );
    }
    //endregion

    //region Author
    public static PostException authorAccountNotActive() {
        return new PostException(
                PostError.AUTHOR_ACCOUNT_NOT_ACTIVE,
                PostError.AUTHOR_ACCOUNT_NOT_ACTIVE.detail()
        );
    }

    public static PostException authorNotFound() {
        return new PostException(
                PostError.AUTHOR_NOT_FOUND,
                PostError.AUTHOR_NOT_FOUND.detail()
        );
    }
    //endregion

    //region Post Publish
    public static PostException postCannotBePublishedWhenArchived() {
        return new PostException(
                PostError.POST_CANNOT_BE_PUBLISHED_WHEN_ARCHIVED,
                PostError.POST_CANNOT_BE_PUBLISHED_WHEN_ARCHIVED.detail()
        );
    }
    //endregion

    //region Slug
    public static PostException postSlugNullOrBlank() {
        return new PostException(
                PostError.POST_SLUG_NULL_OR_BLANK,
                PostError.POST_SLUG_NULL_OR_BLANK.detail()
        );
    }

    public static PostException postSlugInvalidFormat() {
        return new PostException(
                PostError.POST_SLUG_INVALID_FORMAT,
                PostError.POST_SLUG_INVALID_FORMAT.detail()
        );
    }

    public static PostException postSlugInvalidLength() {
        return new PostException(
                PostError.POST_SLUG_INVALID_LENGTH,
                PostError.POST_SLUG_INVALID_LENGTH.detail()
        );
    }

//endregion

    //region Post Update
    public static PostException postCannotBeUpdatedWhenArchived() {
        return new PostException(
                PostError.POST_CANNOT_BE_UPDATED_WHEN_ARCHIVED,
                PostError.POST_CANNOT_BE_UPDATED_WHEN_ARCHIVED.detail()
        );
    }
    //endregion

    //region Authorization
    public static PostException unauthorized(String message) {
        return new PostException(
                PostError.UNAUTHORIZED,
                message
        );
    }
    //endregion
}
