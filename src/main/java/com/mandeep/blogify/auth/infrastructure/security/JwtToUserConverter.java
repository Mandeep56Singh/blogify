package com.mandeep.blogify.auth.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Nullable
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {

        UUID id = UUID.fromString(jwt.getSubject());
        String role = jwt.getClaim("role");

        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(
                id,
                null,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                jwt,
                userDetails.getAuthorities()
        );
    }
}
