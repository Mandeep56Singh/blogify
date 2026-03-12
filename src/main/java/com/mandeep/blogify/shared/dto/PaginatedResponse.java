package com.mandeep.blogify.shared.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> items,
        int pageNumber,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean lastPage
) {
}
