package com.mandeep.blogify.blog.domain.repository;

import com.mandeep.blogify.blog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByTitle(String title);

    Optional<Category> findByTitle(String title);
}
