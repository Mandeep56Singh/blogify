package com.mandeep.blogify.blog.domain.repository;

import com.mandeep.blogify.blog.domain.model.entity.Image;

public interface ImageRepository {
    void save(Image image);
}
