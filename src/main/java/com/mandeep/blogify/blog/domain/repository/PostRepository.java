package com.mandeep.blogify.blog.domain.repository;

import com.mandeep.blogify.blog.domain.model.entity.Post;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;

import java.util.Optional;
import java.util.Set;

public interface PostRepository {
    void save(Post post);
    Set<String> findSlugsByPrefix(String slugPrefix);
    Optional<Post> findById(PostId postId);
}
