package com.mandeep.blogify.shared.domain.exception.enums;

import com.mandeep.blogify.shared.domain.exception.DomainError;

public enum PageError implements DomainError {

    INVALID_PAGE_NUMBER(
            "Invalid page number",
            "You have requested for invalid page number, please make sure page number is positive, non-zero and within total pages",
            DomainErrorType.INVALID_INPUT,
            "INVALID_PAGE_NUMBER"
    ),
    INVALID_PAGE_SIZE(
            "Invalid page size",
            "You have requested for invalid page size, please make sure page size is positive, non-zero and within total elements",
            DomainErrorType.INVALID_INPUT,
            "INVALID_PAGE_SIZE"
    ),
    ;

    private final String title;
    private final String detail;
    private final DomainErrorType errorType;
    private final String errorCode;


    PageError(String title, String detail, DomainErrorType errorType, String errorCode) {
        this.title = title;
        this.detail = detail;
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    @Override
    public DomainErrorType errorType() {
        return errorType;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String detail() {
        return detail;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }
}
