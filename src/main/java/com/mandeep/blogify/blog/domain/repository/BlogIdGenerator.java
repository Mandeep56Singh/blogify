package com.mandeep.blogify.blog.domain.repository;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.ImageId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;

public interface BlogIdGenerator {
    PostId nextPostId();

    CategoryId nextCategoryId();

    ImageId nextImageId();
}
