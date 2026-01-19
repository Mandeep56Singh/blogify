package com.mandeep.blogify.post;

import com.mandeep.blogify.constants.ApiError;
import com.mandeep.blogify.common.PaginatedResponseDto;
import com.mandeep.blogify.post.dto.PostRequestDto;
import com.mandeep.blogify.post.dto.PostResponseDto;
import com.mandeep.blogify.category.Category;
import com.mandeep.blogify.user.UserService;
import com.mandeep.blogify.user.User;
import com.mandeep.blogify.common.exceptions.ApiException;
import com.mandeep.blogify.category.CategoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final PostMapper mapper;

    @Transactional
    public PostResponseDto createPost(@Valid PostRequestDto requestDto) {
        User author = userService.getById(requestDto.authorId());
        List<Category> categories = categoryRepository.findAllById(requestDto.categoryIds());

        if (categories.size() < requestDto.categoryIds().size()) {
            throw new ApiException(ApiError.CATEGORY_NOT_FOUND);
        }

        Post post = new Post(
                requestDto.title(),
                requestDto.content(),
                author,
                new HashSet<>(categories)
        );

        Post createdPost = postRepository.save(post);
        return mapper.toDto(createdPost);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<PostResponseDto> getAllPosts(Integer pageNumber, Integer pageSize) {

        if (pageNumber - 1 < 0) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
        }
        if (pageSize <= 0) {
            throw new ApiException(ApiError.INVALID_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Post> postPage = postRepository.findAll(pageable);

        if (pageNumber - 1 > postPage.getTotalPages()) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
        }

        List<Post> posts = postPage.getContent();
        return new PaginatedResponseDto<>(
                mapper.toDtoList(posts),
                pageNumber ,
                pageSize,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(@NotNull Long id) {
        Post post = getById(id);
        return mapper.toDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(@Valid PostRequestDto requestDto, @NotNull Long id) {
        Post post = getById(id);
        Set<Category> updatedCategories = getPostCategories(requestDto.categoryIds());

        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
        post.getCategories().clear();
        post.getCategories().addAll(updatedCategories);

        return mapper.toDto(post);
    }

    @Transactional
    public void deletePost(@NotNull Long id) {
        Post post = getById(id);
        post.softDelete();
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.POST_NOT_FOUND)
        );
    }

    public Set<Category> getPostCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() < categoryIds.size()) {
            throw new ApiException(ApiError.CATEGORY_NOT_FOUND);
        }

        return  new HashSet<>(categories);
    }

}
