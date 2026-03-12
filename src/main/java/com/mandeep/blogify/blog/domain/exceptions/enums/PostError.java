package com.mandeep.blogify.blog.domain.exceptions.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum PostError implements DomainError {

    //region Title
    POST_TITLE_NULL_OR_BLANK(
            "Post title is required",
            "Please, Provide title for post",
            DomainErrorType.INVALID_INPUT,
            "POST_TITLE_NULL_OR_BLANK"
    ),
    POST_TITLE_INVALID_LENGTH(
            "Post title length is invalid",
            "Post title length should be in range of 5 - 100 characters",
            DomainErrorType.INVALID_INPUT,
            "POST_TITLE_INVALID_LENGTH"
    ),
    POST_NOT_FOUND(
            "Post is not found",
            "Post is not found, Please refresh or check again",
            DomainErrorType.NOT_FOUND,
            "POST_NOT_FOUND"
    ),
    //endregion

    //region Post Content
    POST_CONTENT_NULL_OR_EMPTY(
            "Post content is required",
            "Please, Provide content for post",
            DomainErrorType.INVALID_INPUT,
            "POST_CONTENT_NULL_OR_EMPTY"
    ),
    POST_CONTENT_INVALID_LENGTH(
            "Post content length is invalid",
            "Post content must be between 100 and 20,000 characters",
            DomainErrorType.INVALID_INPUT,
            "POST_CONTENT_INVALID_LENGTH"
    ),
    //endregion

    //region Author
    AUTHOR_NOT_FOUND(
            "Author is not found",
            "Author for the post is not found, Please contact the team if still not resolved",
            DomainErrorType.NOT_FOUND,
            "AUTHOR_NOT_FOUND"
    ),
    AUTHOR_ACCOUNT_NOT_ACTIVE(
            "Account is not active",
            "Your Account is not active, Please contact the team",
            DomainErrorType.INVALID_INPUT,
            "AUTHOR_ACCOUNT_NOT_ACTIVE"
    ),
    //endregion

    //region Categories
    POST_CATEGORIES_NULL_OR_EMPTY(
            "Post categories cannot be empty",
            "Please provide at least one category for the post",
            DomainErrorType.INVALID_INPUT,
            "POST_CATEGORIES_NULL_OR_EMPTY"
    ),
    CATEGORY_NOT_FOUND(
            "Category not found",
            "Please provide valid category",
            DomainErrorType.NOT_FOUND,
            "CATEGORY_NOT_FOUND"
    ),
    CATEGORIES_NOT_FOUND(
            "Provided Categories not found",
            "Please provide valid Category",
            DomainErrorType.NOT_FOUND,
            "CATEGORIES_NOT_FOUND"
    ),
    //endregion

    //region Post Publish
    POST_CANNOT_BE_PUBLISHED_WHEN_ARCHIVED(
            "Cannot publish archived post",
            "This post is archived and cannot be published. Please draft it first.",
            DomainErrorType.INVALID_INPUT,
            "POST_CANNOT_BE_PUBLISHED_WHEN_ARCHIVED"
    ),
    //endregion

    //region Slug
    POST_SLUG_NULL_OR_BLANK(
            "Post slug is required",
            "Slug cannot be null or blank",
            DomainErrorType.INVALID_INPUT,
            "POST_SLUG_NULL_OR_BLANK"
    ),
    POST_SLUG_INVALID_FORMAT(
            "Post slug format is invalid",
            "Slug must contain only lowercase letters, numbers, and hyphens (e.g. my-first-post)",
            DomainErrorType.INVALID_INPUT,
            "POST_SLUG_INVALID_FORMAT"
    ),
    POST_SLUG_INVALID_LENGTH(
            "Post slug length is invalid",
            "Slug must be between 3 and 200 characters",
            DomainErrorType.INVALID_INPUT,
            "POST_SLUG_INVALID_LENGTH"
    ),
//endregion

    //region Post Update
    POST_CANNOT_BE_UPDATED_WHEN_ARCHIVED(
            "Cannot update archived post",
            "This post is archived and cannot be updated. Please draft it first.",
            DomainErrorType.INVALID_INPUT,
            "POST_CANNOT_BE_UPDATED_WHEN_ARCHIVED"
    ),
    //endregion

    //region Authorization
    UNAUTHORIZED(
            "Unauthorised to perform this action on post",
            "Please login first or sign up, to perform the this action",
            DomainErrorType.UNAUTHORIZED,
            "UNAUTHORIZED"
    ),
    //endregion
    ;

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;


    PostError(String title, String detail, DomainErrorType errorType, String errorCode) {
        this.title = title;
        this.detail = detail;
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    @Override
    public DomainErrorType errorType() {
        return errorType;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }
}
