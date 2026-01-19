package com.mandeep.blogify.category;

import com.mandeep.blogify.common.PaginatedResponseDto;
import com.mandeep.blogify.category.dto.CategoryRequestDto;
import com.mandeep.blogify.category.dto.CategoryResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> getAllCategories(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        PaginatedResponseDto<CategoryResponseDto> responseDto = categoryService.getAllCategories(pageNumber, pageSize);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestBody CategoryRequestDto requestDto
    ) {
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Valid @RequestBody CategoryRequestDto requestDto,
            @PathVariable @NotNull Long id
    ) {
        CategoryResponseDto responseDto = categoryService.updateCategory(requestDto, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable @NotNull Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("category deleted", HttpStatus.NOT_FOUND);
    }


}
