package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.TestcontainersConfiguration;
import com.coop.onboarding.application.user.service.AdminUserService;
import com.coop.onboarding.domain.model.UserRole;
import com.coop.onboarding.infrastructure.security.KeycloakRealmRoleConverter;
import com.coop.onboarding.infrastructure.web.dto.AdminUserSummaryResponse;
import com.coop.onboarding.infrastructure.web.dto.CreateUserRequest;
import com.coop.onboarding.infrastructure.web.dto.UpdateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AdminUserControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminUserService adminUserService;

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar GET /api/v1/admin/users")
    void anonymousReceives401OnGetAdminUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Colaborador recebe 403 Forbidden ao tentar listar ou criar usuários")
    void colaboradorReceives403OnAdminRoutes() throws Exception {
        CreateUserRequest req = new CreateUserRequest(
                "Teste",
                "teste@coop.local",
                UserRole.COLABORADOR,
                "Atendimento",
                "Analista",
                "Pass@123",
                null,
                null
        );

        mockMvc.perform(get("/api/v1/admin/users")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR"))))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Gestor pode listar membros, mas recebe 403 ao tentar criar usuário")
    void gestorCanListButCannotCreate() throws Exception {
        when(adminUserService.listAllMembers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/admin/users")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("GESTOR"))))))
                .andExpect(status().isOk());

        CreateUserRequest req = new CreateUserRequest(
                "Teste",
                "teste@coop.local",
                UserRole.COLABORADOR,
                "Atendimento",
                "Analista",
                "Pass@123",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("GESTOR"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Administrador (ROLE_ADMIN) cria usuário com sucesso e recebe 201 Created")
    void adminCanCreateUser() throws Exception {
        UUID newId = UUID.randomUUID();
        AdminUserSummaryResponse res = new AdminUserSummaryResponse(
                newId,
                "kc-" + newId,
                "Novo Usuário",
                "novo@coop.local",
                UserRole.COLABORADOR,
                "Atendimento",
                "Assistente",
                null,
                null,
                "IN_PROGRESS",
                0,
                6,
                0,
                true,
                OffsetDateTime.now()
        );

        when(adminUserService.createMember(any(CreateUserRequest.class))).thenReturn(res);

        CreateUserRequest req = new CreateUserRequest(
                "Novo Usuário",
                "novo@coop.local",
                UserRole.COLABORADOR,
                "Atendimento",
                "Assistente",
                "Pass@123",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newId.toString()))
                .andExpect(jsonPath("$.name").value("Novo Usuário"))
                .andExpect(jsonPath("$.role").value("COLABORADOR"));
    }

    @Test
    @DisplayName("Administrador (ROLE_ADMIN) atualiza usuário com sucesso e recebe 200 OK")
    void adminCanUpdateUser() throws Exception {
        UUID userId = UUID.randomUUID();
        AdminUserSummaryResponse updated = new AdminUserSummaryResponse(
                userId,
                "kc-" + userId,
                "Usuário Atualizado",
                "atualizado@coop.local",
                UserRole.GESTOR,
                "DHO",
                "Coordenador",
                null,
                null,
                "COMPLETED",
                6,
                6,
                100,
                true,
                OffsetDateTime.now()
        );

        when(adminUserService.updateMember(any(UUID.class), any(UpdateUserRequest.class))).thenReturn(updated);

        UpdateUserRequest req = new UpdateUserRequest(
                "Usuário Atualizado",
                "atualizado@coop.local",
                UserRole.GESTOR,
                "DHO",
                "Coordenador",
                null,
                null,
                null,
                true
        );

        mockMvc.perform(put("/api/v1/admin/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuário Atualizado"))
                .andExpect(jsonPath("$.role").value("GESTOR"));
    }

    @Test
    @DisplayName("Administrador (ROLE_ADMIN) exclui usuário com sucesso e recebe 204 No Content")
    void adminCanDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/users/" + userId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("ADMIN"))))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Gestor e Colaborador recebem 403 Forbidden ao tentar excluir usuário")
    void nonAdminCannotDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/users/" + userId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("GESTOR"))))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/admin/users/" + userId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR"))))))
                .andExpect(status().isForbidden());
    }
}
