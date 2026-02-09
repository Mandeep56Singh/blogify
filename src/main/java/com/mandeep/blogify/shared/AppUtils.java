package com.mandeep.blogify.shared;

import com.mandeep.blogify.shared.exceptions.ApiException;
import com.mandeep.blogify.shared.exceptions.AppError;
import com.mandeep.blogify.shared.exceptions.PageError;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
public class AppUtils {

    public static void validatePage(@NotNull Integer pageNumber, @NotNull Integer pageSize) {
        if (pageNumber < 0) {
            throw new ApiException(PageError.INVALID_PAGE_NUMBER);
        }

        if (pageSize <= 0 || pageSize > AppConstants.MAX_PAGE_SIZE) {
            throw new ApiException(PageError.INVALID_PAGE_SIZE);
        }
    }
    public static ProblemDetail getProblemDetail(WebRequest request, AppError error) {

        ProblemDetail pb = ProblemDetail.forStatus(error.getStatus());

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();

        pb.setType(URI.create(baseUrl + error.getType()));
        pb.setTitle(error.getTitle());
        pb.setDetail(error.getDetail());
        pb.setInstance(URI.create(uri));
        pb.setProperty("errorCode", error.getErrorCode());
        pb.setProperty("timestamp", Instant.now(Clock.systemUTC()).toString());

        return pb;
    }

    public static ProblemDetail getProblemDetailWithVoilations(WebRequest request, ConstraintViolationException ex, AppError error) {
        ProblemDetail pd = getProblemDetail(request, error);

        List<Map<String, Object>> violations = ex.getConstraintViolations().stream()
                .map(violation -> {

                    String message = violation.getPropertyPath().toString() + " " + violation.getMessage();
                    Map<String, Object> v = new HashMap<>();
                    v.put("field", violation.getPropertyPath().toString());
                    v.put("invalidValue", violation.getInvalidValue());
                    v.put("message", message);
                    v.put("constraint", violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());

                    return Collections.unmodifiableMap(v);
                })
                .toList();
//

        pd.setProperty("errors", violations);
        return pd;
    }
}
