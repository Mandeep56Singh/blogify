package com.mandeep.blogify.post;

import com.mandeep.blogify.common.PaginatedResponseDto;
import com.mandeep.blogify.post.dto.PostRequestDto;
import com.mandeep.blogify.post.dto.PostResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<PostResponseDto> > getAllPost(
            @RequestParam(name = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {
        PaginatedResponseDto<PostResponseDto> responseDto = postService.getAllPosts(pageNumber, pageSize);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable @NotNull Long id
    ) {
        PostResponseDto responseDto = postService.getPostById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostRequestDto requestDto
    ) {
        PostResponseDto responseDto = postService.createPost(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PostResponseDto> updatePost (
            @Valid @RequestBody PostRequestDto requestDto,
            @PathVariable @NotNull Long id
    ) {
        PostResponseDto responseDto = postService.updatePost(requestDto, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePost (
            @PathVariable @NotNull Long id
    ) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
