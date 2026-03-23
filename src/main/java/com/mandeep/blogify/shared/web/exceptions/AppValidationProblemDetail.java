package com.mandeep.blogify.shared.web.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@JsonDeserialize(as = AppValidationProblemDetail.class)
public record AppValidationProblemDetail(
        String type,
        HttpStatus status,
        String title,
        String detail,
        String errorCode,
        List<Map<String, Object>> violations
) implements ProblemDetailFormat {

    public static AppValidationProblemDetail from(Set<? extends ConstraintViolation<?>> violations) {

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        CommonError error = CommonError.VALIDATION_FAILED;

        String typeUri = baseUrl + "/errors/" + error.errorCode().toLowerCase().replace("_", "-");

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

        return new AppValidationProblemDetail(
                typeUri,
                AppUtils.resolveStatus(error.errorType()),
                error.title(),
                error.detail(),
                error.errorCode(),
                voilationsList
        );
    }

    public static AppValidationProblemDetail from(MethodArgumentNotValidException ex) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        CommonError error = CommonError.VALIDATION_FAILED;
        String typeUri = baseUrl + "/errors/" + error.errorCode().toLowerCase().replace("_", "-");

        List<Map<String, Object>> violationsList = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, Object> vMap = new HashMap<>();
                    vMap.put("field", fieldError.getField());
                    vMap.put("invalidValue", fieldError.getRejectedValue());
                    vMap.put("message", fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value");
                    vMap.put("constraint", fieldError.getCode());
                    return Collections.unmodifiableMap(vMap);
                }).toList();

        return new AppValidationProblemDetail(
                typeUri,
                AppUtils.resolveStatus(error.errorType()),
                error.title(),
                error.detail(),
                error.errorCode(),
                violationsList
        );
    }
}
