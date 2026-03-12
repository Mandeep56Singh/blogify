package com.mandeep.blogify.blog.domain.exceptions;

import com.mandeep.blogify.blog.domain.exceptions.enums.CategoryError;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.shared.domain.exception.DomainException;

public class CategoryException extends DomainException {

    private CategoryException(CategoryError error, String message) {
        super(error, message);
    }

    public static CategoryException categoryTitleNullOrBlank() {
        return new CategoryException(
                CategoryError.CATEGORY_TITLE_NULL_OR_BLANK,
                CategoryError.CATEGORY_TITLE_NULL_OR_BLANK.detail()
        );
    }

    public static CategoryException categoryTitleInvalidLength() {
        return new CategoryException(
                CategoryError.CATEGORY_TITLE_INVALID_LENGTH,
                CategoryError.CATEGORY_TITLE_INVALID_LENGTH.detail()
        );
    }

    public static CategoryException categoryNotFound(CategoryId categoryId) {
        return new CategoryException(
                CategoryError.CATEGORY_NOT_FOUND,
                "Category with id=" + categoryId.value() + "not found"
        );
    }

    public static CategoryException categoryDescriptionTooLong() {
        return new CategoryException(
                CategoryError.CATEGORY_DESCRIPTION_TOO_LONG,
                CategoryError.CATEGORY_DESCRIPTION_TOO_LONG.detail()
        );
    }

    public static CategoryException categoryAlreadyExists(CategoryTitle title) {
        return new CategoryException(
                CategoryError.CATEGORY_ALREADY_EXISTS,
                "Cannot Create Category, Category with title "
                + title.value()
                + " already exists."
        );
    }
}