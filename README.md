# 🎓 coop-onboarding

> **Plataforma Corporativa de Treinamento e Onboarding de Novos Colaboradores com IA Generativa Local (RAG), Segurança Corporativa (Keycloak RBAC) e SPA Moderna em Vue 3.**

[![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Vue 3](https://img.shields.io/badge/Vue.js-3.5%20Composition%20API-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![TailwindCSS](https://img.shields.io/badge/Tailwind-3.4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%20%2B%20pgvector-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://github.com/pgvector/pgvector)
[![Keycloak](https://img.shields.io/badge/Keycloak-25.0-008080?style=for-the-badge&logo=keycloak&logoColor=white)](https://www.keycloak.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

---

## 🏛️ Visão Geral & Proposta de Valor

O **coop-onboarding** é uma plataforma corporativa completa desenvolvida para transformar e acelerar a integração de novos colaboradores em cooperativas e empresas com fluxos operacionais e regulatórios complexos.

Combinando os padrões rigorosos de segurança e resiliência da arquitetura enterprise em **Java 21 / Spring Boot 3** com a vanguarda de **IA Generativa Local e RAG Semântico via Spring AI + pgvector** e uma interface fluida em **Vue 3 + TypeScript**, o sistema oferece:

- 🧭 **Trilhas de Aprendizagem Adaptativas:** Conteúdos organizados por cargo e área com SLAs de conclusão e métricas de desempenho.
- 🤖 **Tutor Virtual de Onboarding (RAG 100% On-Premise):** Assistente inteligente que responde dúvidas pontuais sobre manuais, processos e normas internas sem enviar dados sensíveis para nuvens de terceiros, citando as fontes normativas.
- 📝 **Geração Automatizada de Quizzes:** Criação instantânea de testes pedagógicos de fixação de múltipla escolha com gabarito e justificativa a partir dos materiais cadastrados.
- 🔒 **Governança & Segurança Corporativa:** Autenticação e autorização unificadas via **Keycloak (OAuth2 / OpenID Connect)** com controle de acesso baseado em papéis (`ADMIN`, `GESTOR`, `COLABORADOR`).
- 🎨 **Experiência de Usuário de Alta Performance:** SPA responsiva construída com design system corporativo, zero layout shift (CLS) e gaveta lateral interativa do Tutor de IA com microinterações táteis.

---

## 🏗️ Arquitetura do Sistema

```mermaid
flowchart TD
    subgraph Client["Frontend / Client (Vue 3 + TypeScript)"]
        SPA["Dashboard & Lesson Viewer"]
        AIDrawer["AI Tutor Drawer (RAG Chat)"]
        QuizModal["Quiz Generator Modal"]
    end

    subgraph Security["Identity & Access Management"]
        KC["Keycloak 25 (OAuth2 / JWT / RBAC)"]
    end

    subgraph Backend["Core API (Java 21 + Spring Boot 3.3)"]
        ResourceServer["Spring Security Resource Server"]
        TrackModule["Domain: Track & Content"]
        UserModule["Domain: User & Enrollment"]
        AIModule["Spring AI Engine (RAG & Quiz)"]
    end

    subgraph AIInfra["AI & Local Inference"]
        Ollama["Ollama Local (Llama 3.2 / DeepSeek)"]
        Embeddings["Embedding Model (nomic-embed-text)"]
    end

    subgraph Database["Persistence & Vector Store"]
        Postgres["PostgreSQL 16"]
        PgVector["pgvector (HNSW Index / Cosine)"]
    end

    SPA -->|1. Authenticate| KC
    SPA -->|2. Bearer JWT API Call| ResourceServer
    AIDrawer -->|3. Ask Tutor /api/v1/ai/tutor/ask| AIModule
    QuizModal -->|4. Generate Quiz /api/v1/ai/quiz/generate| AIModule

    ResourceServer --> TrackModule
    ResourceServer --> UserModule
    ResourceServer --> AIModule

    TrackModule --> Postgres
    UserModule --> Postgres
    AIModule -->|RAG Query / Embeddings| Embeddings
    AIModule -->|Similarity Search (Metadata Filtering)| PgVector
    AIModule -->|Prompt & Context| Ollama
```

---

## 🚀 Como Executar o Ecossistema Completo

### Pré-requisitos
- **Java 21 LTS**
- **Node.js 20+**
- **Docker & Docker Compose**
- **Ollama** com os modelos:
  ```bash
  ollama pull nomic-embed-text
  ollama pull llama3.2:3b
  ```

---

### 1. Subir Infraestrutura (PostgreSQL + pgvector e Keycloak)
Na raiz do projeto:
```bash
docker compose -f docker/docker-compose.yml up -d
```
- **PostgreSQL:** `localhost:5432` (Database: `coop_onboarding`, Usuário: `coop_user`, Senha: `coop_pass`)
- **Keycloak:** `http://localhost:8180` (Realm `coop-onboarding` importado automaticamente com usuários `admin`, `gestor`, `colaborador`)

---

### 2. Executar o Backend (Spring Boot 3.3)
```bash
cd backend
./mvnw spring-boot:run
```
A API iniciará em `http://localhost:8080`.

#### Rodar Suíte Completa de Testes Automatizados (38 testes):
```bash
./mvnw clean test
```
*(Executa testes de conformidade arquitetural com ArchUnit, testes de integração com Testcontainers contra PostgreSQL + pgvector real e testes de segurança MockMvc).*

---

### 3. Executar o Frontend (Vue 3 + Vite)
Em outro terminal:
```bash
cd frontend
npm install
npm run dev
```
A interface iniciará em `http://localhost:5173`.

#### Compilar para Produção:
```bash
cd frontend
npm run build
```

---

## 🗺️ Roadmap de Desenvolvimento Concluído

- [x] **Fase 0: Setup de Infraestrutura & Ambiente** (Docker, pgvector, Keycloak, Java 21, Spring Boot 3.3)
- [x] **Fase 1: Modelagem de Dados & Flyway Migrations** (Tabelas de Trilhas, Módulos, Aulas, Matrículas e tabela vetorial HNSW)
- [x] **Fase 2: Configuração de Segurança Keycloak RBAC** (JwtAuthenticationConverter, Roles e Endpoints protegidos)
- [x] **Fase 3: RAG e Ingestão de Documentos com Spring AI** (Chunking com TokenTextSplitter, Tutor RAG filtrado por aula e Gerador de Quizzes estruturados)
- [x] **Fase 4: Frontend Corporativo SPA em Vue 3** (Dashboard de progresso, Leitor de Lições, AI Tutor Drawer e Modal de Quizzes)

---

## 📜 Governança e Qualidade
Este projeto foi concebido e implementado seguindo a metodologia **[senior-ai-dev-workflow](https://github.com/raguiohead/senior-ai-dev-workflow)**:
- Política estrita de **Issue First** e branches isoladas de feature.
- **Commits Semânticos** convencionais.
- Separação de camadas validada por **ArchUnit**.
- Testes de integração reais via **Testcontainers**.
- Princípios de **frontend-design** para interfaces corporativas autorais.
Consulte [DEVELOPMENT.md](file:///mnt/HD-13/projects/coop-onboarding/DEVELOPMENT.md) para detalhes de arquitetura.

---

## 📄 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais detalhes.
