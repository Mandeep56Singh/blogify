package com.mandeep.blogify.shared.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final DomainError error;

    public DomainException(DomainError error, String message) {
        super(message);
        this.error = error;
    }

}
