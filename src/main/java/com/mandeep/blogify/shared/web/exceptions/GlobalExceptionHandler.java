package com.mandeep.blogify.shared.web.exceptions;

import com.mandeep.blogify.shared.domain.exception.DomainException;
import com.mandeep.blogify.shared.domain.exception.enums.CommonError;
import com.mandeep.blogify.shared.dto.Response;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // @Valid annotation exception

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        AppValidationProblemDetail problemDetail = AppValidationProblemDetail.from(ex.getConstraintViolations());
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        AppValidationProblemDetail problemDetail = AppValidationProblemDetail.from(ex);
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }

    // trying to access admin route
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<?>> handleAccessDeniedException(AccessDeniedException ex) {
        CommonError error = CommonError.ACCESS_DENIED;
        AppProblemDetail problemDetail = AppProblemDetail.from(error);
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }


    // type mismatch exception handlers
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        CommonError error = CommonError.TYPE_MISMATCH;
        AppProblemDetail problemDetail = AppProblemDetail.from(error);
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        CommonError error = CommonError.TYPE_MISMATCH;
        AppProblemDetail problemDetail = AppProblemDetail.from(error);
        return new ResponseEntity<>(Response.failure(problemDetail), status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        AppProblemDetail problemDetail = AppProblemDetail.from(CommonError.TYPE_MISMATCH);
        return new ResponseEntity<>(Response.failure(problemDetail), status);
    }

    // for wrong request url
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        AppProblemDetail problemDetail = AppProblemDetail.from(CommonError.RESOURCE_NOT_FOUND);
        return new ResponseEntity<>(Response.failure(problemDetail), status);
    }

    // image size too large
    @Override
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        CommonError error = CommonError.IMAGE_TOO_LARGE;
        AppProblemDetail problemDetail = AppProblemDetail.from(error);
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(Exception ex) {

        CommonError error = CommonError.INVALID_CREDENTIALS;
        AppProblemDetail problemDetail = AppProblemDetail.from(error);
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Response<?>> handleDomainException(DomainException ex) {
        AppProblemDetail problem = AppProblemDetail.from(ex.getError());
        return new ResponseEntity<>(Response.failure(problem), problem.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception ex) {

        CommonError error = CommonError.INTERNAL_SERVER_ERROR;
        AppProblemDetail problemDetail = AppProblemDetail.from(error, ex.getMessage());
        return new ResponseEntity<>(Response.failure(problemDetail), problemDetail.status());
    }


}