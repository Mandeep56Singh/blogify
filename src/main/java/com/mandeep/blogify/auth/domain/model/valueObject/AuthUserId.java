package com.mandeep.blogify.auth.domain.model.valueObject;

import java.util.UUID;

public record AuthUserId(UUID value) {

    public AuthUserId {
        if(value == null) {
            throw new IllegalArgumentException("Auth User Id must not be null");
        }
    }
}
