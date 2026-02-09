package com.mandeep.blogify.blog.domain.exceptions.category;

import com.mandeep.blogify.shared.exceptions.AppError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CategoryError implements AppError {

    CATEGORY_ALREADY_EXITS(
            "/category-already-exists",
            HttpStatus.CONFLICT,
            "category already exists",
            "Category already exists, please try different category title",
            "CATEGORY_ALREADY_EXISTS"
    ),
    CATEGORY_NOT_FOUND(
            "/category-not-found",
            HttpStatus.NOT_FOUND,
            "category not found",
            "Category not found, please try different category",
            "CATEGORY_NOT_FOUND"
    ),
    ;

    private final String type;
    private final HttpStatus status;
    private final String title;
    private final String detail;
    private final String errorCode;

    CategoryError(String type, HttpStatus status, String title, String detail, String errorcode) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.errorCode = errorcode;
    }
}
