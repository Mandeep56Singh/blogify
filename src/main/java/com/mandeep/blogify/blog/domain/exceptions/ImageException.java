package com.mandeep.blogify.blog.domain.exceptions;

import com.mandeep.blogify.blog.domain.exceptions.enums.ImageError;
import com.mandeep.blogify.shared.domain.exception.DomainException;

public class ImageException extends DomainException {

    private ImageException(ImageError error, String message) {
        super(error, message);
    }

    //region Name
    public static ImageException nameNullOrBlank() {
        return new ImageException(
                ImageError.IMAGE_NAME_NULL_OR_BLANK,
                ImageError.IMAGE_NAME_NULL_OR_BLANK.detail()
        );
    }
    //endregion

    //region File
    public static ImageException empty() {
        return new ImageException(
                ImageError.IMAGE_EMPTY,
                ImageError.IMAGE_EMPTY.detail()
        );
    }

    public static ImageException invalidType() {
        return new ImageException(
                ImageError.IMAGE_INVALID_CONTENT_TYPE,
                ImageError.IMAGE_INVALID_CONTENT_TYPE.detail()
        );
    }

    public static ImageException tooLarge() {
        return new ImageException(
                ImageError.IMAGE_TOO_LARGE,
                ImageError.IMAGE_TOO_LARGE.detail()
        );
    }
    //endregion

    //region Not Found
    public static ImageException notFound() {
        return new ImageException(
                ImageError.IMAGE_NOT_FOUND,
                ImageError.IMAGE_NOT_FOUND.detail()
        );
    }
    //endregion
}