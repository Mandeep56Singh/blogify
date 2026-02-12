package com.mandeep.blogify.shared;

import com.mandeep.blogify.shared.exceptions.PageError;

import java.util.Optional;



public class AppUtils {
    public static Optional<PageError> validatePage(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            return Optional.of(PageError.INVALID_PAGE_NUMBER);
        } else if (pageSize < 0 || pageSize > AppConstants.MAX_PAGE_SIZE) {
            return Optional.of(PageError.INVALID_PAGE_SIZE);
        } else {
            return Optional.empty();
        }
    }

}
