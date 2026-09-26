package com.coop.onboarding.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    @DisplayName("Deve extrair roles de realm_access.roles e adicionar prefixo ROLE_ se ausente")
    void shouldExtractAndPrefixRoles() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("realm_access", Map.of("roles", List.of("ADMIN", "COLABORADOR")))
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_COLABORADOR");
    }

    @Test
    @DisplayName("Deve manter roles que já possuem prefixo ROLE_")
    void shouldPreserveExistingRolePrefix() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("realm_access", Map.of("roles", List.of("ROLE_ADMIN", "ROLE_GESTOR")))
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_GESTOR");
    }

    @Test
    @DisplayName("Deve retornar coleção vazia se realm_access ou roles estiver ausente")
    void shouldReturnEmptyWhenNoRoles() {
        Jwt jwtWithoutRealmAccess = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "123")
        );

        Collection<GrantedAuthority> authorities = converter.convert(jwtWithoutRealmAccess);

        assertThat(authorities).isEmpty();
    }
}
