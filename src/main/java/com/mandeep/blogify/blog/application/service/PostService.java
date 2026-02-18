package com.mandeep.blogify.blog.application.service;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.AuthenticatedUserView;
import com.mandeep.blogify.blog.application.dto.request.PostRequestDto;
import com.mandeep.blogify.blog.application.dto.response.PostItemDto;
import com.mandeep.blogify.blog.application.dto.response.PostResponseDto;
import com.mandeep.blogify.blog.application.mapping.PostMapper;
import com.mandeep.blogify.blog.domain.entity.Category;
import com.mandeep.blogify.blog.domain.entity.Post;
import com.mandeep.blogify.blog.domain.exceptions.CategoryError;
import com.mandeep.blogify.blog.domain.exceptions.PostError;
import com.mandeep.blogify.blog.domain.repository.PostRepository;
import com.mandeep.blogify.shared.dto.AuthorView;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.CommonAppError;
import com.mandeep.blogify.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final UserFacade userFacade;
    private final AuthFacade authFacade;
    private final PostMapper postMapper;

    @Transactional
    public ResponseDto<PostResponseDto> createPost(PostRequestDto requestDto) {
        Optional<AuthenticatedUserView> authUser = authFacade.getAuthenticatedUser();

        if (authUser.isEmpty()) {
            return ResponseDto.failure(CommonAppError.UNAUTHORIZED_ACCESS);
        }

        if (postRepository.existsByTitle(requestDto.title())) {
            return ResponseDto.failure(PostError.POST_ALREADY_EXITS);
        }

        List<Category> categories = categoryService.getCategoriesById(requestDto.categoryIds());

        if (categories.size() < requestDto.categoryIds().size()) {
            return ResponseDto.failure(CategoryError.CATEGORY_NOT_FOUND);
        }

        Post post = new Post(
                requestDto.title(),
                requestDto.content(),
                authUser.get().id(),
                new HashSet<>(categories)
        );

        AuthorView authorView = postMapper.toAuthor(authUser.get());
        Post createdPost = postRepository.save(post);
        return ResponseDto.success(postMapper.toPostDto(createdPost, authorView));
    }

    @Transactional(readOnly = true)
    public ResponseDto<PaginatedResponseDto<PostItemDto>> getAllPosts(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Post> postPage = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        List<Post> posts = postPage.getContent();
        Set<Long> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());
        Map<Long, AuthorView> authors = userFacade.getAuthors(authorIds);

        List<PostItemDto> postResponseDtoList = posts.stream().map(
                post -> {
                    AuthorView authorView = authors.get(post.getAuthorId());
                    return postMapper.toItemDto(post, authorView);
                }
        ).toList();

        return ResponseDto.success(new PaginatedResponseDto<>(
                postResponseDtoList,
                pageNumber,
                pageSize,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast())
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<PostResponseDto> getPostById(Long id) {
        return getById(id)
                .map(post -> {
                    // We map the post to a ResponseDto by looking up the author
                    return userFacade.getPostAuthor(post.getAuthorId())
                            .map(author -> ResponseDto.success(postMapper.toPostDto(post, author)))
                            .orElseGet(() -> ResponseDto.failure(PostError.AUTHOR_NOT_FOUND));
                })
                .orElseGet(() -> ResponseDto.failure(PostError.POST_NOT_FOUND));
    }

    @Transactional
    public ResponseDto<PostResponseDto> updatePost(PostRequestDto requestDto, Long id) {
        return getById(id).map(post ->

                getPostCategories(requestDto.categoryIds()).map(
                        (updatedCategories) -> userFacade.getPostAuthor(post.getAuthorId()).map(
                                author -> {
                                    post.setTitle(requestDto.title());
                                    post.setContent(requestDto.content());
                                    post.getCategories().clear();
                                    post.getCategories().addAll(updatedCategories);

                                    return ResponseDto.success(postMapper.toPostDto(post, author));
                                }
                        ).orElseGet(() -> ResponseDto.failure(PostError.POST_NOT_FOUND))
                ).orElseGet(() -> ResponseDto.failure(CategoryError.CATEGORY_NOT_FOUND))

        ).orElseGet(() -> ResponseDto.failure(PostError.POST_NOT_FOUND));

    }

    @Transactional
    public Optional<ResponseDto<?>> deletePost(Long id) {

        Optional<AuthenticatedUserView> currentUser = authFacade.getAuthenticatedUser();

        Optional<Post> post = getById(id);
        if (post.isPresent()) {
            if (currentUser.isEmpty() ||
                    !Objects.equals(post.get().getAuthorId(), currentUser.get().id())) {
                return Optional.of(ResponseDto.failure(CommonAppError.UNAUTHORIZED_ACCESS));
            }
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
