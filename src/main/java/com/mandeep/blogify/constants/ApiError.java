package com.mandeep.blogify.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {

    //    user
    USER_NOT_FOUND(
            "/user-not-found",
            HttpStatus.NOT_FOUND,
            "user not found",
            "We are unable to find the user, please try different user",
            "USER_NOT_FOUND"
    ),
    EMAIL_NOT_FOUND(
            "/email-not-found",
            HttpStatus.NOT_FOUND,
            "email not found",
            "We are unable to find the user, please try different email",
            "EMAIL_NOT_FOUND"
    ),
    EMAIL_ALREADY_EXISTS(
            "/email-already-exits",
            HttpStatus.CONFLICT,
            "email already exists",
            "Email already exists, please try different email",
            "EMAIL_ALREADY_EXISTS"
    ),

    //    Category
    CATEGORY_ALREADY_EXITS(
            "/category-already-exists",
            HttpStatus.CONFLICT,
            "category already exists",
            "Category already exists, please try different category title",
            "CATEGORY_ALREADY_EXISTS"
    ),
    CATEGORY_NOT_FOUND(
            "/category-not-found",
            HttpStatus.NOT_FOUND,
            "category not found",
            "Category not found, please try different category",
            "CATEGORY_NOT_FOUND"
    ),

    // posts
    POST_NOT_FOUND(
            "/post-not-found",
            HttpStatus.NOT_FOUND,
            "post not found",
            "post not found, please try different post",
            "POST_NOT_FOUND"
    ),
    POST_ALREADY_EXITS(
            "/post-already-exists",
            HttpStatus.CONFLICT,
            "post with this title already exists",
            "every post should be unique title, please try different post title",
            "POST_ALREADY_EXISTS"
    ),

    // image
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

    // page
    INVALID_PAGE_NUMBER(
            "/invalid-page-number",
            HttpStatus.BAD_REQUEST,
            "Invalid page number",
            "You have requested for invalid page number, please make sure page number is positive, non-zero and within total pages",
            "INVALID_PAGE_NUMBER"
    ),
    INVALID_PAGE_SIZE(
            "/invalid-page-size",
            HttpStatus.BAD_REQUEST,
            "Invalid page size",
            "You have requested for invalid page size, please make sure page size is positive, non-zero and within total elements",
            "INVALID_PAGE_SIZE"
    ),

    // validation
    VALIDATION_FAILED(
            "/validation-failed",
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            "Please check the input and try again",
            "VALIDATION_ERROR"
    ),
    TYPE_MISMATCH(
            "/type-mismatch",
            HttpStatus.BAD_REQUEST,
            "Invalid parameter type",
            "One or more parameters have invalid types, please check the request",
            "TYPE_MISMATCH"
    ),
    INTERNAL_SERVER_ERROR(
            "/internal-server-error",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong",
            "Please try again after some time",
            "INTERNAL_SERVER_ERROR"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorcode;

    ApiError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorcode = errorcode;
    }
}
