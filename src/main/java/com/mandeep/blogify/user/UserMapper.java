package com.mandeep.blogify.user;

import com.mandeep.blogify.user.dto.UserRequestDto;
import com.mandeep.blogify.user.dto.UserResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);
}
