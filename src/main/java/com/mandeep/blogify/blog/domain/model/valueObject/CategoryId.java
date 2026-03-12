package com.mandeep.blogify.blog.domain.model.valueObject;

import java.util.UUID;

public record CategoryId(UUID value) {
    public CategoryId {
        if (value == null) {
            throw new IllegalArgumentException("Category Id cannot be null");
        }
    }
}
