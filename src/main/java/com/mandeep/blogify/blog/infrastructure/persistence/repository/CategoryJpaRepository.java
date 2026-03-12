package com.mandeep.blogify.blog.infrastructure.persistence.repository;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    boolean existsByTitle(String title);

    @Query("select c.id from CategoryEntity c where c.id in :ids")
    Set<UUID> findExistingIds(@Param("ids") Set<UUID> ids);

    @Query("""
                    SELECT new com.mandeep.blogify.blog.application.dto.CategoryResponse(
                    c.id,
                    c.title
                    ) FROM CategoryEntity c WHERE c.id = :id AND c.status = 'ACTIVE'
            """)
    Optional<CategoryResponse> findCategoryById(@Param("id") UUID id);


    @Query("""
                SELECT new com.mandeep.blogify.blog.application.dto.CategoryResponse(
                    c.id,
                    c.title
                )
                FROM CategoryEntity c
                WHERE c.status = 'ACTIVE'
            """)
    Page<CategoryResponse> findAllActiveCategories(Pageable pageable);


}
