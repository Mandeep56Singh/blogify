package com.mandeep.blogify.blog.domain.model.valueObject;

import java.util.UUID;

public record PostId(UUID value) {
    public PostId {
        if (value == null) {
            throw new IllegalArgumentException("Post Id cannot be null");
        }
    }
}
