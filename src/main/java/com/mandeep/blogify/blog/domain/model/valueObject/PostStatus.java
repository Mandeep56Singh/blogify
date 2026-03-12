package com.mandeep.blogify.blog.domain.model.valueObject;

public enum PostStatus {

    DRAFT,
    PUBLISHED,
    ARCHIVED,
    ;

    public boolean isPublished() {
        return this == PUBLISHED;
    }
    public boolean isArchived() {
        return this == ARCHIVED;
    }
}
