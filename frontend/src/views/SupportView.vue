<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAiTutorStore } from '@/stores/aiTutor';
import {
  LifeBuoy,
  Headphones,
  Users,
  ShieldCheck,
  ChevronDown,
  ChevronUp,
  ChevronRight,
  Send,
  CheckCircle2,
  Sparkles,
  Search,
  MessageSquare,
  HelpCircle,
  FileQuestion,
  Clock,
} from 'lucide-vue-next';

const router = useRouter();
const aiTutorStore = useAiTutorStore();

// Formulário de Ticket
const category = ref('trilhas');
const priority = ref('normal');
const subject = ref('');
const description = ref('');
const isSubmitting = ref(false);
const submittedTicketNumber = ref<string | null>(null);

// FAQ State
const searchQuery = ref('');
const openFaqIndex = ref<number | null>(0);

const faqList = [
  {
    question: 'Qual é o prazo limite (SLA) para conclusão da minha trilha de integração?',
    answer: 'Cada colaborador possui um SLA padrão de 14 dias corridos a partir da data de admissão. O acompanhamento em tempo real pode ser visualizado no Painel Geral do Dashboard.',
  },
  {
    question: 'Como funciona a avaliação de desempenho nos Quizzes da plataforma?',
    answer: 'Ao final de cada módulo você responderá um quiz de fixação. É necessário atingir ao menos 70% de aproveitamento para receber o selo de certificação do módulo. Caso necessário, é possível refazer o simulado a qualquer momento.',
  },
  {
    question: 'O Tutor Virtual de IA tem acesso aos manuais normativos e produtos da cooperativa?',
    answer: 'Sim! Nosso Tutor IA utiliza arquitetura RAG (Retrieval-Augmented Generation) indexando a legislação cooperativista brasileira, normativos do BACEN e a política de crédito da instituição.',
  },
  {
    question: 'Não consigo acessar algum sistema corporativo. Quem devo procurar?',
    answer: 'Para credenciais de rede, e-mail corporativo ou permissões de sistemas de crédito, abra um chamado diretamente com o Service Desk de TI através desta página ou pelo ramal interno 4001.',
  },
  {
    question: 'Como solicito a prorrogação do prazo de uma trilha em caso de imprevisto?',
    answer: 'Seu gestor imediato pode conceder prorrogação de SLA diretamente pelo Painel de Gestão da Turma (/gestao). Converse com ele ou acione o time de Gente & Gestão.',
  },
];

function toggleFaq(index: number) {
  openFaqIndex.value = openFaqIndex.value === index ? null : index;
}

function handleSubmitTicket() {
  if (!subject.value || !description.value) return;

  isSubmitting.value = true;
  setTimeout(() => {
    isSubmitting.value = false;
    const randomTicket = `COOP-${Math.floor(100000 + Math.random() * 900000)}`;
    submittedTicketNumber.value = randomTicket;
    subject.value = '';
    description.value = '';
  }, 1000);
}
</script>

