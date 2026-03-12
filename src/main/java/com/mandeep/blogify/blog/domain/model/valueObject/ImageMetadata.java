package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.ImageException;

public record ImageMetadata(
        String originalName,
        String contentType,
        long sizeInBytes
) {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public ImageMetadata {
        if (originalName == null || originalName.isBlank()) {
            throw ImageException.nameNullOrBlank();
        }

        if (sizeInBytes <= 0) {
            throw ImageException.empty();
        }

        if (sizeInBytes > MAX_SIZE) {
            throw ImageException.tooLarge();
        }

        if (contentType == null || !contentType.startsWith("image/")) {
            throw ImageException.invalidType();
        }
    }
}