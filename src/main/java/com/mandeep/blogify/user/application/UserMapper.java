package com.mandeep.blogify.user.application;

import com.mandeep.blogify.user.application.dto.UserRequestDto;
import com.mandeep.blogify.user.application.dto.UserResponseDto;
import com.mandeep.blogify.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);
}
