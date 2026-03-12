package com.mandeep.blogify.blog.infrastructure.persistence.mapper;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.dto.PostData;
import com.mandeep.blogify.blog.application.dto.PostPageItemData;
import com.mandeep.blogify.blog.domain.model.entity.Post;
import com.mandeep.blogify.blog.domain.model.valueObject.*;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostEntityMapper {

    public PostEntity toEntity(Post post, Set<CategoryEntity> categories) {
        PostEntity postEntity = new PostEntity();
        postEntity.setId(post.getPostId().value());
        postEntity.setTitle(post.getPostTitle().value());
        postEntity.setSlug(post.getPostSlug().value());
        postEntity.setContent(post.getPostContent().value());
        postEntity.setAuthorId(post.getAuthorId().value());
        postEntity.setStatus(post.getPostStatus());
        postEntity.setCategories(categories);
        postEntity.setPublishedAt(post.getPublishedAt());

        return postEntity;

    }

    public void updateEntity(Post post, PostEntity entity, Set<CategoryEntity> categories) {
        entity.setTitle(post.getPostTitle().value());
        entity.setContent(post.getPostContent().value());
        entity.setAuthorId(post.getAuthorId().value());
        entity.setStatus(post.getPostStatus());
        entity.setPublishedAt(post.getPublishedAt());

        entity.getCategories().clear();
        entity.setCategories(categories);
    }
    public PostData toPostData(PostEntity postEntity) {

        Set<CategoryResponse> categories = postEntity.getCategories().stream().map(
                categoryEntity -> new CategoryResponse(categoryEntity.getId(), categoryEntity.getTitle())

        ).collect(Collectors.toSet());

        return new PostData(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getSlug(),
                postEntity.getContent(),
                categories,
                postEntity.getAuthorId(),
                postEntity.getStatus().name(),
                postEntity.getCreatedAt(),
                postEntity.getPublishedAt(),
                postEntity.getLastModifiedAt()

        );
    }

    public PostPageItemData toPageItemData(PostEntity postEntity) {
        Set<CategoryResponse> categories = postEntity.getCategories().stream().map(
                categoryEntity -> new CategoryResponse(categoryEntity.getId(), categoryEntity.getTitle())

        ).collect(Collectors.toSet());

        return new PostPageItemData(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getSlug(),
                categories,
                postEntity.getAuthorId(),
                postEntity.getCreatedAt(),
                postEntity.getPublishedAt()
        );
    }

    public Post toDomain(PostEntity postEntity) {
        return Post.reconstitute(
                new PostId(postEntity.getId()),
                new PostTitle(postEntity.getTitle()),
                new PostSlug(postEntity.getSlug()),
                new PostContent(postEntity.getContent()),
                new UserId(postEntity.getAuthorId()),
                new PostCategories(getPostEntityCategories(postEntity)),
                postEntity.getCreatedAt(),
                postEntity.getPublishedAt(),
                postEntity.getStatus()
        );

    }

    private Set<CategoryId> getPostEntityCategories(PostEntity postEntity) {
        return postEntity.getCategories().stream().map(
                categoryEntity -> new CategoryId(categoryEntity.getId())
        ).collect(Collectors.toSet());
    }
}
