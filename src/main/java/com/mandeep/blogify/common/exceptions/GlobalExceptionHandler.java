package com.mandeep.blogify.common.exceptions;

import com.mandeep.blogify.constants.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.VALIDATION_FAILED;
        ProblemDetail pd = problemDetailProvider(request, apiError);

        // Build detailed field errors
        List<Map<String, Object>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String message = fieldError.getField() + " " + fieldError.getDefaultMessage();

                    Map<String, Object> error = new HashMap<>();
                    error.put("field", fieldError.getField());
                    error.put("rejectedValue", fieldError.getRejectedValue());
                    error.put("message", message);
                    error.put("violation", fieldError.getCode());
                    return Collections.unmodifiableMap(error);

                })
                .toList();


        pd.setProperty("errors", fieldErrors);

        return new ResponseEntity<>(pd, apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ApiError apiError = ApiError.IMAGE_TOO_LARGE;
        ProblemDetail pd = problemDetailProvider(request, apiError);

        return new ResponseEntity<>(pd, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        ApiError apiError = ApiError.VALIDATION_FAILED;
        ProblemDetail pd = problemDetailProvider(request, apiError);

        List<Map<String, Object>> violations = ex.getConstraintViolations().stream()
                .map(voilation -> {

                    String message = voilation.getPropertyPath().toString() + " " + voilation.getMessage();
                    Map<String, Object> v = new HashMap<>();
                    v.put("field", voilation.getPropertyPath().toString());
                    v.put("invalidValue", voilation.getInvalidValue());
                    v.put("message", message);
                    v.put("constraint", voilation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());

                    return Collections.unmodifiableMap(v);
                })
                .toList();
//

        pd.setProperty("errors", violations);

        return new ResponseEntity<>(pd, apiError.getStatus());
    }


    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            org.hibernate.type.descriptor.java.CoercionException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ProblemDetail> handleCustomTypeMismatch(Exception ex, WebRequest request) {

        ApiError apiError = ApiError.TYPE_MISMATCH;
        ProblemDetail pd = problemDetailProvider(request, apiError);

        return new ResponseEntity<>(pd, apiError.getStatus());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> userNameNotFoundHandler(UsernameNotFoundException ex, WebRequest request) {
        ApiError apiError = ApiError.USER_NOT_FOUND;
        ProblemDetail pb = problemDetailProvider(request, apiError);
        return new ResponseEntity<>(pb, apiError.getStatus());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> apiExceptionHandler(ApiException apiException, WebRequest request) {
        ApiError apiError = apiException.getApiError();
        ProblemDetail pb = problemDetailProvider(request, apiError);
        return new ResponseEntity<>(pb, apiError.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> genericExceptionHandler(Exception ex, WebRequest request) {
        ApiError apiError = ApiError.INTERNAL_SERVER_ERROR;
        ProblemDetail pb = problemDetailProvider(request, apiError);
        return new ResponseEntity<>(pb, apiError.getStatus());
    }

    private ProblemDetail problemDetailProvider(WebRequest request, ApiError apiError) {

        ProblemDetail pb = ProblemDetail.forStatus(apiError.getStatus());

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();

        pb.setType(URI.create(baseUrl + apiError.getType()));
        pb.setTitle(apiError.getTitle());
        pb.setDetail(apiError.getDetail());
        pb.setInstance(URI.create(uri));
        pb.setProperty("errorCode", apiError.getErrorcode());
        pb.setProperty("timestamp", Instant.now(Clock.systemUTC()).toString());

        return pb;
    }
}


