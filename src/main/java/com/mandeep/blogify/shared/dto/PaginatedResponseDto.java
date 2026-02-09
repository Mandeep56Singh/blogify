package com.mandeep.blogify.shared.dto;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> data,
        int pageNumber,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean lastPage
) {
}
