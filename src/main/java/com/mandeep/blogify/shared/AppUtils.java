package com.mandeep.blogify.shared;

import com.mandeep.blogify.shared.domain.exception.PageException;
import com.mandeep.blogify.shared.domain.exception.enums.DomainErrorType;
import org.springframework.http.HttpStatus;


public class AppUtils {
    public static void validatePage(int pageNumber, int pageSize) {
        if (pageNumber < 0) throw PageException.invalidPageNumber();
        if (pageSize <= 0 || pageSize > AppConstants.MAX_PAGE_SIZE) throw PageException.invalidPageSize();
    }

    public static HttpStatus resolveStatus(DomainErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case UNEXPECTED -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
