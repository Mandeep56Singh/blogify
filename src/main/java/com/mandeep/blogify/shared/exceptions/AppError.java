package com.mandeep.blogify.shared.exceptions;

import org.springframework.http.HttpStatus;

public interface AppError {
    String type();
    HttpStatus status();
    String title();
    String detail();
    String errorCode();
}
