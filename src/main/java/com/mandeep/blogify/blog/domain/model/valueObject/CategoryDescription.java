package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.CategoryException;

public record CategoryDescription(String value) {

    private static final int MAX_LENGTH = 1_000;

    public CategoryDescription {

        if (value != null && value.strip().length() > MAX_LENGTH) {
            throw CategoryException.categoryDescriptionTooLong();
        }

        value = value != null ? value.strip() : "";
    }
}