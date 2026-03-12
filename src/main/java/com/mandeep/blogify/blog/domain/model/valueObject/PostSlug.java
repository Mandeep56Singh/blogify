package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;

public record PostSlug(String value) {

    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 150;

    // Only lowercase letters, digits, single hyphens between words — no leading/trailing hyphens
    private static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    public PostSlug {
        if (value == null || value.isBlank()) {
            throw PostException.postSlugNullOrBlank();
        }

        value = value.strip();

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw PostException.postSlugInvalidLength();
        }

        if (!value.matches(SLUG_PATTERN)) {
            throw PostException.postSlugInvalidFormat();
        }
    }
}