package com.mandeep.blogify.blog.infrastructure.adapter;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.ImageId;
import com.mandeep.blogify.blog.domain.model.valueObject.PostId;
import com.mandeep.blogify.blog.domain.repository.BlogIdGenerator;
import com.mandeep.blogify.shared.infrastructure.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class BlogIdGeneratorAdapter implements BlogIdGenerator {
    @Override
    public PostId nextPostId() {
        return new PostId(IdGenerator.next());
    }

    @Override
    public CategoryId nextCategoryId() {
        return new CategoryId(IdGenerator.next());
    }

    @Override
    public ImageId nextImageId() {
        return new ImageId(IdGenerator.next());
    }
}
