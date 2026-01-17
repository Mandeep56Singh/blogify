package com.mandeep.blogify.exceptions;

import com.mandeep.blogify.constants.ApiError;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ApiError apiError;

    public ApiException(ApiError apiError) {
        this.apiError = apiError;
    }
}
