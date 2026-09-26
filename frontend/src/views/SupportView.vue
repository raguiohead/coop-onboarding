<script setup lang="ts">
import { ref, computed } from 'vue';
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
const selectedFaqCategory = ref('todas');
const openFaqIndex = ref<number | null>(0);

interface FaqItem {
  category: 'trilhas' | 'gestao' | 'quizzes' | 'ia' | 'certificados';
  categoryLabel: string;
  question: string;
  answer: string;
}

const faqList: FaqItem[] = [
  {
    category: 'trilhas',
    categoryLabel: 'Jornada & Metodologia',
    question: 'Como funciona a jornada de onboarding e qual a metodologia adotada?',
    answer: 'A plataforma integra o Método PARA institucional (Projetos, Áreas, Recursos e Arquivos) com o modelo de aprendizado por micro-lições. Cada colaborador ingressante recebe uma trilha de formação segmentada em módulos conceituais, leituras normativas do Sistema Cooperativo e avaliações práticas de fixação.',
  },
  {
    category: 'gestao',
    categoryLabel: 'Gestão da Turma',
    question: 'Como o gestor acompanha em qual lição e módulo cada colaborador está?',
    answer: 'No Painel de Gestão (/gestao), o gestor possui o "Raio-X de Aprendizagem". Em tempo real, a tabela exibe a etapa exata em que o colaborador parou (por exemplo: "Módulo 2: Lição 2.2"). Clicando no botão "Raio-X", o gestor abre o histórico completo com datas de conclusão, tempo dedicado, notas de simulados e pode disparar lembretes pedagógicos de apoio.',
  },
  {
    category: 'gestao',
    categoryLabel: 'Papel do Gestor',
    question: 'Por que o gestor não precisa completar as lições ou prestar os quizzes?',
    answer: 'Os gestores exercem papel de liderança, monitoramento pedagógico e supervisão regulatória. Por isso, as lições aparecem para eles em Modo de Consulta da Grade e os quizzes funcionam como "Gabarito Comentado", permitindo auditar as perguntas, respostas corretas e fundamentações BACEN sem gerar notas avaliativas no histórico.',
  },
  {
    category: 'quizzes',
    categoryLabel: 'Quizzes & Avaliações',
    question: 'Como funciona a avaliação de desempenho nos Quizzes da plataforma?',
    answer: 'Ao final de cada módulo você responderá a um quiz de múltipla escolha. A nota de aprovação institucional é de 70%. O sistema registra a maior nota obtida e você pode refazer o simulado sempre que desejar para consolidar o conhecimento.',
  },
  {
    category: 'ia',
    categoryLabel: 'Tutor IA & RAG Local',
    question: 'Como o Tutor IA responde às dúvidas dos colaboradores utilizando normativos locais?',
    answer: 'Nosso Tutor Virtual opera com arquitetura RAG (Retrieval-Augmented Generation) 100% soberana e local. Quando você faz uma pergunta, o sistema realiza uma busca semântica em alta velocidade no banco vetorial pgvector, consultando normativos da Lei 5.764/71, resoluções do CMN/BACEN e manuais internos da cooperativa, gerando respostas fundamentadas e com citação das fontes normativas.',
  },
  {
    category: 'certificados',
    categoryLabel: 'Prazos & SLA',
    question: 'Qual é o prazo regulatório (SLA) para conclusão e o que acontece se expirar?',
    answer: 'O SLA padrão é de 14 dias corridos a partir da data de admissão. Caso o prazo se aproxime do fim (menos de 3 dias), a plataforma emite um alerta visual prioritário. O gestor imediato pode conceder prorrogação ou entrar em contato através do canal de apoio.',
  },
  {
    category: 'certificados',
    categoryLabel: 'Certificação Digital',
    question: 'Como e quando o meu Certificado de Formação Cooperativista é emitido?',
    answer: 'O Certificado Oficial de Qualificação Cooperativa é gerado automaticamente na aba "Meu Perfil" assim que todas as lições obrigatórias forem concluídas com aproveitamento superior a 70% nos quizzes. O certificado conta com chave hash criptográfica SHA-256 e validação para fins curriculares internos.',
  },
  {
    category: 'gestao',
    categoryLabel: 'Administração & RBAC',
    question: 'Qual a diferença de poder entre o Administrador, o Gestor e o Colaborador?',
    answer: 'O Colaborador foca em sua trilha individual de aprendizado. O Gestor supervisiona sua turma, acompanha o Raio-X dos alunos e gera novos quizzes via IA. Já o Administrador tem governança plena: cadastra, edita, exclui ou promove colaboradores e gestores, além de auditar as conexões Keycloak e a base vetorial.',
  },
];

