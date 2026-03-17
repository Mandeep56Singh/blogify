package com.mandeep.blogify.blog.infrastructure.persistence.adapter;

import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.mapper.CategoryEntityMapper;
import com.mandeep.blogify.blog.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryEntityMapper categoryEntityMapper;

    @Override
    public void save(Category category) {
        CategoryEntity categoryEntity = categoryJpaRepository.findById(category.getCategoryId().value())
                .map(existing -> {
                    existing.setTitle(category.getTitle().value());
                    existing.setDescription(category.getDescription().value());
                    existing.setStatus(category.getCategoryStatus());
                    return existing;
                })
                .orElseGet(() -> categoryEntityMapper.toEntity(category));

        categoryJpaRepository.save(categoryEntity);
    }

    @Override
    public void update(Category category) {
        categoryJpaRepository.findById(category.getCategoryId().value())
                .ifPresent(categoryEntity -> {
                    categoryEntity.setTitle(category.getTitle().value());
                    categoryEntity.setDescription(category.getDescription().value());
                    categoryEntity.setStatus(category.getCategoryStatus());
                });
    }

    @Override
    public Set<CategoryId> findExistingIds(Set<CategoryId> categoryIds) {
        Set<UUID> ids = categoryJpaRepository.findExistingIds(
                categoryIds.stream()
                        .map(CategoryId::value)
                        .collect(Collectors.toSet())
        );

        return ids.stream().map(CategoryId::new).collect(Collectors.toSet());
    }

    @Override
    public boolean existsByTitle(CategoryTitle categoryTitle) {
        return categoryJpaRepository.existsByTitle(categoryTitle.value());
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return categoryJpaRepository.findById(id.value()).map(categoryEntityMapper::toDomain);
    }
}
