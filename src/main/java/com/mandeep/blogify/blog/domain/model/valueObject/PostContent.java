package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;

public record PostContent(
        String value
) {
    private static final int MIN_LENGTH = 100;
    private static final int MAX_LENGTH = 20_000;

    public PostContent {

        if (value == null || value.isBlank()) {
            throw PostException.postContentNullOrEmpty();
        }

        value = value.strip();

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw PostException.postContentInvalidLength();
        }

    }
}
