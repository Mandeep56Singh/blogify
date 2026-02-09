package com.mandeep.blogify.blog.domain.exceptions.imageUpload;

import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.exceptions.CommonAppError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mandeep.blogify.shared.AppUtils.getProblemDetail;
import static com.mandeep.blogify.shared.AppUtils.getProblemDetailWithVoilations;

public class ImageUploadExceptionHandler extends ResponseEntityExceptionHandler {

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

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ImageUploadError apiError = ImageUploadError.IMAGE_TOO_LARGE;
        ProblemDetail pd = getProblemDetail(request, apiError);

        return new ResponseEntity<>(pd, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> genericExceptionHandler(Exception ex, WebRequest request) {
        CommonAppError error = CommonAppError.INTERNAL_SERVER_ERROR;
        ProblemDetail pb = getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ProblemDetail> handleCategoryException(WebRequest request, ImageUploadException ex) {

        ImageUploadError error = ex.getImageUploadError();
        ProblemDetail pb = AppUtils.getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }
}
