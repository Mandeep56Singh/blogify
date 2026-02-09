package com.mandeep.blogify.blog.domain.exceptions.category;

import com.mandeep.blogify.shared.exceptions.CommonAppError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mandeep.blogify.shared.AppUtils.getProblemDetail;
import static com.mandeep.blogify.shared.AppUtils.getProblemDetailWithVoilations;

@ControllerAdvice
public class CategoryExceptionHandler extends ResponseEntityExceptionHandler {

    // Validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        CommonAppError error = CommonAppError.VALIDATION_FAILED;
        ProblemDetail pd = getProblemDetailWithVoilations(request, ex, error);

        return new ResponseEntity<>(pd, error.getStatus());
    }


    // Type Mismatch
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            org.hibernate.type.descriptor.java.CoercionException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ProblemDetail> handleCustomTypeMismatch(Exception ex, WebRequest request) {

        CommonAppError error = CommonAppError.TYPE_MISMATCH;
        ProblemDetail pd = getProblemDetail(request, error);

        return new ResponseEntity<>(pd, error.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> genericExceptionHandler(Exception ex, WebRequest request) {
        CommonAppError error = CommonAppError.INTERNAL_SERVER_ERROR;
        ProblemDetail pb = getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }

    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ProblemDetail> handleUserException(CategoryException ex, WebRequest request) {
        CategoryError error = ex.getCategoryError();
        ProblemDetail pb = getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }
}
