package com.mandeep.blogify.blog.ui;

import com.mandeep.blogify.blog.application.dto.request.PostRequestDto;
import com.mandeep.blogify.blog.application.dto.response.PostResponseDto;
import com.mandeep.blogify.blog.application.service.PostService;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.PageError;
import com.mandeep.blogify.shared.exceptions.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/posts")
class PostController {

    private final PostService postService;
    private final RequestValidator validator;

    @GetMapping
    public ResponseEntity<ResponseDto<PaginatedResponseDto<PostResponseDto>>> getAllPost(
            @RequestParam(name = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {

        Optional<PageError> pageError = AppUtils.validatePage(pageNumber - 1, pageSize);

        if (pageError.isPresent()) {
            return new ResponseEntity<>(ResponseDto.failure(pageError.get()), pageError.get().status());
        }

        ResponseDto<PaginatedResponseDto<PostResponseDto>> responseDto = postService.getAllPosts(pageNumber, pageSize);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<PostResponseDto>> getPost(
            @PathVariable Long id
    ) {
        ResponseDto<PostResponseDto> responseDto = postService.getPostById(id);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ResponseDto<PostResponseDto>> createPost(
            @RequestBody PostRequestDto requestDto
    ) {

        Optional<ResponseDto<PostResponseDto>> validationError = validator.validate(requestDto);
        if (validationError.isPresent()) {
            return new ResponseEntity<>(validationError.get(), validationError.get().error().status());
        }

        ResponseDto<PostResponseDto> responseDto = postService.createPost(requestDto);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<PostResponseDto>> updatePost(
            @RequestBody PostRequestDto requestDto,
            @PathVariable Long id
    ) {
        Optional<ResponseDto<PostResponseDto>> validationError = validator.validate(requestDto);
        if (validationError.isPresent()) {
            return new ResponseEntity<>(validationError.get(), validationError.get().error().status());
        }

        ResponseDto<PostResponseDto> responseDto = postService.updatePost(requestDto, id);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<?>> deletePost(
            @PathVariable Long id
    ) {
        Optional<ResponseDto<?>> responseDto = postService.deletePost(id);
        return responseDto.<ResponseEntity<ResponseDto<?>>>map(
                        dto -> new ResponseEntity<>(dto, dto.error().status())).
                orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
