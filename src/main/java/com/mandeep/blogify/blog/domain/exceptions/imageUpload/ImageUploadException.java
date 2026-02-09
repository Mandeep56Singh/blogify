package com.mandeep.blogify.blog.domain.exceptions.imageUpload;

import lombok.Getter;

@Getter
public class ImageUploadException extends RuntimeException {
    private final ImageUploadError imageUploadError;

    public ImageUploadException(ImageUploadError imageUploadError) {
        this.imageUploadError = imageUploadError;
    }

}
