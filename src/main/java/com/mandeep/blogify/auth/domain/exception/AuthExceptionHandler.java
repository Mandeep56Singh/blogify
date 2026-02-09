package com.mandeep.blogify.auth.domain.exception;

import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.exceptions.AppError;
import com.mandeep.blogify.shared.exceptions.CommonAppError;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mandeep.blogify.shared.AppUtils.getProblemDetail;

@ControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ProblemDetail> userNameNotFoundHandler(UsernameNotFoundException ex, WebRequest request) {
        AppError error = AuthError.INVALID_CREDENTIALS;
        ProblemDetail pb = AppUtils.getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> genericExceptionHandler(Exception ex, WebRequest request) {
        CommonAppError error = CommonAppError.INTERNAL_SERVER_ERROR;
        ProblemDetail pb = getProblemDetail(request, error);
        return new ResponseEntity<>(pb, error.getStatus());
    }
}
