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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
class CategoryController {

    private final CategoryService categoryService;
    private final RequestValidator validator;

    @GetMapping
    public ResponseEntity<ResponseDto<PaginatedResponseDto<CategoryResponseDto>>> getAllCategories(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
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


    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> createCategory(
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

    @PostMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<CategoryResponseDto>> updateCategory(
            @RequestBody CategoryRequestDto requestDto,
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ResponsePayload>> deleteCategory(
            @PathVariable Long id) {
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
