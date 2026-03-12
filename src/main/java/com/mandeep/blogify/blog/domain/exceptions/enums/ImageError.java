package com.mandeep.blogify.blog.domain.exceptions.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public enum ImageError implements DomainError {

    IMAGE_NAME_NULL_OR_BLANK(
            "Image name is required",
            "Image original name cannot be null or blank",
            DomainErrorType.INVALID_INPUT,
            "IMAGE_NAME_NULL_OR_BLANK"
    ),

    IMAGE_EMPTY(
            "Image file is empty",
            "Image size must be greater than zero",
            DomainErrorType.INVALID_INPUT,
            "IMAGE_EMPTY"
    ),

    IMAGE_INVALID_CONTENT_TYPE(
            "Invalid image type",
            "Only valid image MIME types are allowed",
            DomainErrorType.INVALID_INPUT,
            "IMAGE_INVALID_CONTENT_TYPE"
    ),

    IMAGE_TOO_LARGE(
            "Image size exceeds allowed limit",
            "Image exceeds the maximum allowed size",
            DomainErrorType.INVALID_INPUT,
            "IMAGE_TOO_LARGE"
    ),

    IMAGE_NOT_FOUND(
            "Image not found",
            "The requested image does not exist",
            DomainErrorType.NOT_FOUND,
            "IMAGE_NOT_FOUND"
    );

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;

    ImageError(String title, String detail, DomainErrorType errorType, String errorCode) {
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