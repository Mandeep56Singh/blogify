package com.mandeep.blogify.blog.application.service;

import com.mandeep.blogify.blog.application.dto.request.PostRequestDto;
import com.mandeep.blogify.blog.application.dto.response.PostResponseDto;
import com.mandeep.blogify.blog.application.mapping.PostMapper;
import com.mandeep.blogify.blog.domain.entity.Category;
import com.mandeep.blogify.blog.domain.entity.Post;
import com.mandeep.blogify.blog.domain.exceptions.CategoryError;
import com.mandeep.blogify.blog.domain.exceptions.PostError;
import com.mandeep.blogify.blog.domain.repository.PostRepository;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final UserFacade userFacade;
    private final PostMapper mapper;

    @Transactional
    public ResponseDto<PostResponseDto> createPost(PostRequestDto requestDto) {
        Optional<UserView> author = userFacade.getUserById(requestDto.authorId());

        if (author.isEmpty()) {
            return ResponseDto.failure(PostError.AUTHOR_NOT_FOUND);
        }

        if(postRepository.existsByTitle(requestDto.title())) {
            return ResponseDto.failure(PostError.POST_ALREADY_EXITS);
        }

        List<Category> categories = categoryService.getCategoriesById(requestDto.categoryIds());

        if (categories.size() < requestDto.categoryIds().size()) {
            return ResponseDto.failure(CategoryError.CATEGORY_NOT_FOUND);
        }

        Post post = new Post(requestDto.title(), requestDto.content(), author.get().id(), new HashSet<>(categories));

        Post createdPost = postRepository.save(post);
        return ResponseDto.success(mapper.toDto(createdPost));
    }

    @Transactional(readOnly = true)
    public ResponseDto<PaginatedResponseDto<PostResponseDto>> getAllPosts(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Post> postPage = postRepository.findAll(pageable);


        List<Post> posts = postPage.getContent();
        return ResponseDto.success(new PaginatedResponseDto<>(mapper.toDtoList(posts), pageNumber, pageSize, postPage.getTotalElements(), postPage.getTotalPages(), postPage.isLast()));
    }

    @Transactional(readOnly = true)
    public ResponseDto<PostResponseDto> getPostById(Long id) {
        return getById(id).map(post -> ResponseDto.success(mapper.toDto(post))).orElseGet(() -> ResponseDto.failure(PostError.POST_NOT_FOUND));
    }

    @Transactional
    public ResponseDto<PostResponseDto> updatePost(PostRequestDto requestDto, Long id) {
        return getById(id).map(post ->

                getPostCategories(requestDto.categoryIds()).map(
                        (updatedCategories) -> {
                            post.setTitle(requestDto.title());
                            post.setContent(requestDto.content());
                            post.getCategories().clear();
                            post.getCategories().addAll(updatedCategories);

                            return ResponseDto.success(mapper.toDto(post));
                        }
                ).orElseGet(() -> ResponseDto.failure(CategoryError.CATEGORY_NOT_FOUND))

        ).orElseGet(() -> ResponseDto.failure(PostError.POST_NOT_FOUND));

    }

    @Transactional
    public Optional<ResponseDto<?>> deletePost(Long id) {
        Optional<Post> post = getById(id);
        if (post.isPresent()) {
            post.get().softDelete();
            return Optional.empty();
        }
        return Optional.of(ResponseDto.failure(PostError.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Set<Category>> getPostCategories(List<Long> categoryIds) {
        List<Category> categories = categoryService.getCategoriesById(categoryIds);

        if (categories.size() < categoryIds.size()) {
            return Optional.empty();
        }

        return Optional.of(new HashSet<>(categories));
    }

}
