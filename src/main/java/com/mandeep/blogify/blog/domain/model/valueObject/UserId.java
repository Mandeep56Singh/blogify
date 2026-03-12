package com.mandeep.blogify.blog.domain.model.valueObject;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("Author Id cannot be null");
        }
    }
}
