package com.mandeep.blogify.shared.dto;

import com.mandeep.blogify.shared.web.exceptions.ProblemDetailFormat;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;


public record Response<T>(
        boolean success,
        String instance,
        T data,
        Map<String, Object> metaData,
        ProblemDetailFormat error,
        Instant timestamp
) {
    public static <T> Response<T> success(T data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new Response<>(true, uri, data, null, null, Instant.now(Clock.systemUTC()));
    }

    public static <T> Response<T> success(T data, Map<String, Object> metaData) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new Response<>(true, uri, data, metaData, null, Instant.now(Clock.systemUTC()));
    }

    public static Response<Void> failure(ProblemDetailFormat error) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return new Response<>(false, uri, null, null, error, Instant.now(Clock.systemUTC()));
    }
}
