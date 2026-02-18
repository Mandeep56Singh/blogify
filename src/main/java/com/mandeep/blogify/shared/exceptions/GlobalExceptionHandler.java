package com.mandeep.blogify.shared.exceptions;

import com.mandeep.blogify.auth.domain.exception.AuthError;
import com.mandeep.blogify.shared.dto.ResponseDto;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // trying to access admin route
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto<?>> handleAccessDeniedException(AccessDeniedException ex) {
        CommonAppError error = CommonAppError.ACCESS_DENIED;
        AppProblem problem = AppProblem.getDetail(error);
        return new ResponseEntity<>(ResponseDto.failure(problem), error.status());
    }

    // type mismatch exception handlers
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        CommonAppError error = CommonAppError.TYPE_MISMATCH;
        AppProblem problem = AppProblem.getDetail(error);
        return new ResponseEntity<>(ResponseDto.failure(problem), error.status());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        CommonAppError error = CommonAppError.TYPE_MISMATCH;
        AppProblem problem = AppProblem.getDetail(error);
        return new ResponseEntity<>(ResponseDto.failure(problem), status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        AppProblem problem = AppProblem.getDetail(CommonAppError.TYPE_MISMATCH);
        return new ResponseEntity<>(ResponseDto.failure(problem), status);
    }

    // for wrong request url
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        AppProblem problem = AppProblem.getDetail(CommonAppError.RESOURCE_NOT_FOUND);
        return new ResponseEntity<>(ResponseDto.failure(problem), status);
    }

    // image size too large
    @Override
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        CommonAppError error = CommonAppError.IMAGE_TOO_LARGE;
        AppProblem problem = AppProblem.getDetail(error);
        return new ResponseEntity<>(ResponseDto.failure(problem), error.status());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(Exception ex) {
        AuthError error = AuthError.INVALID_CREDENTIALS;
        AppProblem problem = AppProblem.getDetail(error);
        return new ResponseEntity<>(ResponseDto.failure(problem), error.status());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception ex) {

        CommonAppError error = CommonAppError.INTERNAL_SERVER_ERROR;
        AppProblem problem = AppProblem.getDetail(error);
        ResponseDto<?> responseDto = ResponseDto.failure(problem);
        return new ResponseEntity<>(responseDto, error.status());
    }

}