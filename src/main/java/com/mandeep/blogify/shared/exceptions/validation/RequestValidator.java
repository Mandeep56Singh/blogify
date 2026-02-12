package com.mandeep.blogify.shared.exceptions.validation;

import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.dto.ResponsePayload;
import com.mandeep.blogify.shared.exceptions.AppValidationProblem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator {
    private final Validator validator;

    // two generics : reason,
    public <U extends ResponsePayload, V> Optional<ResponseDto<U>> validate(V requestData) {
        Set<ConstraintViolation<V>> violations = validator.validate(requestData);

        if (violations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ResponseDto.failure(AppValidationProblem.getProblemDetail(violations)));
    }
}
