package com.mandeep.blogify.blog.infrastructure.persistence.adapter;

import com.mandeep.blogify.blog.domain.model.entity.Post;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.repository.PostRepository;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.mapper.PostEntityMapper;
import com.mandeep.blogify.blog.infrastructure.persistence.repository.CategoryJpaRepository;
import com.mandeep.blogify.blog.infrastructure.persistence.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostCommandAdapter implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostEntityMapper postEntityMapper;
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public void save(Post post) {
        Set<CategoryEntity> categoryEntities = post.getPostCategories().value()
                .stream()
                .map(categoryId -> categoryJpaRepository.getReferenceById(categoryId.value()))
                .collect(Collectors.toSet());

        PostEntity postEntity = postJpaRepository.findById(post.getPostId().value())
                .map(existing -> {
                    postEntityMapper.updateEntity(post, existing, categoryEntities);
                    return existing;
                })
                .orElseGet(() -> postEntityMapper.toEntity(post, categoryEntities)
                );

        postJpaRepository.save(postEntity);
    }

    @Override
    public Set<String> findSlugsByPrefix(String slugPrefix) {
        return postJpaRepository.findSlugsByPrefix(slugPrefix + "%");
    }

    @Override
    public Optional<Post> findById(PostId postId) {
        return postJpaRepository.findById(postId.value()).map(postEntityMapper::toDomain);
    }

}
