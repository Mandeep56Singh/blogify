package com.mandeep.blogify.auth.application.mapping;

import com.mandeep.blogify.auth.application.dto.AuthUserDto;
import com.mandeep.blogify.auth.domain.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "role", source = "authorities", qualifiedByName = "extractRole")
    AuthUserDto toDto(AuthUser user);

    @Named(value = "extractRole")
    default String extractRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.joining(","));

    }
}
