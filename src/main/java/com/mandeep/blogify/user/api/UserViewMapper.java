package com.mandeep.blogify.user.api;

import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UserViewMapper {
    UserView toView(UserResponse userResponse);
}
