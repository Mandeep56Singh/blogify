package com.mandeep.blogify.blog.domain.model.valueObject;

public enum CategoryStatus {
    ACTIVE,
    ARCHIVED,
    ;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }

}
