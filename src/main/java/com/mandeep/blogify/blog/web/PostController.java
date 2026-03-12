package com.mandeep.blogify.blog.web;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.AuthView;
import com.mandeep.blogify.blog.application.command.PostCommandService;
import com.mandeep.blogify.blog.application.dto.PostPageItemResponse;
import com.mandeep.blogify.blog.application.dto.PostResponse;
import com.mandeep.blogify.blog.application.query.PostQueryService;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.web.dto.PostWebRequest;
import com.mandeep.blogify.blog.web.mapper.PostMapper;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import com.mandeep.blogify.shared.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/posts")
@Validated
@Tag(name = "Posts", description = "Blog post management APIs")
class PostController {


    private final PostQueryService queryService;
    private final PostCommandService commandService;
    private final PostMapper postMapper;
    private final AuthFacade authFacade;

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
    public ResponseEntity<Response<PaginatedResponse<PostPageItemResponse>>> getAllPost(
            @Parameter(description = "Page number (default: 1)") @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            @Parameter(description = "Number of items per page (default: 10)") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        int realPageNumber = pageNumber - 1;

        AppUtils.validatePage(realPageNumber, pageSize);

        var page = queryService.getAllPublishedPosts(realPageNumber, pageSize);

        return ResponseEntity.ok(Response.success(page));
    }

    @Operation(summary = "Get post by ID", description = "Retrieve a single post using its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<PostResponse>> getPost(
            @Parameter(description = "ID of the post to fetch") @PathVariable UUID id
    ) {
        PostResponse postResponse = queryService.getPostById(id);

        return ResponseEntity.ok(Response.success(postResponse));
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
    public ResponseEntity<Response<PostResponse>> createPost(
            @Parameter(description = "Post request payload", required = true)
            @RequestBody @Valid PostWebRequest webRequest
    ) {
        UUID currentUserId = authFacade.getCurrentUserId().map(
                AuthView::id
        ).orElseThrow(
                AccountException::accountNotAuthenticated
        );

        UUID id  = commandService.createPost(postMapper.toRequest(webRequest, currentUserId));
        PostResponse postResponse = queryService.getPostById(id);

        return new ResponseEntity<>(Response.success(postResponse), HttpStatus.CREATED);
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
    public ResponseEntity<Response<PostResponse>> updatePost(
            @Parameter(description = "Post request payload", required = true) @RequestBody @Valid PostWebRequest webRequest,
            @Parameter(description = "ID of the post to update") @PathVariable UUID id
    ) {
        UUID currentUserId = authFacade.getCurrentUserId().map(
                AuthView::id
        ).orElseThrow(
                AccountException::accountNotAuthenticated
        );

        commandService.updatePost(id, postMapper.toRequest(webRequest, currentUserId));
        PostResponse postResponse = queryService.getPostById(id);

        return ResponseEntity.ok(Response.success(postResponse));
    }


    @Operation(
            summary = "Publish a post",
            description = "Changes the status of a draft or archived post to PUBLISHED.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post published successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Not the author)"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PatchMapping(value = "/{id}/publish")
    public ResponseEntity<Response<PostResponse>> publishPost(
            @Parameter(description = "ID for the post to publish") @PathVariable UUID id
    ) {
        UUID currentUserId = authFacade.getCurrentUserId().map(
                AuthView::id
        ).orElseThrow(
                AccountException::accountNotAuthenticated
        );

        commandService.publishPost(id, currentUserId);
        PostResponse postResponse = queryService.getPostById(id);
        return ResponseEntity.ok(Response.success(postResponse));
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
    public ResponseEntity<Response<?>> deletePost(
            @Parameter(description = "ID of the post to delete") @PathVariable UUID id
    ) {
        UUID currentUserId = authFacade.getCurrentUserId().map(
                AuthView::id
        ).orElseThrow(
                AccountException::accountNotAuthenticated
        );

        commandService.deletePost(id, currentUserId);
        return ResponseEntity.ok(Response.success(null));
    }
}
