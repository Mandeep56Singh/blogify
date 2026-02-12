package com.mandeep.blogify.shared.dto;

import com.mandeep.blogify.shared.exceptions.AppError;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;


public record ResponseDto<T extends ResponsePayload>(
        boolean success,
        String instance,
        T data,
        Map<String, Object> metaData,
        AppError error,
        Instant timestamp
) {
    public static <T extends ResponsePayload> ResponseDto<T> success(T data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new ResponseDto<>(true, uri, data, null, null, Instant.now(Clock.systemUTC()));
    }

    public static <T extends ResponsePayload> ResponseDto<T> success(T data, Map<String, Object> metaData) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new ResponseDto<>(true, uri, data, metaData, null, Instant.now(Clock.systemUTC()));
    }

    public static <T extends ResponsePayload> ResponseDto<T> failure(AppError error) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new ResponseDto<>(false, uri, null, null, error, Instant.now(Clock.systemUTC()));
    }
}
