package com.mandeep.blogify.shared.exceptions;

import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

public record AppValidationProblem(
        String type,
        HttpStatus status,
        String title,
        String detail,
        String errorCode,
        List<Map<String, Object>>  violations
) implements AppError{

    public static AppValidationProblem getProblemDetail(Set<? extends ConstraintViolation<?>> violations) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        CommonAppError error = CommonAppError.VALIDATION_FAILED;

        List<Map<String, Object>> voilationsList = violations.stream().map(
            v -> {
                String message = v.getPropertyPath().toString() + " " + v.getMessage();
                Map<String, Object> vMap = new HashMap<>();
                vMap.put("field", v.getPropertyPath().toString());
                vMap.put("invalidValue", v.getInvalidValue());
                vMap.put("message", message);
                vMap.put("constraint", v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());

                return Collections.unmodifiableMap(vMap);
            }).toList();

        return new AppValidationProblem(
                baseUrl + error.type(),
                error.status(),
                error.title(),
                error.detail(),
                error.errorCode(),
                voilationsList
        );
    }
}
