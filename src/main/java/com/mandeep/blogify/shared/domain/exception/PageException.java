package com.mandeep.blogify.shared.domain.exception;

import com.mandeep.blogify.shared.domain.exception.enums.PageError;

public class PageException extends DomainException {
    private PageException(PageError pageError, String message) {
        super(pageError, message);
    }

    public static PageException invalidPageNumber() {
        return new PageException(
                PageError.INVALID_PAGE_NUMBER,
                PageError.INVALID_PAGE_NUMBER.detail()
        );
    }

    public static PageException invalidPageSize() {
        return new PageException(
                PageError.INVALID_PAGE_SIZE,
                PageError.INVALID_PAGE_SIZE.detail()
        );
    }
}
