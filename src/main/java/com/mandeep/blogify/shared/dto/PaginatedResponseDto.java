package com.mandeep.blogify.shared.dto;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> items,
        int pageNumber,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean lastPage
) implements ResponsePayload{
}
