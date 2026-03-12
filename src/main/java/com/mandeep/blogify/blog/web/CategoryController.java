package com.mandeep.blogify.blog.web;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.AuthView;
import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.query.CategoryQueryService;
import com.mandeep.blogify.blog.web.dto.CategoryWebRequest;
import com.mandeep.blogify.blog.web.mapper.CategoryMapper;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.domain.exception.CommonException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Categories", description = "Category management APIs")
@SecurityRequirement(name = "bearerAuth")
class CategoryController {

    private final CategoryQueryService queryService;
    private final CategoryCommandService commandService;
    private final CategoryMapper categoryMapper;
    private final AuthFacade authFacade;

    @Operation(summary = "Get paginated categories", description = "Retrieve a paginated list of categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<Response<PaginatedResponse<CategoryResponse>>> getAllCategories(
            @Parameter(description = "Page number to retrieve (starts from 1)", example = "1")
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        int realPageNumber = pageNumber - 1;

        AppUtils.validatePage(realPageNumber, pageSize);

        var response = queryService.getAllCategories(realPageNumber, pageSize);

        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "Create category", description = "Create a new category. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Response<CategoryResponse>> createCategory(
            @Parameter(description = "Category object to create", required = true)
            @RequestBody CategoryWebRequest webRequest
    ) {

        AuthView authView = authFacade.getCurrentUserId().orElseThrow(
                CommonException::accountNotAuthenticated
        );

        CategoryRequest request = categoryMapper.toRequest(webRequest, authView.id());
        UUID id = commandService.createCategory(request);
        CategoryResponse categoryResponse = queryService.getCategoryById(id);
        return new ResponseEntity<>(Response.success(categoryResponse), HttpStatus.CREATED);
    }

    @Operation(summary = "Update category", description = "Update an existing category by ID. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<CategoryResponse>> updateCategory(
            @Parameter(description = "Category object with updated values", required = true)
            @RequestBody @Valid CategoryWebRequest webRequest,

            @Parameter(description = "ID of the category to update", required = true, example = "1")
            @PathVariable UUID id
    ) {

        AuthView authView = authFacade.getCurrentUserId().orElseThrow(
                CommonException::accountNotAuthenticated
        );

        commandService.updateCategory(
                id,
                categoryMapper.toRequest(webRequest, authView.id())
        );

        CategoryResponse updatedCategoryResponse = queryService.getCategoryById(id);
        return ResponseEntity.ok(Response.success(updatedCategoryResponse));
    }

    @Operation(summary = "Delete category", description = "Delete a category by ID. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<?>> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true, example = "1")
            @PathVariable UUID id
    ) {
        AuthView authView = authFacade.getCurrentUserId().orElseThrow(
                CommonException::accountNotAuthenticated
        );
        commandService.deleteCategory(id, authView.id());
        return ResponseEntity.ok(Response.success(null));
    }


}
