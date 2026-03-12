package com.mandeep.blogify.shared.domain.exception;

import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;

public interface DomainError {
    String title();
    String detail();
    DomainErrorType errorType();
    String errorCode();
}
