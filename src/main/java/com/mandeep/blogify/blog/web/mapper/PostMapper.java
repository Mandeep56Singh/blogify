package com.mandeep.blogify.blog.web.mapper;

import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.web.dto.PostWebRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    PostRequest toRequest(PostWebRequest webRequest, UUID authorId);

}
