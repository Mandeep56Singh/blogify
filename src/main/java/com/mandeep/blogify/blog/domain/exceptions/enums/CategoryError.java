package com.mandeep.blogify.blog.domain.exceptions.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum CategoryError implements DomainError {

    CATEGORY_TITLE_NULL_OR_BLANK(
            "Category title is required",
            "Please provide a title for the category",
            DomainErrorType.INVALID_INPUT,
            "CATEGORY_TITLE_NULL_OR_BLANK"
    ),
    CATEGORY_TITLE_INVALID_LENGTH(
            "Category title length is invalid",
            "Category title must be between 3 and 120 characters",
            DomainErrorType.INVALID_INPUT,
            "CATEGORY_TITLE_INVALID_LENGTH"
    ),
    CATEGORY_DESCRIPTION_TOO_LONG(
            "Category description is too long",
            "Category description cannot exceed 1000 characters",
            DomainErrorType.INVALID_INPUT,
            "CATEGORY_DESCRIPTION_TOO_LONG"
    ),
    CATEGORY_ALREADY_EXISTS(
            "Category already exists",
            "A category with this title already exists",
            DomainErrorType.CONFLICT,
            "CATEGORY_ALREADY_EXISTS"
    ),
    CATEGORY_NOT_FOUND(
            "Category not found",
            "The requested category does not exist",
            DomainErrorType.NOT_FOUND,
            "CATEGORY_NOT_FOUND"
    );

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;

    CategoryError(String title, String detail, DomainErrorType errorType, String errorCode) {
        this.title = title;
        this.detail = detail;
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    @Override
    public DomainErrorType errorType() { return errorType; }

    @Override
    public String title() { return title; }

    @Override
    public String detail() { return detail; }

    @Override
    public String errorCode() { return errorCode; }
}