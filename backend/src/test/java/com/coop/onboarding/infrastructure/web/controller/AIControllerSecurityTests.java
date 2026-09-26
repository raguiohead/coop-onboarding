package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.TestcontainersConfiguration;
import com.coop.onboarding.application.ai.AskTutorQuery;
import com.coop.onboarding.application.ai.AskTutorUseCase;
import com.coop.onboarding.application.ai.GenerateQuizCommand;
import com.coop.onboarding.application.ai.GenerateQuizUseCase;
import com.coop.onboarding.application.ai.IngestLessonCommand;
import com.coop.onboarding.application.ai.IngestLessonUseCase;
import com.coop.onboarding.domain.ai.GeneratedQuiz;
import com.coop.onboarding.domain.ai.QuizQuestion;
import com.coop.onboarding.domain.ai.TutorAnswer;
import com.coop.onboarding.infrastructure.security.KeycloakRealmRoleConverter;
import com.coop.onboarding.infrastructure.web.dto.ai.AskTutorRequest;
import com.coop.onboarding.infrastructure.web.dto.ai.GenerateQuizRequest;
import com.coop.onboarding.infrastructure.web.dto.ai.IngestLessonRequest;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AIControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngestLessonUseCase ingestLessonUseCase;

    @MockBean
    private AskTutorUseCase askTutorUseCase;

    @MockBean
    private GenerateQuizUseCase generateQuizUseCase;

    // =========================================================================
    // 1. Cenários Anônimos (401 Unauthorized)
    // =========================================================================

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar POST /api/v1/ai/lessons/{lessonId}/ingest")
    void anonymousReceives401OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Princípios do Cooperativismo",
                "# Conteúdo detalhado sobre cooperativismo",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar POST /api/v1/ai/tutor/ask")
    void anonymousReceives401OnPostAskTutor() throws Exception {
        AskTutorRequest request = new AskTutorRequest(
                UUID.randomUUID(),
                "O que significa o princípio de adesão voluntária e livre?"
        );

        mockMvc.perform(post("/api/v1/ai/tutor/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar POST /api/v1/ai/quiz/generate")
    void anonymousReceives401OnPostGenerateQuiz() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                UUID.randomUUID(),
                "# Conteúdo para gerar questões",
                3
        );

        mockMvc.perform(post("/api/v1/ai/quiz/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // 2. Cenários com COLABORADOR (403 Forbidden em Ingest e Generate)
    // =========================================================================

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("COLABORADOR recebe 403 Forbidden ao tentar POST /api/v1/ai/lessons/{lessonId}/ingest")
    void colaboradorReceives403OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Introdução ao Cooperativismo",
                "# Conteúdo da aula",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("COLABORADOR com Keycloak JWT recebe 403 Forbidden ao tentar POST /api/v1/ai/lessons/{lessonId}/ingest")
    void colaboradorWithJwtReceives403OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Introdução ao Cooperativismo",
                "# Conteúdo da aula",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("COLABORADOR recebe 403 Forbidden ao tentar POST /api/v1/ai/quiz/generate")
    void colaboradorReceives403OnPostGenerateQuiz() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                UUID.randomUUID(),
                "# Conteúdo para quiz",
                3
        );

        mockMvc.perform(post("/api/v1/ai/quiz/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("COLABORADOR com Keycloak JWT recebe 403 Forbidden ao tentar POST /api/v1/ai/quiz/generate")
    void colaboradorWithJwtReceives403OnPostGenerateQuiz() throws Exception {
        GenerateQuizRequest request = new GenerateQuizRequest(
                UUID.randomUUID(),
                "# Conteúdo para quiz",
                3
        );

        mockMvc.perform(post("/api/v1/ai/quiz/generate")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    // =========================================================================
    // 3. Cenários com COLABORADOR chamando /tutor/ask (200 OK)
    // =========================================================================

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("COLABORADOR recebe 200 OK ao POST /api/v1/ai/tutor/ask")
    void colaboradorReceives200OnPostAskTutor() throws Exception {
        UUID lessonId = UUID.randomUUID();
        AskTutorRequest request = new AskTutorRequest(
                lessonId,
                "Como funciona a gestão democrática em uma cooperativa?"
        );

        TutorAnswer mockAnswer = TutorAnswer.builder()
                .lessonId(lessonId)
                .answer("Na gestão democrática cooperativa, cada cooperado tem direito a um voto.")
                .sources(List.of("Princípios do Cooperativismo (lesson:" + lessonId + ")"))
                .build();

        when(askTutorUseCase.execute(any(AskTutorQuery.class))).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/v1/ai/tutor/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.answer").value("Na gestão democrática cooperativa, cada cooperado tem direito a um voto."))
                .andExpect(jsonPath("$.sources[0]").value("Princípios do Cooperativismo (lesson:" + lessonId + ")"));
    }

    @Test
    @DisplayName("COLABORADOR com Keycloak JWT recebe 200 OK ao POST /api/v1/ai/tutor/ask")
    void colaboradorWithJwtReceives200OnPostAskTutor() throws Exception {
        UUID lessonId = UUID.randomUUID();
        AskTutorRequest request = new AskTutorRequest(
                lessonId,
                "Qual a função do conselho fiscal?"
        );

        TutorAnswer mockAnswer = TutorAnswer.builder()
                .lessonId(lessonId)
                .answer("O conselho fiscal fiscaliza as contas e operações da cooperativa.")
                .sources(List.of("Governança Cooperativa"))
                .build();

        when(askTutorUseCase.execute(any(AskTutorQuery.class))).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/v1/ai/tutor/ask")
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("COLABORADOR")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.answer").value("O conselho fiscal fiscaliza as contas e operações da cooperativa."));
    }

    // =========================================================================
    // 4. Cenários com ADMIN e GESTOR (200 OK em Ingest e Generate)
    // =========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN recebe 200 OK ao POST /api/v1/ai/lessons/{lessonId}/ingest")
    void adminReceives200OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Estrutura Societária Cooperativa",
                "# Visão geral sobre o estatuto e regimento interno.",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        doNothing().when(ingestLessonUseCase).execute(any(IngestLessonCommand.class));

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.message").value("Conteúdo da aula indexado com sucesso"));

        verify(ingestLessonUseCase).execute(any(IngestLessonCommand.class));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("GESTOR recebe 200 OK ao POST /api/v1/ai/lessons/{lessonId}/ingest")
    void gestorReceives200OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Cultura e Valores Cooperativos",
                "# Valores essenciais do modelo cooperativo.",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        doNothing().when(ingestLessonUseCase).execute(any(IngestLessonCommand.class));

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.message").value("Conteúdo da aula indexado com sucesso"));

        verify(ingestLessonUseCase).execute(any(IngestLessonCommand.class));
    }

    @Test
    @DisplayName("ADMIN com Keycloak JWT recebe 200 OK ao POST /api/v1/ai/lessons/{lessonId}/ingest")
    void adminWithJwtReceives200OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Assembleias Cooperativas",
                "# Guia sobre assembleias gerais ordinárias.",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        doNothing().when(ingestLessonUseCase).execute(any(IngestLessonCommand.class));

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()));
    }

    @Test
    @DisplayName("GESTOR com Keycloak JWT recebe 200 OK ao POST /api/v1/ai/lessons/{lessonId}/ingest")
    void gestorWithJwtReceives200OnPostIngest() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest request = new IngestLessonRequest(
                "Intercooperação na Prática",
                "# O princípio da intercooperação.",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        doNothing().when(ingestLessonUseCase).execute(any(IngestLessonCommand.class));

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .with(jwt()
                                .authorities(new KeycloakRealmRoleConverter())
                                .jwt(jwt -> jwt.claim("realm_access", Map.of("roles", List.of("ROLE_GESTOR")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN recebe 200 OK ao POST /api/v1/ai/quiz/generate")
    void adminReceives200OnPostGenerateQuiz() throws Exception {
        UUID lessonId = UUID.randomUUID();
        GenerateQuizRequest request = new GenerateQuizRequest(
                lessonId,
                "# Conteúdo para gerar questões sobre finanças cooperativas.",
                2
        );

        QuizQuestion q1 = QuizQuestion.builder()
                .question("Qual a destinação do Fundo de Reserva?")
                .options(List.of("Cobrir eventuais perdas", "Distribuir aos gestores", "Pagar impostos", "Investir em bolsas de valores"))
                .correctAnswer("Cobrir eventuais perdas")
                .explanation("O Fundo de Reserva tem destinação legal específica para cobrir perdas e manter a estabilidade.")
                .build();

        GeneratedQuiz mockQuiz = GeneratedQuiz.builder()
                .lessonId(lessonId)
                .questions(List.of(q1))
                .build();

        when(generateQuizUseCase.execute(any(GenerateQuizCommand.class))).thenReturn(mockQuiz);

        mockMvc.perform(post("/api/v1/ai/quiz/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.questions[0].question").value("Qual a destinação do Fundo de Reserva?"))
                .andExpect(jsonPath("$.questions[0].correctAnswer").value("Cobrir eventuais perdas"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @DisplayName("GESTOR recebe 200 OK ao POST /api/v1/ai/quiz/generate")
    void gestorReceives200OnPostGenerateQuiz() throws Exception {
        UUID lessonId = UUID.randomUUID();
        GenerateQuizRequest request = new GenerateQuizRequest(
                lessonId,
                "# Conteúdo de governança",
                1
        );

        QuizQuestion q1 = QuizQuestion.builder()
                .question("Quantos votos tem cada associado na cooperativa?")
                .options(List.of("1 voto", "Proporcional ao capital", "Depende do cargo", "2 votos"))
                .correctAnswer("1 voto")
                .explanation("No cooperativismo vigora o princípio 'um cooperado, um voto'.")
                .build();

        GeneratedQuiz mockQuiz = GeneratedQuiz.builder()
                .lessonId(lessonId)
                .questions(List.of(q1))
                .build();

        when(generateQuizUseCase.execute(any(GenerateQuizCommand.class))).thenReturn(mockQuiz);

        mockMvc.perform(post("/api/v1/ai/quiz/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.questions[0].question").value("Quantos votos tem cada associado na cooperativa?"));
    }

    // =========================================================================
    // 5. Validação de Payload (400 Bad Request)
    // =========================================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Payload inválido em POST /ingest retorna 400 Bad Request com ProblemDetail")
    void adminWithInvalidIngestPayloadReceives400() throws Exception {
        UUID lessonId = UUID.randomUUID();
        IngestLessonRequest invalidRequest = new IngestLessonRequest(
                "", // Blank title
                "", // Blank contentMarkdown
                null,
                null
        );

        mockMvc.perform(post("/api/v1/ai/lessons/{lessonId}/ingest", lessonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.contentMarkdown").exists());
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    @DisplayName("Payload inválido em POST /tutor/ask retorna 400 Bad Request com ProblemDetail")
    void authenticatedWithInvalidAskPayloadReceives400() throws Exception {
        AskTutorRequest invalidRequest = new AskTutorRequest(
                null, // null lessonId
                ""    // blank question
        );

        mockMvc.perform(post("/api/v1/ai/tutor/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.lessonId").exists())
                .andExpect(jsonPath("$.errors.question").exists());
    }
}
