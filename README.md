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
[![Ollama](https://img.shields.io/badge/Ollama-Llama%203.2%201B-FF6F00?style=for-the-badge&logo=ollama&logoColor=white)](https://ollama.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Online%20(Oracle%20Cloud)-success?style=for-the-badge&logo=oracle&logoColor=white)](http://163.176.171.177)

---

## 🌐 Demonstração ao Vivo na Web (Live Demo)

A plataforma **coop-onboarding** está **publicada e em execução 24/7** em uma instância de nuvem Oracle Cloud:

👉 **[http://163.176.171.177](http://163.176.171.177)**

> ⚠️ **Atenção:** Como o acesso é realizado diretamente pelo IP público da instância (sem domínio com certificado SSL dedicado), **acesse utilizando explicitamente `http://`** (se o navegador tentar converter automaticamente para `https://`, ele exibirá erro de recusa de conexão).

### 🔑 Credenciais para Teste Rápido

Você pode entrar e testar imediatamente com qualquer um dos 3 perfis corporativos oficiais:

| Usuário | Senha | Perfil (Role) | O que testar na demonstração |
| :--- | :--- | :--- | :--- |
| **`rodrigo.admin`** | `Admin@123` | Administrador (`ROLE_ADMIN`) | **Gestão de Usuários (CRUD completo sincronizado com Keycloak e DB)**, Painel de Governança e Saúde dos Serviços, métricas e recursos de Gestor. |
| **`mariana.gestora`** | `Gestor@123` | Gestora (`ROLE_GESTOR`) | Dashboard analítico de desempenho dos colaboradores da equipe, progresso de trilhas e **Gerador de Quizzes pedagógicos via IA**. |
| **`lucas.colaborador`** | `Colab@123` | Colaborador (`ROLE_COLABORADOR`) | Trilhas de formação, leitor imersivo, **Guia Rápido de Onboarding**, quizzes de fixação e **Tutor IA com Streaming SSE em tempo real**. |

> 💡 **Dica de Acesso Rápido:** A tela de login dispõe de botões interativos (*chips*) para preenchimento imediato das credenciais de Rodrigo, Mariana ou Lucas com apenas 1 clique!

---

## 🏛️ Visão Geral & Proposta de Valor

O **coop-onboarding** é uma plataforma corporativa completa desenvolvida para transformar e acelerar a integração de novos colaboradores em cooperativas e empresas com fluxos operacionais e regulatórios complexos.

Combinando os padrões rigorosos de segurança e resiliência da arquitetura enterprise em **Java 21 / Spring Boot 3** com a vanguarda de **IA Generativa Local e RAG Semântico via Spring AI + pgvector** e uma interface fluida em **Vue 3 + TypeScript**, o sistema oferece:

- 🧭 **Trilhas de Aprendizagem Adaptativas:** Conteúdos organizados por cargo e área com SLAs de conclusão e métricas de desempenho.
- 🤖 **Tutor Virtual de Onboarding (RAG 100% On-Premise):** Assistente inteligente que responde dúvidas pontuais sobre manuais, processos e normas internas sem enviar dados sensíveis para nuvens de terceiros, citando as fontes normativas.
- ⚡ **Inferência Otimizada & Streaming SSE:** Respostas em tempo real via Server-Sent Events alimentadas pelo modelo ultraleve **`llama3.2:1b`** com embeddings `nomic-embed-text` e cache em memória de alta performance.
- 📝 **Geração Automatizada de Quizzes:** Criação instantânea de testes pedagógicos de fixação de múltipla escolha com gabarito e justificativa a partir dos materiais cadastrados.
- 🔒 **Governança & Segurança Corporativa:** Autenticação e autorização unificadas via **Keycloak (OAuth2 / OpenID Connect)** com controle de acesso granular baseado em papéis (`ROLE_ADMIN`, `ROLE_GESTOR`, `ROLE_COLABORADOR`).
- 🎨 **Experiência de Usuário de Alta Performance:** SPA responsiva construída com design system corporativo, zero layout shift (CLS), gaveta lateral interativa do Tutor de IA com largura redimensionável (420px a 1200px), card de carregamento skeleton animado e Guia Rápido contextual.

---

## 👥 Credenciais & Perfis de Acesso

O ambiente corporativo já vem provisionado com 3 perfis reais, integrados bidirecionalmente entre o banco de dados PostgreSQL (`public.users` e `identity.user_profiles`) e o Keycloak IAM:

| Usuário | Senha | Nome | Papel (Role) | Escopo de Acesso & Funcionalidades |
| :--- | :--- | :--- | :--- | :--- |
| **`rodrigo.admin`** | `Admin@123` | Rodrigo Mendes | `ROLE_ADMIN` | **Super Administrador:** Possui **Gestão Completa de Usuários** (criação, edição e exclusão de contas sincronizadas em tempo real no Keycloak e no PostgreSQL), **Painel de Governança e Saúde dos Serviços** (status do PostgreSQL/pgvector, métricas da IA local Ollama e diagnósticos de infraestrutura) e **todos os privilégios pedagógicos de Gestor**. |
| **`mariana.gestora`** | `Gestor@123` | Mariana Silva | `ROLE_GESTOR` | **Gestão de Equipe & Aprendizado:** Dashboard analítico de desempenho dos colaboradores da equipe, acompanhamento de taxas de conclusão, consulta pedagógica de trilhas e **Gerador de Quizzes pedagógicos via IA**. |
| **`lucas.colaborador`** | `Colab@123` | Lucas Ferreira | `ROLE_COLABORADOR` | **Experiência do Aprendiz:** Acesso às trilhas de formação, leitor imersivo de lições, **Guia Rápido de Onboarding**, realização de quizzes de fixação e **Tutor IA** em tempo real com streaming. |

> 🛡️ **Segurança e Validação de Token:** A aplicação implementa validação ativa de token JWT junto ao endpoint de UserInfo do Keycloak (`/protocol/openid-connect/userinfo`). Cada ciclo de navegação protegida verifica a autenticidade e validade do token. Em caso de expiração ou revogação, o usuário é redirecionado imediatamente para a tela de Login com armazenamento de sessão isolado em `sessionStorage`.

---

## 🏗️ Arquitetura do Sistema

```mermaid
flowchart TD
    subgraph Client["Frontend / Client (Vue 3 + TypeScript + Tailwind)"]
        SPA["Dashboard & Lesson Viewer"]
        AIDrawer["AI Tutor Drawer (Resizable 420-1200px + Skeleton)"]
        QuizModal["Quiz Generator Modal"]
        UserMgmt["Cockpit de Gestão & Usuários (/gestao)"]
        AdminPanel["Admin & System Health Console"]
    end

    subgraph Security["Identity & Access Management"]
        KC["Keycloak 25 (OAuth2 / JWT / RBAC / Admin REST)"]
    end

    subgraph Backend["Core API (Java 21 + Spring Boot 3.3.5)"]
        ResourceServer["Spring Security Resource Server (JWT Filter)"]
        TrackModule["Domain: Track & Content"]
        UserModule["Domain: User & Enrollment"]
        AdminModule["Admin Module: User CRUD & Keycloak Sync"]
        AIModule["Spring AI Engine (RAG Tutor & Quiz Generator)"]
        CacheModule["In-Memory Streaming Cache"]
    end

    subgraph AIInfra["AI & Local Inference (Ollama)"]
        OllamaChat["Ollama LLM (llama3.2:1b - Temp 0.1)"]
        OllamaEmbed["Ollama Embeddings (nomic-embed-text)"]
    end

    subgraph Database["Persistence & Vector Store"]
        Postgres["PostgreSQL 16 (public + identity schemas)"]
        PgVector["pgvector (HNSW Index / Cosine Similarity)"]
    end

    SPA -->|"1. Authenticate & Token Validation"| KC
    SPA -->|"2. Bearer JWT API Call"| ResourceServer
    UserMgmt -->|"3. Admin User CRUD /api/v1/admin/users"| ResourceServer
    AIDrawer -->|"4. Stream SSE /api/v1/ai/tutor/stream"| AIModule
    QuizModal -->|"5. Generate Quiz /api/v1/ai/quiz/generate"| AIModule
    AdminPanel -->|"6. Health & Services /actuator/health"| ResourceServer

    ResourceServer --> TrackModule
    ResourceServer --> UserModule
    ResourceServer --> AdminModule
    ResourceServer --> AIModule

    AdminModule -->|"Bidirectional Sync (Users/Roles/Credentials)"| KC
    AdminModule -->|"users & user_profiles"| Postgres
    AIModule --> CacheModule
    TrackModule --> Postgres
    UserModule --> Postgres
    AIModule -->|"Embeddings generation"| OllamaEmbed
    AIModule -->|"Metadata Filtered Similarity Search (topK=2)"| PgVector
    AIModule -->|"Context & Prompt"| OllamaChat
```

---

## 🤖 Engenharia de IA Local & Otimizações de Desempenho

A plataforma opera com inferência de IA 100% on-premise, garantindo conformidade total com a LGPD e privacidade corporativa:

1. **Modelo de Linguagem (LLM):** `llama3.2:1b` (1.3B parâmetros), selecionado por oferecer a melhor relação custo-benefício e velocidade em CPUs corporativas padrão sem exigir placa de vídeo dedicada.
2. **Modelo de Embeddings:** `nomic-embed-text` (768 dimensões), gerando representações semânticas precisas de regulamentos internos e cartilhas de cooperativismo.
3. **Busca Vetorial Segmentada (`pgvector`):** Utiliza metadados estritos (`lessonId`, `trackId`, `documentType`) e índice HNSW com métrica de cosseno, limitando o `topK` em 2 fragmentos essenciais para cortar a latência de pré-avaliação do prompt para menos de 1.5 segundo.
4. **Streaming SSE sem Regressão de Espaços:** Endpoint reativo `/api/v1/ai/tutor/stream` consumido em tempo real, preservando espaçamento natural de palavras e renderizando blocos Markdown com suporte a cópia rápida.
5. **Cache em Memória de Streaming:** Perguntas repetidas são servidas imediatamente do cache em ~2 segundos com consumo zero de CPU do modelo.
6. **Card Skeleton Animado:** Substituição de bolhas vazias por indicador de progresso estruturado com animação *shimmer*, status do modelo ativo e etapas da busca normativa.

---

## 🚀 Como Executar o Ecossistema Completo

### Pré-requisitos
- **Docker & Docker Compose** (para execução em contêineres)
- **Java 21 LTS** e **Node.js 20+** (necessários apenas para desenvolvimento híbrido fora do Docker)
- **Ollama** com os modelos locais baixados:
  ```bash
  ollama pull nomic-embed-text
  ollama pull llama3.2:1b
  ```

---

### 🌟 Opção A: Execução Completa via Docker Compose Local (Recomendado)

Você pode subir toda a plataforma de ponta a ponta (Frontend, Backend, Keycloak e PostgreSQL/pgvector) com um único comando:

```bash
docker compose -f docker/docker-compose.local.yml up -d --build
```

#### Serviços e Portas Locais Ativas:
| Serviço | URL / Endereço | Descrição / Credenciais Padrão |
| :--- | :--- | :--- |
| **Frontend SPA (Nginx)** | [http://localhost](http://localhost) | Interface Web corporativa em Vue 3 (porta 80). Proxy reverso configurado para `/api/` e `/auth/`. |
| **Backend API (Spring Boot)** | [http://localhost:8080](http://localhost:8080) | API REST Java 21. Health Check: `/actuator/health` \| Swagger UI: `/swagger-ui.html` |
| **Keycloak IAM** | [http://localhost:8180](http://localhost:8180) | Servidor de Identidade OpenID Connect. Admin Console: `admin` / `admin` |
| **PostgreSQL 16 + pgvector** | `localhost:5432` | Banco relacional e vetorial. Usuário: `coop_user` \| Senha: `coop_pass` \| DB: `coop_onboarding` |

Para encerrar os contêineres locais:
```bash
docker compose -f docker/docker-compose.local.yml down
```

---

### 🛠️ Opção B: Desenvolvimento Híbrido (Local / IDE)

Se preferir rodar o Backend e Frontend diretamente na máquina de desenvolvimento:

#### 1. Subir Infraestrutura (PostgreSQL + pgvector e Keycloak)
Na raiz do projeto:
```bash
docker compose -f docker/docker-compose.yml up -d
```

#### 2. Executar o Backend (Spring Boot 3.3.5)
```bash
cd backend
./mvnw spring-boot:run
```
A API iniciará em `http://localhost:8080`.

#### Rodar Suíte Completa de Testes Automatizados (com Testcontainers & MockMvc):
```bash
./mvnw clean test
```
*(Executa testes de conformidade arquitetural ArchUnit, testes com Testcontainers contra PostgreSQL real e testes de segurança do AdminUserControllerSecurityTests).*

#### 3. Executar o Frontend (Vue 3 + Vite)
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

### 4. Executar Testes E2E (Playwright)
Para validar o fluxo completo dos três perfis (`colaborador`, `gestor` e `admin`) e a interação do Tutor IA:
```bash
cd frontend
node test-all-roles.mjs
```

---

## 🔄 Esteira de CI/CD & Deploy Contínuo (GitLab-Style no GitHub)

O ecossistema implementa uma esteira corporativa completa de **Integração Contínua e Entrega Contínua (CI/CD)** via **GitHub Actions** (`.github/workflows/ci-cd.yml`) com publicação automática de contêineres no **GitHub Container Registry (GHCR)**:

```mermaid
flowchart LR
    DevBranch["develop (Dev)"] -->|Push / PR| CI1["Testes & Build Docker :dev"]
    CI1 --> GHCR1["GHCR (ghcr.io/.../backend:dev)"]
    
    DevBranch -->|Merge PR| StagingBranch["staging (QA)"]
    StagingBranch --> CI2["Testes & Build Docker :staging"]
    CI2 --> GHCR2["GHCR (ghcr.io/.../backend:staging)"]
    GHCR2 --> StagingDeploy["Deploy em Homologação"]

    StagingBranch -->|Merge PR| MainBranch["main (Prod)"]
    MainBranch --> CI3["Testes & Build Docker :latest"]
    CI3 --> GHCR3["GHCR (ghcr.io/.../backend:latest)"]
    GHCR3 --> WebDeploy["🚀 Deploy na Web (Produção)"]
```

### Topologia de Branches e Tags:
- **`develop`:** Ambiente de desenvolvimento contínuo. Imagens geradas com tag `:dev` e `:${GITHUB_SHA}`.
- **`staging`:** Ambiente de homologação e validação de qualidade (QA). Imagens geradas com tag `:staging` e `:qa`.
- **`main`:** Ambiente de produção oficial. Imagens geradas com tag `:latest` e `:production`, disparando deploy automatizado via SSH para a instância na nuvem Oracle Cloud (`http://163.176.171.177`).

### Executar a Stack de Produção Completa com Docker Compose:
```bash
docker compose -f docker/docker-compose.prod.yml up -d
```
*(Executa Frontend Nginx na porta 80, Backend Spring Boot, PostgreSQL 16 com pgvector, Keycloak 25 e Ollama em rede isolada).*

---

## 🗺️ Roadmap de Desenvolvimento Concluído

- [x] **Fase 0: Setup de Infraestrutura & Ambiente** (Docker, pgvector, Keycloak, Java 21, Spring Boot 3.3)
- [x] **Fase 1: Modelagem de Dados & Flyway Migrations** (Tabelas de Trilhas, Módulos, Aulas, Matrículas e tabela vetorial HNSW)
- [x] **Fase 2: Configuração de Segurança Keycloak RBAC** (JwtAuthenticationConverter, Roles e Endpoints protegidos)
- [x] **Fase 3: RAG e Ingestão de Documentos com Spring AI** (Chunking com TokenTextSplitter, Tutor RAG filtrado por aula e Gerador de Quizzes estruturados)
- [x] **Fase 4: Frontend Corporativo SPA em Vue 3** (Dashboard de progresso, Leitor de Lições, AI Tutor Drawer e Modal de Quizzes)
- [x] **Fase 5: Otimizações de IA e Experiência do Usuário (UX/UI)**:
  - Redimensionamento interativo da tela de chat (420px a 1200px) com persistência de layout;
  - Adoção do modelo ultraleve `llama3.2:1b` e hiperparâmetros de baixa latência;
  - Resolução definitiva de espaçamento de palavras em streaming SSE;
  - Fase de carregamento com *Skeleton Shimmer Card*;
  - Guia Rápido contextual exclusivo para perfil Colaborador;
  - Console de status de serviços e base vetorial para perfil Admin.
- [x] **Fase 6: Governança de Identidade, Gestão de Usuários e Segurança Canônica**:
  - Provisionamento e sincronização bidirecional de usuários entre aplicação e Keycloak IAM;
  - Cockpit de Gestão de Usuários completo exclusivo para Administradores (`/gestao`) com criação, edição, exclusão e redefinição de credenciais;
  - Proteção de segurança ativa contra auto-exclusão do administrador logado e salvaguarda da conta mestre `rodrigo.admin`;
  - Validação ativa de token OpenID Connect via endpoint UserInfo (`/protocol/openid-connect/userinfo`) e início estrito na tela de Login;
  - Sessões isoladas via `sessionStorage` eliminando auto-logins fantasmas e retenção indevida de credenciais;
  - *Quick-fill chips* interativos na tela de Login para alternância instantânea entre os perfis Rodrigo (Admin), Mariana (Gestora) e Lucas (Colaborador);
  - Cobertura de testes de segurança com `AdminUserControllerSecurityTests` e pipeline sincronizada nas branches `develop`, `staging` e `main` com deploy na nuvem Oracle Cloud.


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
