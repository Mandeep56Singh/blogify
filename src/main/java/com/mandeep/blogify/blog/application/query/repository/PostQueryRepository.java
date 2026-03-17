package com.mandeep.blogify.blog.application.query.repository;

import com.mandeep.blogify.blog.application.dto.PostData;
import com.mandeep.blogify.blog.application.dto.PostPageItemData;
import com.mandeep.blogify.shared.dto.PaginatedResponse;

import java.util.Optional;
import java.util.UUID;

public interface PostQueryRepository {
    Optional<PostData> findById(UUID id);

    PaginatedResponse<PostPageItemData> findAllPublished(int pageNumber, int pageSize);

    boolean existsBySlug(String slug);
}
