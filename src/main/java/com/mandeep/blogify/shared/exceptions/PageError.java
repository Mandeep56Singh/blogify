package com.mandeep.blogify.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PageError implements AppError{

    INVALID_PAGE_NUMBER(
            "/invalid-page-number",
            HttpStatus.BAD_REQUEST,
            "Invalid page number",
            "You have requested for invalid page number, please make sure page number is positive, non-zero and within total pages",
            "INVALID_PAGE_NUMBER"
    ),
    INVALID_PAGE_SIZE(
            "/invalid-page-size",
            HttpStatus.BAD_REQUEST,
            "Invalid page size",
            "You have requested for invalid page size, please make sure page size is positive, non-zero and within total elements",
            "INVALID_PAGE_SIZE"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    PageError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }

}
