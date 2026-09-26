package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.TestcontainersConfiguration;
import com.coop.onboarding.application.track.CreateTrackCommand;
import com.coop.onboarding.application.track.CreateTrackUseCase;
import com.coop.onboarding.application.track.GetTrackUseCase;
import com.coop.onboarding.domain.model.Track;
import com.coop.onboarding.infrastructure.security.KeycloakRealmRoleConverter;
import com.coop.onboarding.infrastructure.web.dto.CreateTrackRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TrackSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTrackUseCase createTrackUseCase;

    @MockBean
    private GetTrackUseCase getTrackUseCase;

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar POST /api/v1/tracks")
    void anonymousReceives401OnPostTracks() throws Exception {
        CreateTrackRequest request = new CreateTrackRequest(
                "Trilha Cooperativismo",
                "Fundamentos do modelo cooperativo",
                "Geral",
                20
        );

        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar GET /api/v1/tracks")
    void anonymousReceives401OnGetTracks() throws Exception {
        mockMvc.perform(get("/api/v1/tracks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("Usuário com role COLABORADOR recebe 403 Forbidden ao tentar POST /api/v1/tracks")
    void colaboradorReceives403OnPostTracks() throws Exception {
        CreateTrackRequest request = new CreateTrackRequest(
                "Trilha Cooperativismo",
                "Fundamentos do modelo cooperativo",
                "Geral",
                20
        );

        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Acesso Negado"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Usuário com JWT contendo role COLABORADOR em realm_access recebe 403 Forbidden ao tentar POST /api/v1/tracks")
    void colaboradorWithKeycloakJwtReceives403OnPostTracks() throws Exception {
        CreateTrackRequest request = new CreateTrackRequest(
                "Trilha Cooperativismo",
                "Fundamentos do modelo cooperativo",
                "Geral",
                20
        );

        mockMvc.perform(post("/api/v1/tracks")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt
                                        .claim("realm_access", Map.of("roles", List.of("COLABORADOR")))
                                ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Acesso Negado"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Usuário com role ADMIN recebe 201 Created ao POST /api/v1/tracks")
    void adminReceives201OnPostTracks() throws Exception {
        CreateTrackRequest request = new CreateTrackRequest(
                "Trilha Cooperativismo",
                "Fundamentos do modelo cooperativo",
                "Geral",
                20
        );

        Track createdTrack = Track.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .description(request.description())
                .targetDepartment(request.targetDepartment())
                .estimatedHours(request.estimatedHours())
                .isActive(true)
                .modules(Collections.emptyList())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(createTrackUseCase.execute(any(CreateTrackCommand.class))).thenReturn(createdTrack);

        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdTrack.getId().toString()))
                .andExpect(jsonPath("$.title").value("Trilha Cooperativismo"))
                .andExpect(jsonPath("$.estimatedHours").value(20))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("Usuário com JWT contendo role ADMIN em realm_access recebe 201 Created ao POST /api/v1/tracks")
    void adminWithKeycloakJwtReceives201OnPostTracks() throws Exception {
        CreateTrackRequest request = new CreateTrackRequest(
                "Trilha Crédito e Risco",
                "Formação sobre análise de crédito rural",
                "Crédito",
                30
        );

        Track createdTrack = Track.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .description(request.description())
                .targetDepartment(request.targetDepartment())
                .estimatedHours(request.estimatedHours())
                .isActive(true)
                .modules(Collections.emptyList())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(createTrackUseCase.execute(any(CreateTrackCommand.class))).thenReturn(createdTrack);

        mockMvc.perform(post("/api/v1/tracks")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt
                                        .claim("realm_access", Map.of("roles", List.of("ROLE_ADMIN")))
                                ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Trilha Crédito e Risco"));
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("Usuário autenticado com role COLABORADOR recebe 200 OK ao GET /api/v1/tracks")
    void authenticatedUserReceives200OnGetTracks() throws Exception {
        Track track = Track.builder()
                .id(UUID.randomUUID())
                .title("Trilha Geral")
                .description("Trilha introdutória")
                .targetDepartment("Todos")
                .estimatedHours(10)
                .isActive(true)
                .modules(Collections.emptyList())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(getTrackUseCase.findAll()).thenReturn(List.of(track));

        mockMvc.perform(get("/api/v1/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Trilha Geral"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin enviando payload inválido recebe 400 Bad Request com RFC 7807 ProblemDetail")
    void adminWithInvalidPayloadReceives400ProblemDetail() throws Exception {
        CreateTrackRequest invalidRequest = new CreateTrackRequest(
                "", // Blank title violates @NotBlank
                "Descrição válida",
                "TI",
                -5 // Negative hours violates @PositiveOrZero
        );

        mockMvc.perform(post("/api/v1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.estimatedHours").exists());
    }
}
