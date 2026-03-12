package com.mandeep.blogify.auth.infrastructure.adapter;

import com.mandeep.blogify.auth.application.command.TokenProvider;
import com.mandeep.blogify.auth.application.dto.TokenInfo;
import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.infrastructure.security.RsaKeyProperties;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final RsaKeyProperties rsaKeyProperties;

    @Override
    public TokenInfo generateToken(AuthUserId id, Role role) {

        Instant now = Instant.now();
        Instant expiry = now.plus(rsaKeyProperties.tokenExpireIn());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("blogify-api")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(id.value().toString())
                .claim("role", role.name())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenInfo(token, rsaKeyProperties.tokenExpireIn().getSeconds());
    }
}
