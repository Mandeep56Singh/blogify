package com.mandeep.blogify.blog.domain.model.valueObject;

import java.util.UUID;

public record ImageId (UUID value) {
    public ImageId {
        if (value == null) {
            throw new IllegalArgumentException("Image Id cannot be null");
        }
    }
}