const filteredFaqList = computed(() => {
  return faqList.filter((item) => {
    const matchesCategory =
      selectedFaqCategory.value === 'todas' || item.category === selectedFaqCategory.value;
    const query = searchQuery.value.toLowerCase().trim();
    const matchesQuery =
      !query ||
      item.question.toLowerCase().includes(query) ||
      item.answer.toLowerCase().includes(query) ||
      item.categoryLabel.toLowerCase().includes(query);
    return matchesCategory && matchesQuery;
  });
});

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
    <section class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 class="text-lg font-bold text-slate-900 flex items-center space-x-2">
            <HelpCircle class="w-5 h-5 text-brand-600" />
            <span>Perguntas Frequentes & Como Funciona a Plataforma</span>
          </h2>
          <p class="text-xs text-slate-500 mt-1">
            Respostas detalhadas sobre a metodologia pedagógica, papéis de Colaborador e Gestor, quizzes e IA local.
          </p>
        </div>

        <!-- Barra de Busca Rápida -->
        <div class="relative w-full sm:w-72">
          <Search class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar dúvida ou termo..."
            class="w-full pl-9 pr-3.5 py-2 rounded-xl border border-slate-200 text-xs text-slate-800 placeholder:text-slate-400 focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
          />
        </div>
      </div>

      <!-- Abas de Categoria de Dúvidas -->
      <div class="flex flex-wrap gap-1.5 pt-1 border-b border-slate-100 pb-3">
        <button
          @click="selectedFaqCategory = 'todas'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'todas'
              ? 'bg-slate-900 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Todas ({{ faqList.length }})
        </button>
        <button
          @click="selectedFaqCategory = 'trilhas'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'trilhas'
              ? 'bg-brand-600 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Trilhas & Jornada
        </button>
        <button
          @click="selectedFaqCategory = 'gestao'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'gestao'
              ? 'bg-indigo-600 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Gestores & Raio-X
        </button>
        <button
          @click="selectedFaqCategory = 'quizzes'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'quizzes'
              ? 'bg-amber-600 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Quizzes & Notas
        </button>
        <button
          @click="selectedFaqCategory = 'ia'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'ia'
              ? 'bg-purple-600 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Tutor IA & Normas
        </button>
        <button
          @click="selectedFaqCategory = 'certificados'"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all cursor-pointer',
            selectedFaqCategory === 'certificados'
              ? 'bg-emerald-600 text-white shadow-2xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          Prazos & Certificados
        </button>
      </div>

      <!-- Lista de Perguntas (Acordeão) -->
      <div v-if="filteredFaqList.length > 0" class="space-y-3 pt-1">
        <div
          v-for="(item, idx) in filteredFaqList"
          :key="idx"
          class="rounded-2xl border border-slate-200/80 overflow-hidden transition-colors"
        >
          <button
            @click="toggleFaq(idx)"
            class="w-full px-5 py-4 text-left flex items-start sm:items-center justify-between gap-3 text-xs sm:text-sm font-bold text-slate-800 hover:bg-slate-50 transition-colors cursor-pointer"
          >
            <div class="flex flex-col sm:flex-row sm:items-center gap-2">
              <span class="inline-block px-2 py-0.5 rounded text-[10px] font-bold uppercase bg-slate-100 text-slate-600 shrink-0">
                {{ item.categoryLabel }}
              </span>
              <span class="text-slate-900">{{ item.question }}</span>
            </div>
            <ChevronUp v-if="openFaqIndex === idx" class="w-4 h-4 text-slate-400 shrink-0 mt-0.5 sm:mt-0" />
            <ChevronDown v-else class="w-4 h-4 text-slate-400 shrink-0 mt-0.5 sm:mt-0" />
          </button>
          <div
            v-if="openFaqIndex === idx"
            class="px-5 pb-5 text-xs sm:text-sm text-slate-600 leading-relaxed bg-slate-50/50 border-t border-slate-100 animate-fade-in"
          >
            {{ item.answer }}
          </div>
        </div>
      </div>

      <div v-else class="p-8 text-center bg-slate-50 rounded-2xl border border-dashed border-slate-200 text-slate-500 text-xs">
        <p>Nenhuma pergunta encontrada para "<strong>{{ searchQuery }}</strong>".</p>
        <button
          @click="searchQuery = ''; selectedFaqCategory = 'todas'"
          class="mt-2 text-brand-600 hover:underline font-bold"
        >
          Limpar filtros de busca
        </button>
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
