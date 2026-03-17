package com.mandeep.blogify.shared.web.exceptions;

import org.springframework.http.HttpStatus;

public interface ProblemDetailFormat {
    String type();

    HttpStatus status();

    String title();

    String detail();

    String errorCode();
}
