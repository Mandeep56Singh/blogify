package com.mandeep.blogify.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record AppProblem(
        String type,
        HttpStatus status,
        String title,
        String detail,
        String errorCode

) implements AppError {

    public static AppProblem getDetail(AppError error) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        return new AppProblem(
                baseUrl + error.type(),
                error.status(),
                error.title(),
                error.detail(),
                error.errorCode()
        );
    }

}
