package com.mandeep.blogify.auth.api;

import com.mandeep.blogify.auth.AuthenticatedUserView;
import com.mandeep.blogify.auth.domain.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthenticatedUserMapper {
    AuthenticatedUserView toUser(AuthUser user);
}
