package com.mandeep.blogify.blog.infrastructure.persistence.repository;

import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, UUID> {

    boolean existsBySlug(String slug);


    @Query("SELECT p.slug FROM PostEntity p WHERE p.slug LIKE :pattern")
    Set<String> findSlugsByPrefix(@Param("pattern") String pattern);

    @Query(
            value = "SELECT p.id FROM PostEntity p WHERE p.status = :status",
            countQuery = "SELECT COUNT(p) FROM PostEntity p WHERE p.status = :status"
    )
    Page<UUID> findIdsOfAllPublished(
            Pageable pageable,
            @Param("status") PostStatus status
    );

    @QueryHints(
            @QueryHint(name = "hibernate.query.passDistinctThrough", value = "false")
    )
    @Query("""
                SELECT DISTINCT p FROM PostEntity p
                LEFT JOIN FETCH p.categories
                WHERE p.id IN :ids
            """)
    List<PostEntity> findPostsWithCategoriesByIds(@Param("ids") List<UUID> ids);

}
