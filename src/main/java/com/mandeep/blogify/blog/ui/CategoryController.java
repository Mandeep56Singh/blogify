package com.mandeep.blogify.blog.ui;

import com.mandeep.blogify.blog.application.dto.request.CategoryRequestDto;
import com.mandeep.blogify.blog.application.dto.response.CategoryResponseDto;
import com.mandeep.blogify.blog.application.service.CategoryService;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.dto.ResponsePayload;
import com.mandeep.blogify.shared.exceptions.AppError;
import com.mandeep.blogify.shared.exceptions.AppProblem;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
@SecurityRequirement(name = "bearerAuth")
class CategoryController {

    private final CategoryService categoryService;
    private final RequestValidator validator;

    @Operation(summary = "Get paginated categories", description = "Retrieve a paginated list of categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<ResponseDto<PaginatedResponseDto<CategoryResponseDto>>> getAllCategories(
            @Parameter(description = "Page number to retrieve (starts from 1)", example = "1")
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        Optional<PageError> pageError = AppUtils.validatePage(pageNumber - 1, pageSize);

        if (pageError.isPresent()) {
            return new ResponseEntity<>(ResponseDto.failure(pageError.get()), pageError.get().status());
        }


        ResponseDto<PaginatedResponseDto<CategoryResponseDto>> responseDto =
                categoryService.getAllCategories(pageNumber, pageSize);

        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Create category", description = "Create a new category. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> createCategory(
            @Parameter(description = "Category object to create", required = true)
            @RequestBody CategoryRequestDto requestDto
    ) {

        Optional<ResponseDto<CategoryResponseDto>> validationError = validator.validate(requestDto);
        if (validationError.isPresent()) {
            return new ResponseEntity<>(validationError.get(), validationError.get().error().status());
        }

        ResponseDto<CategoryResponseDto> responseDto = categoryService.createCategory(requestDto);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update category", description = "Update an existing category by ID. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<CategoryResponseDto>> updateCategory(
            @Parameter(description = "Category object with updated values", required = true)
            @RequestBody CategoryRequestDto requestDto,

            @Parameter(description = "ID of the category to update", required = true, example = "1")
            @PathVariable Long id
    ) {

        Optional<ResponseDto<CategoryResponseDto>> validationError = validator.validate(requestDto);
        if (validationError.isPresent()) {
            return new ResponseEntity<>(validationError.get(), validationError.get().error().status());
        }

        ResponseDto<CategoryResponseDto> responseDto = categoryService.updateCategory(requestDto, id);

        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete category", description = "Delete a category by ID. **Requires ADMIN role**")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ResponsePayload>> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true, example = "1")
            @PathVariable Long id
    ) {
        Optional<AppError> error = categoryService.deleteCategory(id);

        if (error.isPresent()) {
            AppError detail = AppProblem.getDetail(error.get());
            return ResponseEntity
                    .status(detail.status())
                    .body(ResponseDto.failure(detail));
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
