package com.coop.onboarding.infrastructure.iam;

import com.coop.onboarding.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.*;

@Service
public class KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final String serverUrl;
    private final String realm;
    private final String adminUsername;
    private final String adminPassword;
    private final RestClient restClient;

    private String cachedToken;
    private Instant tokenExpiresAt = Instant.MIN;

    public KeycloakAdminService(
            @Value("${coop.keycloak.server-url:#{null}}") String explicitServerUrl,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:#{null}}") String issuerUri,
            @Value("${coop.keycloak.realm:coop-onboarding}") String realm,
            @Value("${coop.keycloak.admin-username:admin}") String adminUsername,
            @Value("${coop.keycloak.admin-password:admin}") String adminPassword
    ) {
        String resolvedUrl = explicitServerUrl;
        if (resolvedUrl == null || resolvedUrl.isBlank() || "http://localhost:8180".equals(resolvedUrl)) {
            if (issuerUri != null && !issuerUri.isBlank()) {
                int realmsIdx = issuerUri.indexOf("/realms");
                if (realmsIdx != -1) {
                    resolvedUrl = issuerUri.substring(0, realmsIdx);
                }
            }
        }
        if (resolvedUrl == null || resolvedUrl.isBlank()) {
            resolvedUrl = "http://localhost:8180";
        }

        this.serverUrl = resolvedUrl.endsWith("/") ? resolvedUrl.substring(0, resolvedUrl.length() - 1) : resolvedUrl;
        this.realm = realm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.restClient = RestClient.builder().build();
        log.info("KeycloakAdminService inicializado. ServerUrl: {}, Realm: {}", this.serverUrl, this.realm);
    }

    private synchronized String getAdminAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return cachedToken;
        }

        String tokenUrl = serverUrl + "/realms/master/protocol/openid-connect/token";
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "admin-cli");
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);
        formData.add("grant_type", "password");

        try {
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null && response.containsKey("access_token")) {
                this.cachedToken = (String) response.get("access_token");
                int expiresIn = response.containsKey("expires_in") ? (Integer) response.get("expires_in") : 60;
                this.tokenExpiresAt = Instant.now().plusSeconds(Math.max(10, expiresIn - 15));
                return this.cachedToken;
            }
        } catch (Exception e) {
            log.error("Falha ao obter token administrativo do Keycloak em {}", tokenUrl, e);
            throw new IllegalStateException("Não foi possível autenticar como Administrador no Keycloak: " + e.getMessage(), e);
        }

        throw new IllegalStateException("Resposta inesperada do Keycloak ao solicitar token admin");
    }

    public String createKeycloakUser(String username, String email, String fullName, String password, UserRole role) {
        String token = getAdminAccessToken();
        String usersUrl = serverUrl + "/admin/realms/" + realm + "/users";

        String[] nameParts = splitFullName(fullName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", username);
        userPayload.put("email", email);
        userPayload.put("firstName", firstName);
        userPayload.put("lastName", lastName);
        userPayload.put("enabled", true);
        userPayload.put("emailVerified", true);

        if (password != null && !password.isBlank()) {
            Map<String, Object> cred = new HashMap<>();
            cred.put("type", "password");
            cred.put("value", password);
            cred.put("temporary", false);
            userPayload.put("credentials", List.of(cred));
        }

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(usersUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userPayload)
                    .retrieve()
                    .toBodilessEntity();

            String keycloakId = null;
            if (response.getHeaders().getLocation() != null) {
                String path = response.getHeaders().getLocation().getPath();
                keycloakId = path.substring(path.lastIndexOf('/') + 1);
            }

            if (keycloakId == null || keycloakId.isBlank()) {
                keycloakId = findKeycloakUserIdByUsername(username);
            }

            if (keycloakId != null) {
                assignRealmRole(keycloakId, role);
            }

            log.info("Usuário Keycloak criado com sucesso. Username: {}, ID: {}", username, keycloakId);
            return keycloakId;
        } catch (Exception e) {
            log.error("Erro ao criar usuário no Keycloak: {}", username, e);
            throw new IllegalStateException("Falha ao criar usuário no Keycloak: " + e.getMessage(), e);
        }
    }

    public void updateKeycloakUser(String keycloakId, String email, String fullName, UserRole role, String newPassword, Boolean enabled) {
        String token = getAdminAccessToken();
        String userUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId;

        String[] nameParts = splitFullName(fullName);
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("email", email);
        userPayload.put("firstName", nameParts[0]);
        userPayload.put("lastName", nameParts[1]);
        if (enabled != null) {
            userPayload.put("enabled", enabled);
        }

        try {
            restClient.put()
                    .uri(userUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userPayload)
                    .retrieve()
                    .toBodilessEntity();

            if (newPassword != null && !newPassword.isBlank()) {
                String resetPasswordUrl = userUrl + "/reset-password";
                Map<String, Object> cred = new HashMap<>();
                cred.put("type", "password");
                cred.put("value", newPassword);
                cred.put("temporary", false);

                restClient.put()
                        .uri(resetPasswordUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(cred)
                        .retrieve()
                        .toBodilessEntity();
            }

            if (role != null) {
                assignRealmRole(keycloakId, role);
            }

            log.info("Usuário Keycloak {} atualizado com sucesso.", keycloakId);
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário Keycloak {}", keycloakId, e);
            throw new IllegalStateException("Falha ao atualizar usuário no Keycloak: " + e.getMessage(), e);
        }
    }

    public void deleteKeycloakUser(String keycloakId) {
        String token = getAdminAccessToken();
        String userUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId;

        try {
            restClient.delete()
                    .uri(userUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Usuário Keycloak {} excluído com sucesso.", keycloakId);
        } catch (Exception e) {
            log.warn("Erro ao excluir usuário Keycloak {}: {}", keycloakId, e.getMessage());
        }
    }

    public void assignRealmRole(String keycloakId, UserRole role) {
        String token = getAdminAccessToken();
        String targetRoleName = "ROLE_" + role.name();

        try {
            // 1. Busca a role no realm
            Map<String, Object> roleObj = null;
            try {
                String roleUrl = serverUrl + "/admin/realms/" + realm + "/roles/" + targetRoleName;
                roleObj = restClient.get()
                        .uri(roleUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});
            } catch (Exception e) {
                try {
                    String altUrl = serverUrl + "/admin/realms/" + realm + "/roles/" + role.name();
                    roleObj = restClient.get()
                            .uri(altUrl)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});
                    if (roleObj != null) {
                        targetRoleName = role.name();
                    }
                } catch (Exception ignored) {}
            }

            if (roleObj != null) {
                // 2. Remove roles prévias conflitantes
                List<String> knownRoles = List.of("ROLE_COLABORADOR", "ROLE_GESTOR", "ROLE_ADMIN");
                for (String rName : knownRoles) {
                    if (!rName.equalsIgnoreCase(targetRoleName)) {
                        try {
                            String rUrl = serverUrl + "/admin/realms/" + realm + "/roles/" + rName;
                            Map<String, Object> rObj = restClient.get()
                                    .uri(rUrl)
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .retrieve()
                                    .body(new ParameterizedTypeReference<>() {});
                            if (rObj != null) {
                                String userRoleMappingUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId + "/role-mappings/realm";
                                restClient.method(org.springframework.http.HttpMethod.DELETE)
                                        .uri(userRoleMappingUrl)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(List.of(rObj))
                                        .retrieve()
                                        .toBodilessEntity();
                            }
                        } catch (Exception ignored) {}
                    }
                }

                // 3. Adiciona a nova role
                String addRoleUrl = serverUrl + "/admin/realms/" + realm + "/users/" + keycloakId + "/role-mappings/realm";
                restClient.post()
                        .uri(addRoleUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(List.of(roleObj))
                        .retrieve()
                        .toBodilessEntity();

                log.info("Role {} atribuída com sucesso ao usuário {}", targetRoleName, keycloakId);
            }
        } catch (Exception e) {
            log.error("Erro ao atribuir role {} ao usuário {}", targetRoleName, keycloakId, e);
        }
    }

    private String findKeycloakUserIdByUsername(String username) {
        String token = getAdminAccessToken();
        String searchUrl = serverUrl + "/admin/realms/" + realm + "/users?username=" + username + "&exact=true";
        try {
            List<Map<String, Object>> users = restClient.get()
                    .uri(searchUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (users != null && !users.isEmpty()) {
                return (String) users.get(0).get("id");
            }
        } catch (Exception e) {
            log.warn("Erro ao buscar Keycloak ID por username {}", username, e);
        }
        return null;
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Usuário", ""};
        }
        String trimmed = fullName.trim();
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace == -1) {
            return new String[]{trimmed, ""};
        }
        return new String[]{trimmed.substring(0, firstSpace), trimmed.substring(firstSpace + 1).trim()};
    }
}
