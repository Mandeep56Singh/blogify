package com.mandeep.blogify.shared.exceptions;

import org.springframework.http.HttpStatus;

public interface AppError {
    String getType();
    HttpStatus getStatus();
    String getTitle();
    String getDetail();
    String getErrorCode();
}
