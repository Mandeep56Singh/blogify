package com.mandeep.blogify.shared.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final AppError apiError;

    public ApiException(AppError apiError) {
        this.apiError = apiError;
    }
}
