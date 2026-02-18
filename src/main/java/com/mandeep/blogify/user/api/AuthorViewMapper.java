package com.mandeep.blogify.user.api;

import com.mandeep.blogify.shared.dto.AuthorView;
import com.mandeep.blogify.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorViewMapper {
    AuthorView toView(User user);
}
