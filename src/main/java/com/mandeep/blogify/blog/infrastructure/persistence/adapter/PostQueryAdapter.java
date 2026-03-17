package com.mandeep.blogify.blog.infrastructure.persistence.adapter;

import com.mandeep.blogify.blog.application.dto.PostData;
import com.mandeep.blogify.blog.application.dto.PostPageItemData;
import com.mandeep.blogify.blog.application.query.repository.PostQueryRepository;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.mapper.PostEntityMapper;
import com.mandeep.blogify.blog.infrastructure.persistence.repository.PostJpaRepository;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostQueryAdapter implements PostQueryRepository {

    private final PostEntityMapper postEntityMapper;
    private final PostJpaRepository jpaRepository;

    @Override
    public Optional<PostData> findById(UUID id) {
        return jpaRepository.findById(id).map(
                postEntityMapper::toPostData
        );
    }

    @Override
    public PaginatedResponse<PostPageItemData> findAllPublished(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by("publishedAt").descending()
        );

        Page<UUID> postIdPage = jpaRepository.findIdsOfAllPublished(pageable, PostStatus.PUBLISHED);

        if (postIdPage.isEmpty()) {
            return new PaginatedResponse<>(
                    List.of(), pageNumber, pageSize, 0, 0, true
            );
        }


        List<UUID> postIds = postIdPage.getContent();

        Map<UUID, PostEntity> postMap = jpaRepository
                .findPostsWithCategoriesByIds(postIds)
                .stream()
                .collect(Collectors.toMap(PostEntity::getId, p -> p));

        List<PostPageItemData> pageData = postIds.stream()
                .map(postMap::get)
                .map(postEntityMapper::toPageItemData)
                .toList();


        return new PaginatedResponse<>(
                pageData,
                pageNumber,
                pageSize,
                postIdPage.getTotalElements(),
                postIdPage.getTotalPages(),
                postIdPage.isLast()
        );
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }
}
