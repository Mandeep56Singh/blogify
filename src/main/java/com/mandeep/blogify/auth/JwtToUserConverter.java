package com.mandeep.blogify.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Nullable
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {

        Long userId = jwt.getClaim("id");
        String email = jwt.getSubject();
        String name = jwt.getClaim("name");
        String role = jwt.getClaim("role");

        AuthUser userDetails = new AuthUser(
                userId,
                email,
                "",
                name,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                jwt,
                userDetails.getAuthorities()
        );
    }
}