<template>
  <div class="space-y-10 pb-16 animate-fade-in max-w-5xl mx-auto">
    <!-- Breadcrumb & Hero -->
    <div class="space-y-4">
      <nav class="flex items-center space-x-2 text-xs text-slate-500">
        <router-link to="/" class="hover:text-brand-700 transition-colors">Início</router-link>
        <ChevronRight class="w-3.5 h-3.5" />
        <span class="text-slate-900 font-semibold">Suporte ao Colaborador</span>
      </nav>

      <div class="bg-gradient-to-br from-brand-900 via-slate-900 to-brand-950 rounded-3xl p-8 sm:p-10 text-white relative overflow-hidden shadow-xl border border-slate-800">
        <div class="absolute -right-10 -bottom-10 w-80 h-80 bg-brand-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="relative z-10 max-w-2xl space-y-4">
          <div class="inline-flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/30">
            <LifeBuoy class="w-3.5 h-3.5" />
            <span>Central de Ajuda & Atendimento Interno</span>
          </div>
          <h1 class="text-2xl sm:text-4xl font-extrabold tracking-tight font-sans">
            Suporte Integral ao Colaborador
          </h1>
          <p class="text-sm sm:text-base text-slate-300 leading-relaxed">
            Estamos ao seu lado durante toda a sua jornada de integração. Tire dúvidas, acione o Service Desk de TI ou fale com o time de Gente & Gestão.
          </p>
        </div>
      </div>
    </div>

    <!-- 1. Canais Rápidos de Atendimento -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs flex flex-col justify-between space-y-4 hover:border-brand-300 transition-colors">
        <div class="space-y-3">
          <div class="w-10 h-10 rounded-2xl bg-indigo-50 text-indigo-700 flex items-center justify-center">
            <Headphones class="w-5 h-5" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Service Desk de TI</h3>
          <p class="text-xs text-slate-500 leading-relaxed">
            Problemas com login no Keycloak, computadores, e-mail corporativo ou estações de trabalho.
          </p>
        </div>
        <div class="pt-2 text-xs font-semibold text-indigo-700 flex items-center justify-between border-t border-slate-100">
          <span>Ramal: 4001</span>
          <span class="text-slate-400 font-normal">Seg a Sex, 8h às 18h</span>
        </div>
      </div>

      <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs flex flex-col justify-between space-y-4 hover:border-brand-300 transition-colors">
        <div class="space-y-3">
          <div class="w-10 h-10 rounded-2xl bg-teal-50 text-teal-700 flex items-center justify-center">
            <Users class="w-5 h-5" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Gente & Gestão (RH)</h3>
          <p class="text-xs text-slate-500 leading-relaxed">
            Dúvidas sobre o plano de integração, benefícios, certidões, crachás e acompanhamento de carreira.
          </p>
        </div>
        <div class="pt-2 text-xs font-semibold text-teal-700 flex items-center justify-between border-t border-slate-100">
          <span>Ramal: 4005</span>
          <span class="text-slate-400 font-normal">rh@coop.local</span>
        </div>
      </div>

      <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs flex flex-col justify-between space-y-4 hover:border-brand-300 transition-colors">
        <div class="space-y-3">
          <div class="w-10 h-10 rounded-2xl bg-ai-50 text-ai-600 flex items-center justify-center">
            <Sparkles class="w-5 h-5" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Tutor Virtual de IA</h3>
          <p class="text-xs text-slate-500 leading-relaxed">
            Dúvidas imediatas sobre conceitos de cooperativismo, 7 princípios, assembleias e termos bancários.
          </p>
        </div>
        <button
          @click="aiTutorStore.openDrawer"
          class="w-full py-2 px-3 rounded-xl bg-ai-600 hover:bg-ai-700 text-white text-xs font-bold transition-colors cursor-pointer flex items-center justify-center gap-1.5"
        >
          <Sparkles class="w-3.5 h-3.5" />
          <span>Abrir Tutor Agora (24h)</span>
        </button>
      </div>
    </div>

    <!-- 2. Perguntas Frequentes (FAQ) -->
    <section class="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-xs space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-lg font-bold text-slate-900 flex items-center space-x-2">
            <HelpCircle class="w-5 h-5 text-brand-600" />
            <span>Perguntas Frequentes (FAQ)</span>
          </h2>
          <p class="text-xs text-slate-500 mt-1">Respostas rápidas para as dúvidas mais comuns dos novos colaboradores.</p>
        </div>
      </div>

      <div class="space-y-3 pt-2">
        <div
          v-for="(item, idx) in faqList"
          :key="idx"
          class="rounded-2xl border border-slate-200/80 overflow-hidden transition-colors"
        >
          <button
            @click="toggleFaq(idx)"
            class="w-full px-5 py-4 text-left flex items-center justify-between text-xs sm:text-sm font-bold text-slate-800 hover:bg-slate-50 transition-colors cursor-pointer"
          >
            <span>{{ item.question }}</span>
            <ChevronUp v-if="openFaqIndex === idx" class="w-4 h-4 text-slate-400 shrink-0" />
            <ChevronDown v-else class="w-4 h-4 text-slate-400 shrink-0" />
          </button>
          <div
            v-if="openFaqIndex === idx"
            class="px-5 pb-4 text-xs sm:text-sm text-slate-600 leading-relaxed bg-slate-50/50 border-t border-slate-100"
          >
            {{ item.answer }}
          </div>
        </div>
      </div>
    </section>

    <!-- 3. Formulário de Abertura de Chamado -->
    <section class="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-xs space-y-6">
      <div>
        <h2 class="text-lg font-bold text-slate-900 flex items-center space-x-2">
          <MessageSquare class="w-5 h-5 text-brand-600" />
          <span>Abertura de Chamado Interno</span>
        </h2>
        <p class="text-xs text-slate-500 mt-1">
          Não encontrou o que precisava? Envie sua solicitação diretamente para a equipe responsável.
        </p>
      </div>

      <!-- Feedback de Ticket Criado com Sucesso -->
      <div v-if="submittedTicketNumber" class="p-6 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-900 space-y-3">
        <div class="flex items-center space-x-3">
          <CheckCircle2 class="w-6 h-6 text-emerald-600" />
          <div>
            <h4 class="text-sm font-bold">Chamado registrado com sucesso!</h4>
            <p class="text-xs text-emerald-700">Protocolo gerado: <strong class="font-mono text-emerald-950">{{ submittedTicketNumber }}</strong></p>
          </div>
        </div>
        <p class="text-xs text-emerald-800">
          Nossa equipe interna responderá no seu e-mail corporativo em até 4 horas úteis.
        </p>
        <button
          @click="submittedTicketNumber = null"
          class="text-xs font-bold text-emerald-700 hover:underline cursor-pointer"
        >
          Abrir outro chamado
        </button>
      </div>

      <form v-else @submit.prevent="handleSubmitTicket" class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Área Responsável</label>
            <select
              v-model="category"
              class="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-xs sm:text-sm font-medium text-slate-800 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
            >
              <option value="trilhas">Trilhas de Aprendizagem & Quizzes</option>
              <option value="ti">Tecnologia da Informação & Acessos</option>
              <option value="rh">Gente & Gestão (Benefícios e Carreira)</option>
              <option value="ouvidoria">Ouvidoria & Sugestões</option>
            </select>
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Prioridade</label>
            <select
              v-model="priority"
              class="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-xs sm:text-sm font-medium text-slate-800 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
            >
              <option value="baixa">Baixa (Dúvida pontual)</option>
              <option value="normal">Normal (Até 24h)</option>
              <option value="alta">Alta (Bloqueia meu aprendizado)</option>
            </select>
          </div>
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Assunto Resumido</label>
          <input
            v-model="subject"
            type="text"
            required
            placeholder="Ex: Erro ao concluir a lição do Módulo 2"
            class="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-xs sm:text-sm font-medium text-slate-800 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-700 mb-1.5 uppercase tracking-wide">Descrição Detalhada</label>
          <textarea
            v-model="description"
            rows="4"
            required
            placeholder="Descreva o que aconteceu ou a dúvida específica..."
            class="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 text-xs sm:text-sm font-medium text-slate-800 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
          ></textarea>
        </div>

        <div class="pt-2 flex justify-end">
          <button
            type="submit"
            :disabled="isSubmitting"
            class="inline-flex items-center space-x-2 px-6 py-3 rounded-2xl text-xs sm:text-sm font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-md shadow-brand-600/20 disabled:opacity-50 transition-all cursor-pointer"
          >
            <Send class="w-4 h-4" />
            <span>{{ isSubmitting ? 'Enviando chamado...' : 'Enviar Chamado' }}</span>
          </button>
        </div>
      </form>
    </section>
  </div>
</template>
