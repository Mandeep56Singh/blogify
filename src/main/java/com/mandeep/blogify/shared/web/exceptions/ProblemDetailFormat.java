package com.mandeep.blogify.shared.web.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.HttpStatus;

@JsonDeserialize(using = ProblemDetailDeserializer.class)
public sealed interface ProblemDetailFormat
permits AppProblemDetail, AppValidationProblemDetail{
    String type();

    HttpStatus status();

    String title();

    String detail();

    String errorCode();
}
