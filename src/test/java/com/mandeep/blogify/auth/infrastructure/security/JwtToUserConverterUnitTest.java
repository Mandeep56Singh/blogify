package com.mandeep.blogify.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtToUserConverter")
class JwtToUserConverterUnitTest {

    private final JwtToUserConverter converter = new JwtToUserConverter();

    private Jwt buildJwt(UUID subject, String role) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(subject.toString())
                .claim("role", role)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Nested
    @DisplayName("convert()")
    class Convert {

        @Test
        @DisplayName("Extracts subject UUID as user id")
        void should_ExtractUserId_From_JwtSubject() {
            UUID id = UUID.randomUUID();
            Jwt jwt = buildJwt(id, "USER");

            UsernamePasswordAuthenticationToken token = converter.convert(jwt);

            AuthenticatedUserDetails details = (AuthenticatedUserDetails) token.getPrincipal();
            assertThat(details.id()).isEqualTo(id);
        }

        @Test
        @DisplayName("Prefixes role claim with ROLE_")
        void should_PrefixRole_When_Converting() {
            Jwt jwt = buildJwt(UUID.randomUUID(), "ADMIN");

            UsernamePasswordAuthenticationToken token = converter.convert(jwt);

            assertThat(token.getAuthorities())
                    .extracting(Object::toString)
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("Sets JWT as credentials")
        void should_SetJwt_AsCredentials() {
            Jwt jwt = buildJwt(UUID.randomUUID(), "USER");

            UsernamePasswordAuthenticationToken token = converter.convert(jwt);

            assertThat(token.getCredentials()).isEqualTo(jwt);
        }

        @Test
        @DisplayName("Username and password are null — not needed for JWT auth")
        void should_HaveNullUsernameAndPassword() {
            Jwt jwt = buildJwt(UUID.randomUUID(), "USER");

            UsernamePasswordAuthenticationToken token = converter.convert(jwt);

            AuthenticatedUserDetails details = (AuthenticatedUserDetails) token.getPrincipal();
            assertThat(details.getUsername()).isNull();
            assertThat(details.getPassword()).isNull();
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when subject is not a valid UUID")
        void should_ThrowException_When_SubjectIsNotUuid() {
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .subject("not-a-uuid") // <--- Bad subject
                    .build();

            assertThatThrownBy(() -> converter.convert(jwt))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Handles missing role claim gracefully (or as expected)")
        void should_HandleMissingRole() {
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .subject(UUID.randomUUID().toString())
                    // Missing .claim("role", ...)
                    .build();

            UsernamePasswordAuthenticationToken token = converter.convert(jwt);

            // This confirms your current behavior: "ROLE_null"
            assertThat(token.getAuthorities())
                    .extracting(Object::toString)
                    .containsExactly("ROLE_null");
        }
    }
}