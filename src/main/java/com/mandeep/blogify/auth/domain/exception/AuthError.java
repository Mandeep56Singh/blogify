package com.mandeep.blogify.auth.domain.exception;


import com.mandeep.blogify.shared.exceptions.AppError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthError implements AppError {

    INVALID_CREDENTIALS(
            "/invalid-credentials",
            HttpStatus.NOT_FOUND,
            "user not found",
            "Invalid email or password, please Sign up if you haven't",
            "USER_NOT_FOUND"
    );

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    AuthError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }
}
