package com.mandeep.blogify.blog.domain.exceptions.post;

import com.mandeep.blogify.shared.exceptions.AppError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostError implements AppError {

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

    PostError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }
}
