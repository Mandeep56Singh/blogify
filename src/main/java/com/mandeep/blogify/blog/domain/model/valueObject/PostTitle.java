package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;

public record PostTitle(String value) {

    public PostTitle {

        if (value == null || value.isBlank()) {
            throw PostException.postTitleNullOrBlank();
        }
        value = value.strip();

        if (value.length() < 5 || value.length() > 150) {
            throw PostException.postTitleInvalidLength();
        }

    }
}
