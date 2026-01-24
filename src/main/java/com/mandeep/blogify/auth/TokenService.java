package com.mandeep.blogify.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final RsaKeyProperties rsaKeys;

    public TokenService(JwtEncoder encoder, JwtDecoder decoder, RsaKeyProperties rsaKeys) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.rsaKeys = rsaKeys;
    }

    public String generateToken(Authentication authentication) {
        AuthUser user = (AuthUser) authentication.getPrincipal();

        String role = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(userRole -> userRole.replace("ROLE_", ""))
                .collect(Collectors.joining(","));

        Long id = user.id();
        String name = user.name();
        Instant now = Instant.now();

        Instant expireAt = now.plus(rsaKeys.tokenExpireIn());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("blogify-api")
                .issuedAt(now)
                .subject(user.getUsername())
                .claim("id", id)
                .claim("name", name)
                .claim("role", role)
                .expiresAt(expireAt)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
