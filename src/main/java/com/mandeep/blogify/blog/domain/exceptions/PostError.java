package com.mandeep.blogify.blog.domain.exceptions;

import com.mandeep.blogify.shared.exceptions.AppError;
import org.springframework.http.HttpStatus;

public enum PostError implements AppError {
    AUTHOR_NOT_FOUND(
            "/author-not-found",
            HttpStatus.NOT_FOUND,
            "user not found",
            "We are unable to find the author for this post, please refresh or try later",
            "USER_NOT_FOUND"
    ),
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
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    PostError(String type, HttpStatus status, String title, String detail, String errorCode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorCode;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }
}
