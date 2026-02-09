package com.mandeep.blogify.blog.domain.exceptions.imageUpload;

import com.mandeep.blogify.shared.exceptions.AppError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ImageUploadError implements AppError {

    INVALID_IMAGE_DIRECTORY(
            "/invalid-image-directory",
            HttpStatus.BAD_REQUEST,
            "Image directory invalid",
            "The directory where you want to upload image is invalid",
            "INVALID_IMAGE_DIRECTORY"
    ),
    IMAGE_EMPTY(
            "/image-empty",
            HttpStatus.BAD_REQUEST,
            "Empty image file",
            "The uploaded image file is empty. Please select a file to upload.",
            "IMAGE_EMPTY"
    ),
    IMAGE_NOT_FOUND(
            "/image-not-found",
            HttpStatus.NOT_FOUND,
            "Image not found",
            "We are unable to find the requested image, please try a different image ID",
            "IMAGE_NOT_FOUND"
    ),
    IMAGE_UPLOAD_FAILED(
            "/image-upload-failed",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Image upload failed",
            "There was an error uploading your image. Please try again.",
            "IMAGE_UPLOAD_FAILED"
    ),
    IMAGE_INVALID_TYPE(
            "/image-invalid-type",
            HttpStatus.BAD_REQUEST,
            "Invalid image type",
            "Only image files are allowed (jpg, png, gif, etc.)",
            "IMAGE_INVALID_TYPE"
    ),
    IMAGE_TOO_LARGE(
            "/image-too-large",
            HttpStatus.BAD_REQUEST,
            "Image too large",
            "The uploaded image exceeds the maximum allowed size",
            "IMAGE_TOO_LARGE"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    ImageUploadError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }

}
