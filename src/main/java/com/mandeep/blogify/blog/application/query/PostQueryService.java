package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.dto.*;
import com.mandeep.blogify.blog.application.query.repository.PostQueryRepository;
import com.mandeep.blogify.blog.domain.exceptions.PostException;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostQueryRepository queryRepository;
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID id) {

        log.debug("post.fetch.attempt id={}", id);

        PostId postId = new PostId(id);
        PostData postData = queryRepository.findById(postId.value()).orElseThrow(
                () -> PostException.postNotFound(postId)
        );
        UserView author = userFacade.getUserById(postData.authorId()).orElseThrow(
                PostException::authorNotFound
        );

        AuthorData authorData = new AuthorData(author.id(), author.userName());

        return mapToResponse(postData, authorData);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<PostPageItemResponse> getAllPublishedPosts(int pageNumber, int pageSize) {

        log.debug("post.fetch.all.attempt pageNumber={} pageSize={}", pageNumber, pageSize);
        PaginatedResponse<PostPageItemData> pagedData = queryRepository.findAllPublished(pageNumber, pageSize);

        if (pagedData.items().isEmpty()) {
            return new PaginatedResponse<>(List.of(), pageNumber, pageSize, 0, 0, true);
        }

        Set<UUID> authorIds = pagedData.items().stream()
                .map(PostPageItemData::authorId)
                .collect(Collectors.toSet());

        Map<UUID, UserView> authorsMap = userFacade.getUsersById(authorIds);

        List<PostPageItemResponse> pagePosts = pagedData.items().stream()
                .map(postData -> {
                    UserView author = authorsMap.get(postData.authorId());
                    AuthorData authorData = author != null
                            ? new AuthorData(author.id(), author.userName())
                            : new AuthorData(postData.authorId(), "Unknown Author");

                    return new PostPageItemResponse(
                            postData.postId(),
                            postData.title(),
                            postData.slug(),
                            postData.categories(),
                            authorData,
                            postData.createdAt(),
                            postData.publishedAt()
                    );
                })
                .toList();

        return new PaginatedResponse<>(
                pagePosts,
                pagedData.pageNumber(),
                pagedData.pageSize(),
                pagedData.totalItems(),
                pagedData.totalPages(),
                pagedData.lastPage()
        );
    }


    private PostResponse mapToResponse(PostData postData, AuthorData authorData) {
        return new PostResponse(
                postData.postId(),
                postData.title(),
                postData.slug(),
                postData.content(),
                postData.categories(),
                authorData,
                postData.status(),
                postData.createdAt(),
                postData.publishedAt(),
                postData.lastModifiedAt()
        );
    }


}
