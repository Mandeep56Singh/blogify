package com.mandeep.blogify.mapper.user;

import com.mandeep.blogify.dto.user.UserRequestDto;
import com.mandeep.blogify.dto.user.UserResponseDto;
import com.mandeep.blogify.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);
}
