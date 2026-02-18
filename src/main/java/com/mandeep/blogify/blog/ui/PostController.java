package com.mandeep.blogify.blog.ui;

import com.mandeep.blogify.blog.application.dto.request.PostRequestDto;
import com.mandeep.blogify.blog.application.dto.response.PostItemDto;
import com.mandeep.blogify.blog.application.dto.response.PostResponseDto;
import com.mandeep.blogify.blog.application.service.PostService;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.PageError;
import com.mandeep.blogify.shared.exceptions.validation.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/posts")
@Tag(name = "Posts", description = "Blog post management APIs")
class PostController {

    private final PostService postService;
    private final RequestValidator validator;

    @Operation(
            summary = "Get paginated posts",
            description = "Retrieve a paginated list of blog posts. Provide pageNumber and pageSize query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<ResponseDto<PaginatedResponseDto<PostItemDto>>> getAllPost(
            @Parameter(description = "Page number (default: 1)") @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            @Parameter(description = "Number of items per page (default: 10)") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        Optional<PageError> pageError = AppUtils.validatePage(pageNumber - 1, pageSize);

        if (pageError.isPresent()) {
            return new ResponseEntity<>(ResponseDto.failure(pageError.get()), pageError.get().status());
        }

        ResponseDto<PaginatedResponseDto<PostItemDto>> responseDto = postService.getAllPosts(pageNumber, pageSize);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Get post by ID", description = "Retrieve a single post using its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<PostResponseDto>> getPost(
            @Parameter(description = "ID of the post to fetch") @PathVariable Long id
    ) {
        ResponseDto<PostResponseDto> responseDto = postService.getPostById(id);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new post",
            description = "Create a blog post. Requires authentication and valid request body",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Post already exists")
    })
    @PostMapping
    public ResponseEntity<ResponseDto<PostResponseDto>> createPost(
            @Parameter(description = "Post request payload", required = true)
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

    @Operation(
            summary = "Update a post",
            description = "Update an existing blog post by ID. Requires authentication and valid request body",
            security = @SecurityRequirement(name = "bearerAuth")
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<PostResponseDto>> updatePost(
            @Parameter(description = "Post request payload", required = true) @RequestBody PostRequestDto requestDto,
            @Parameter(description = "ID of the post to update") @PathVariable Long id
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

    @Operation(summary = "Delete a post",
            description = "Soft-delete a post by ID. Requires authentication",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<?>> deletePost(
            @Parameter(description = "ID of the post to delete") @PathVariable Long id
    ) {
        Optional<ResponseDto<?>> responseDto = postService.deletePost(id);
        return responseDto.<ResponseEntity<ResponseDto<?>>>map(
                        dto -> new ResponseEntity<>(dto, dto.error().status())).
                orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
