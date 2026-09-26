# Coop Onboarding - Frontend SPA (Vue 3 + TypeScript)

Interface web corporativa moderna de alta performance desenvolvida para a plataforma de onboarding inteligente de cooperativas de crédito.

## 🚀 Tecnologias e Arquitetura

- **Framework:** Vue 3 (Composition API com `<script setup>` e TypeScript estrito)
- **Bundler:** Vite 6
- **Gerenciamento de Estado:** Pinia 3
- **Roteamento:** Vue Router 4 (HTML5 History Mode)
- **Estilização & Design System:** TailwindCSS com paleta institucional (Teal corporativo `#0d9488` / `#0f766e` e acentos de IA em Indigo/Violeta `#6366f1` / `#8b5cf6`)
- **Tipografia:** Plus Jakarta Sans & Inter
- **Ícones:** Lucide Icons (`lucide-vue-next`)
- **Renderização Markdown:** `markdown-it` com formatação tipográfica personalizada

## 📂 Estrutura de Diretórios

```
frontend/
├── public/                 # Favicon vetorial e ativos públicos
├── src/
│   ├── api/                # Cliente HTTP tipado com suporte a Bearer token e endpoints AI/Tracks
│   ├── assets/             # Estilos globais e regras TailwindCSS
│   ├── components/
│   │   ├── ai/             # AiTutorDrawer (chat RAG) e QuizGeneratorModal (geração de quizzes)
│   │   ├── common/         # ProgressBar e SkeletonLoader
│   │   └── layout/         # AppHeader com seletor de perfil e status RBAC
│   ├── router/             # Rotas SPA (DashboardView, LessonView)
│   ├── stores/             # Stores Pinia (auth, tracks, aiTutor)
│   ├── types/              # Definições de tipos TypeScript
│   ├── views/              # Telas principais (DashboardView, LessonView)
│   ├── App.vue             # Componente raiz com botão flutuante e rodapé
│   └── main.ts             # Inicialização do Vue 3, Pinia e Router
├── index.html
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── vite.config.ts
```

## 🧩 Funcionalidades Implementadas

1. **Dashboard de Onboarding (`DashboardView`):**
   - Banner de boas-vindas personalizado com departamento do usuário.
   - Barra de progresso geral e dias restantes no SLA institucional.
   - Cards de métricas rápidas (trilhas ativas, aulas concluídas, carga horária e status do Tutor IA).
   - Grade de trilhas de capacitação com tags de departamento, SLA e botão "Iniciar/Continuar Trilha".

2. **Leitor de Lição e Checklist (`LessonView`):**
   - Sidebar responsiva com lista de módulos, checklist interativo de aulas e percentual da trilha.
   - Leitor Markdown corporativo com suporte a títulos, blocos normativos, checklists e citações.
   - Botão para marcar lição como concluída com persistência local.
   - Botão para abrir o Drawer do Tutor de IA a qualquer momento.
   - Botão "Gerar Quiz com IA" (habilitado para perfis Gestor e Admin).

3. **Tutor Virtual de Onboarding RAG (`AiTutorDrawer`):**
   - Drawer deslizante lateral com efeito backdrop blur suave.
   - Chat interativo em tempo real conectado a `/api/v1/ai/tutor/ask` com fallback contextual em caso de offline.
   - Indicador de pensamento/consulta vetorial.
   - Acordeão expansível de **Fontes e Trechos Citados** comprovando o embasamento normativo do RAG.
   - Chips de perguntas rápidas contextuais.

4. **Gerador de Quizzes com IA (`QuizGeneratorModal`):**
   - Modal exclusivo para Gestores e Administradores.
   - Configuração de número de questões (1 a 5).
   - Chamada à API `/api/v1/ai/quiz/generate`.
   - Interface interativa para responder e testar o quiz com validação instantânea e explicação conceitual detalhada gerada pela IA.

5. **Simulação de Perfis e RBAC:**
   - Dropdown no cabeçalho permite alternar entre os perfis:
     - **Ana Carolina Silva** (Colaborador - Atendimento)
     - **Roberto Mendes** (Gestor - Gente & Gestão)
     - **Mariana Duarte** (Admin - Tecnologia & Governança)

## 🛠️ Comandos de Desenvolvimento

```bash
# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento (porta 5173 com proxy para backend 8080)
npm run dev

# Verificação de tipos e compilação de produção
npm run build

# Pré-visualização do build de produção
npm run preview
```
