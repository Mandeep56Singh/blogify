package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.CategoryException;

public record CategoryTitle(String value) {

    private static final int MAX_LENGTH = 120;

    public CategoryTitle {
        if (value == null || value.isBlank()) {
            throw CategoryException.categoryTitleNullOrBlank();
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty() || trimmed.length() > MAX_LENGTH) {
            throw CategoryException.categoryTitleInvalidLength();
        }

        value = trimmed;
    }
}