package com.mandeep.blogify.shared.web.exceptions;

import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.domain.exception.DomainError;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record AppProblemDetail(
        String type,
        HttpStatus status,
        String title,
        String detail,
        String errorCode

) implements ProblemDetailFormat {

    public static AppProblemDetail from(DomainError error) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String typeUri = baseUrl + "/errors/" + error.errorCode().toLowerCase().replace("_", "-");

        return new AppProblemDetail(
                typeUri,
                AppUtils.resolveStatus(error.errorType()),
                error.title(),
                error.detail(),
                error.errorCode()
        );
    }

    public static AppProblemDetail from(DomainError error, String message) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String typeUri = baseUrl + "/errors/" + error.errorCode().toLowerCase().replace("_", "-");

        return new AppProblemDetail(
                typeUri,
                AppUtils.resolveStatus(error.errorType()),
                error.title(),
                message,
                error.errorCode()
        );
    }


}
