<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useTrackStore } from '@/stores/tracks';
import { useQuizStore } from '@/stores/quizzes';
import ProgressBar from '@/components/common/ProgressBar.vue';
import {
  User,
  ShieldCheck,
  Award,
  Calendar,
  Mail,
  Building,
  LogOut,
  CheckCircle2,
  Lock,
  Printer,
  Sparkles,
  Palette,
  FileCheck,
  TrendingUp,
  X,
  Users,
  Check,
  Server,
  Activity,
  Layers,
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const trackStore = useTrackStore();
const quizStore = useQuizStore();

const isCertificateOpen = ref(false);
const isCoverPickerOpen = ref(false);

// Presets de Capa Customizável (Gradientes e Sólidos)
interface CoverOption {
  id: string;
  name: string;
  category: 'Gradiente' | 'Sólido';
  bgClass: string;
  previewClass: string;
}

const coverOptions: CoverOption[] = [
  {
    id: 'grad-coop',
    name: 'Esmeralda & Cooperativo',
    category: 'Gradiente',
    bgClass: 'bg-gradient-to-r from-emerald-900 via-brand-800 to-teal-900',
    previewClass: 'bg-gradient-to-r from-emerald-800 to-teal-700',
  },
  {
    id: 'grad-ocean',
    name: 'Azul Confiança & Oceano',
    category: 'Gradiente',
    bgClass: 'bg-gradient-to-r from-sky-900 via-blue-900 to-indigo-950',
    previewClass: 'bg-gradient-to-r from-sky-700 to-indigo-800',
  },
  {
    id: 'grad-purple',
    name: 'Índigo & Inovação',
    category: 'Gradiente',
    bgClass: 'bg-gradient-to-r from-indigo-900 via-purple-900 to-slate-950',
    previewClass: 'bg-gradient-to-r from-indigo-700 to-purple-800',
  },
  {
    id: 'grad-sunset',
    name: 'Pôr do Sol & Terracota',
    category: 'Gradiente',
    bgClass: 'bg-gradient-to-r from-amber-700 via-rose-800 to-purple-950',
    previewClass: 'bg-gradient-to-r from-amber-600 to-rose-700',
  },
  {
    id: 'solid-forest',
    name: 'Verde Floresta Institucional',
    category: 'Sólido',
    bgClass: 'bg-emerald-800',
    previewClass: 'bg-emerald-800',
  },
  {
    id: 'solid-navy',
    name: 'Azul Marinho Corporativo',
    category: 'Sólido',
    bgClass: 'bg-slate-900',
    previewClass: 'bg-slate-900',
  },
  {
    id: 'solid-graphite',
    name: 'Grafite Minimalista',
    category: 'Sólido',
    bgClass: 'bg-zinc-800',
    previewClass: 'bg-zinc-800',
  },
];

const savedCover = localStorage.getItem(`coop_profile_cover_${authStore.currentUser.id}`);
const selectedCoverId = ref(
  savedCover || (authStore.isAdmin ? 'grad-purple' : authStore.isGestor ? 'grad-ocean' : 'grad-coop')
);

const activeCover = computed(() => {
  return coverOptions.find((c) => c.id === selectedCoverId.value) || coverOptions[0];
});

function selectCover(coverId: string) {
  selectedCoverId.value = coverId;
  localStorage.setItem(`coop_profile_cover_${authStore.currentUser.id}`, coverId);
}

function handleLogout() {
  authStore.logout();
  router.push('/login');
}

function printCertificate() {
  window.print();
}

// Matrícula Limpa e Formatada
const userMatricula = computed(() => {
  const raw = authStore.currentUser.id.replace(/-/g, '').slice(0, 4).toUpperCase();
  return `#CP-2026-${raw || '0842'}`;
});

// Métricas Dinâmicas do Usuário Ativo
const completionRate = computed(() => trackStore.overallProgressPercent);
const completedLessons = computed(() => trackStore.completedCount);
const totalLessons = computed(() => trackStore.totalLessonsCount);

const certificateHash = computed(() => {
  return `COOP-CERT-2026-${userMatricula.value.replace('#', '')}-BACEN-9941`;
});
</script>

<template>
  <div class="space-y-8 pb-20 animate-fade-in w-full">
    <!-- 1. Profile Hero Banner -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
      <!-- Capa com degradê/sólido customizável -->
      <div :class="['h-44 sm:h-52 relative transition-all duration-500', activeCover.bgClass]">
        <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-white/10 via-transparent to-black/25 pointer-events-none"></div>

        <!-- Botão Customizar Capa -->
        <div class="absolute bottom-4 right-4 flex items-center space-x-2">
          <button
            @click="isCoverPickerOpen = !isCoverPickerOpen"
            class="px-3 py-1.5 rounded-xl bg-white/20 hover:bg-white/30 backdrop-blur-md text-white text-xs font-bold border border-white/30 flex items-center space-x-1.5 cursor-pointer shadow-xs transition-all"
            title="Escolha uma nova capa para o seu perfil"
          >
            <Palette class="w-3.5 h-3.5" />
            <span>Mudar Capa</span>
          </button>
        </div>
      </div>

      <!-- Popover de Seleção de Capa -->
      <div
        v-if="isCoverPickerOpen"
        class="p-4 bg-slate-900 text-white border-b border-slate-800 animate-fade-in flex flex-col sm:flex-row sm:items-center justify-between gap-4"
      >
        <div>
          <p class="text-xs font-bold uppercase tracking-wider text-slate-300">Escolha o Tema da Capa</p>
          <p class="text-[11px] text-slate-400">Selecione entre opções gradientes institucionais ou tons sólidos minimalistas.</p>
        </div>

        <div class="flex flex-wrap items-center gap-2">
          <button
            v-for="cover in coverOptions"
            :key="cover.id"
            @click="selectCover(cover.id)"
            :class="[
              'px-3 py-1.5 rounded-xl text-xs font-semibold flex items-center space-x-2 border transition-all cursor-pointer',
              selectedCoverId === cover.id
                ? 'bg-white text-slate-900 border-white shadow-sm font-bold scale-105'
                : 'bg-white/10 text-white border-white/20 hover:bg-white/20'
            ]"
          >
            <span :class="['w-3 h-3 rounded-full border border-white/50 shrink-0', cover.previewClass]"></span>
            <span>{{ cover.name }}</span>
            <Check v-if="selectedCoverId === cover.id" class="w-3.5 h-3.5 text-slate-900" />
          </button>
          <button
            @click="isCoverPickerOpen = false"
            class="p-1.5 rounded-lg text-slate-400 hover:text-white transition-colors cursor-pointer ml-1"
          >
            <X class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- User Info Strip (100% sobre fundo branco sem sobreposição indevida) -->
      <div class="px-6 sm:px-10 pb-8 pt-4 relative bg-white">
        <div class="flex flex-col sm:flex-row items-center sm:items-end justify-between gap-6">
          <div class="flex flex-col sm:flex-row items-center sm:items-end space-y-4 sm:space-y-0 sm:space-x-6 text-center sm:text-left">
            <!-- Avatar em destaque -->
            <div class="relative -mt-16 sm:-mt-20 shrink-0">
              <img
                :src="authStore.currentUser.avatarUrl"
                :alt="authStore.currentUser.name"
                class="w-28 h-28 sm:w-32 sm:h-32 rounded-3xl object-cover border-4 border-white shadow-xl shadow-slate-900/15 bg-white"
              />
              <span
                class="absolute bottom-1 right-1 w-6 h-6 rounded-xl bg-emerald-500 border-2 border-white flex items-center justify-center text-white text-[11px] shadow-sm"
                title="Status Ativo"
              >
                ✓
              </span>
            </div>

            <!-- Identificação do Usuário (Texto nítido) -->
            <div class="pt-1">
              <div class="flex flex-wrap items-center justify-center sm:justify-start gap-2.5">
                <h1 class="text-2xl sm:text-3xl font-extrabold text-slate-900 font-sans tracking-tight">
                  {{ authStore.currentUser.name }}
                </h1>
                <span :class="['px-2.5 py-0.5 rounded-lg text-[11px] font-bold border uppercase tracking-wider', authStore.roleBadge.bg]">
                  {{ authStore.roleBadge.label }}
                </span>
              </div>
              <p class="text-xs sm:text-sm text-slate-600 font-medium mt-1">
                {{ authStore.currentUser.department }} · Matrícula: <strong class="text-slate-800">{{ userMatricula }}</strong>
              </p>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center space-x-3 shrink-0">
            <button
              v-if="authStore.isColaborador"
              @click="isCertificateOpen = true"
              class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs font-bold text-brand-900 bg-brand-50 hover:bg-brand-100 border border-brand-200/80 transition-all cursor-pointer shadow-2xs"
            >
              <Award class="w-4 h-4 text-brand-700" />
              <span>Ver Certificado</span>
            </button>

            <button
              @click="handleLogout"
              class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs font-bold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 transition-colors cursor-pointer"
            >
              <LogOut class="w-4 h-4" />
              <span>Sair da Sessão</span>
            </button>
          </div>
        </div>

        <!-- Dados Cadastrais Detalhados -->
        <div class="grid grid-cols-1 sm:grid-cols-4 gap-3 pt-6 mt-6 border-t border-slate-100 text-xs">
          <div class="p-3.5 rounded-2xl bg-slate-50/80 border border-slate-200/60 flex items-center space-x-3">
            <Mail class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 uppercase font-semibold">E-mail Corporativo</p>
              <p class="font-bold text-slate-800 truncate">{{ authStore.currentUser.email }}</p>
            </div>
          </div>

          <div class="p-3.5 rounded-2xl bg-slate-50/80 border border-slate-200/60 flex items-center space-x-3">
            <Building class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 uppercase font-semibold">Lotação / Área</p>
              <p class="font-bold text-slate-800 truncate">{{ authStore.currentUser.department }}</p>
            </div>
          </div>

          <div class="p-3.5 rounded-2xl bg-slate-50/80 border border-slate-200/60 flex items-center space-x-3">
            <Calendar class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 uppercase font-semibold">Data de Ingresso</p>
              <p class="font-bold text-slate-800">{{ authStore.currentUser.joinDate }}</p>
            </div>
          </div>

          <div class="p-3.5 rounded-2xl bg-slate-50/80 border border-slate-200/60 flex items-center space-x-3">
            <ShieldCheck class="w-4 h-4 text-emerald-500 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 uppercase font-semibold">Conformidade Regulatória</p>
              <p class="font-bold text-emerald-700">Homologado</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 2. KPIs Reativos e Adaptados por Papel -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- CASO A: COLABORADOR -->
      <template v-if="authStore.isColaborador">
        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Progresso na Trilha</span>
            <TrendingUp class="w-4 h-4 text-teal-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">{{ completionRate }}%</span>
            <span class="text-xs text-slate-400 font-medium">concluído</span>
          </div>
          <ProgressBar :value="completionRate" variant="success" class="mt-3" />
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Lições Realizadas</span>
            <FileCheck class="w-4 h-4 text-brand-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">{{ completedLessons }}</span>
            <span class="text-xs text-slate-400 font-medium">de {{ totalLessons }} lições</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Roteiro regulatório obrigatório</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Quizzes & Avaliações</span>
            <Award class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-indigo-700">{{ quizStore.averageQuizScore }}%</span>
            <span class="text-xs text-slate-400 font-medium">média de acerto</span>
          </div>
          <p class="text-[11px] text-indigo-700 font-semibold mt-3">
            {{ quizStore.completedQuizzesCount }} de {{ quizStore.quizzes.length }} avaliações feitas
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Prazo Restante (SLA)</span>
            <Calendar class="w-4 h-4 text-amber-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">14 dias</span>
            <span class="text-xs text-emerald-600 font-semibold">Início do prazo</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">SLA regulatório padrão</p>
        </div>
      </template>

      <!-- CASO B: GESTOR DE TURMA -->
      <template v-else-if="authStore.isGestor">
        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Turma Supervisionada</span>
            <Users class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">6</span>
            <span class="text-xs text-slate-400 font-medium">integrantes</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Supervisão pedagógica ativa</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Aproveitamento Médio</span>
            <Award class="w-4 h-4 text-emerald-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-emerald-700">92%</span>
            <span class="text-xs text-emerald-600 font-semibold">Institucional</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Acima da média regulatória</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Conformidade de SLA</span>
            <Calendar class="w-4 h-4 text-brand-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">100%</span>
            <span class="text-xs text-emerald-600 font-semibold">No prazo</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Sem colaboradores bloqueados</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Banco de Quizzes</span>
            <Layers class="w-4 h-4 text-purple-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-purple-700">{{ quizStore.quizzes.length }}</span>
            <span class="text-xs text-slate-400 font-medium">avaliações</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Modo consulta e gabarito comentado</p>
        </div>
      </template>

      <!-- CASO C: ADMINISTRADOR -->
      <template v-else>
        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Contas sob Governança</span>
            <Users class="w-4 h-4 text-rose-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900">6</span>
            <span class="text-xs text-slate-400 font-medium">perfis</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Governança RBAC centralizada</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Saúde do Sistema</span>
            <Activity class="w-4 h-4 text-emerald-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-emerald-700">100%</span>
            <span class="text-xs text-emerald-600 font-semibold">Operacional</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Spring Boot + Postgres + pgvector</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Base Vetorial RAG</span>
            <Server class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-indigo-700">Ativa</span>
            <span class="text-xs text-slate-400 font-medium">pgvector</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Indexação de normativos locais</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
            <span class="font-semibold uppercase tracking-wider text-[10px]">Segurança & Auditoria</span>
            <ShieldCheck class="w-4 h-4 text-teal-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-teal-700">Conforme</span>
            <span class="text-xs text-slate-400 font-medium">BACEN / LGPD</span>
          </div>
          <p class="text-[11px] text-slate-500 mt-3">Auditoria contínua de acessos</p>
        </div>
      </template>
    </div>

    <!-- 3. Badges e Conquistas Institucionais -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs p-6 sm:p-8">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-slate-100 mb-6">
        <div>
          <h2 class="text-base font-bold text-slate-900 flex items-center gap-2">
            <Award class="w-5 h-5 text-brand-600" />
            <span>Badges & Competências Institucionais</span>
          </h2>
          <p class="text-xs text-slate-500 mt-0.5">Insígnias concedidas de acordo com as trilhas e atribuições da sua carreira.</p>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Badge 1 -->
        <div class="p-4 rounded-2xl bg-teal-50/70 border border-teal-200 flex flex-col justify-between">
          <div>
            <div class="w-10 h-10 rounded-xl bg-teal-600 text-white flex items-center justify-center font-bold shadow-xs mb-3">
              <CheckCircle2 class="w-5 h-5" />
            </div>
            <h3 class="text-xs font-bold text-teal-950">Doutrina Cooperativa</h3>
            <p class="text-[11px] text-teal-800 mt-1 leading-snug">
              Compreensão integral dos 7 Princípios da ACI e da Lei 5.764/71.
            </p>
          </div>
          <span class="mt-4 text-[10px] font-bold text-teal-700 bg-teal-100/80 px-2 py-0.5 rounded self-start">
            Reconhecido
          </span>
        </div>

        <!-- Badge 2 -->
        <div class="p-4 rounded-2xl bg-indigo-50/70 border border-indigo-200 flex flex-col justify-between">
          <div>
            <div class="w-10 h-10 rounded-xl bg-indigo-600 text-white flex items-center justify-center font-bold shadow-xs mb-3">
              <ShieldCheck class="w-5 h-5" />
            </div>
            <h3 class="text-xs font-bold text-indigo-950">Segurança & Ciberdefesa</h3>
            <p class="text-[11px] text-indigo-800 mt-1 leading-snug">
              Conformidade com a Resolução CMN nº 4.893 e boas práticas de sigilo.
            </p>
          </div>
          <span class="mt-4 text-[10px] font-bold text-indigo-700 bg-indigo-100/80 px-2 py-0.5 rounded self-start">
            Reconhecido
          </span>
        </div>

        <!-- Badge 3 -->
        <div class="p-4 rounded-2xl bg-brand-50/70 border border-brand-200 flex flex-col justify-between">
          <div>
            <div class="w-10 h-10 rounded-xl bg-brand-700 text-white flex items-center justify-center font-bold shadow-xs mb-3">
              <Sparkles class="w-5 h-5" />
            </div>
            <h3 class="text-xs font-bold text-brand-950">Inovação em IA Cooperativa</h3>
            <p class="text-[11px] text-brand-800 mt-1 leading-snug">
              Utilização ativa do Tutor Virtual de IA com busca semântica em normas.
            </p>
          </div>
          <span class="mt-4 text-[10px] font-bold text-brand-700 bg-brand-100/80 px-2 py-0.5 rounded self-start">
            Reconhecido
          </span>
        </div>

        <!-- Badge 4 -->
        <div class="p-4 rounded-2xl bg-slate-50 border border-slate-200 flex flex-col justify-between">
          <div>
            <div class="w-10 h-10 rounded-xl bg-slate-800 text-white flex items-center justify-center font-bold shadow-xs mb-3">
              <Award class="w-5 h-5" />
            </div>
            <h3 class="text-xs font-bold text-slate-800">Governança Cooperativa</h3>
            <p class="text-[11px] text-slate-500 mt-1 leading-snug">
              Alinhamento com assembleias gerais e órgãos estatutários fiscalizatórios.
            </p>
          </div>
          <span class="mt-4 text-[10px] font-bold text-slate-700 bg-slate-200 px-2 py-0.5 rounded self-start">
            Ativo
          </span>
        </div>
      </div>
    </div>

    <!-- 4. Modal Oficial de Certificado Digital (Disponível para Colaborador) -->
    <div
      v-if="isCertificateOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-3xl w-full overflow-hidden flex flex-col max-h-[95vh]">
        <!-- Top Toolbar -->
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50">
          <div class="flex items-center space-x-2">
            <Award class="w-5 h-5 text-brand-600" />
            <h3 class="text-sm font-extrabold text-slate-900">Certificado Oficial de Formação Cooperativa</h3>
          </div>
          <div class="flex items-center space-x-2">
            <button
              @click="printCertificate"
              class="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-bold text-slate-700 bg-white hover:bg-slate-100 border border-slate-200 transition-colors cursor-pointer"
            >
              <Printer class="w-3.5 h-3.5" />
              <span>Imprimir</span>
            </button>
            <button
              @click="isCertificateOpen = false"
              class="text-slate-400 hover:text-slate-600 p-1.5 rounded-xl hover:bg-slate-200 transition-colors cursor-pointer"
            >
              <X class="w-5 h-5" />
            </button>
          </div>
        </div>

        <!-- Certificate Body Styled as Real Document -->
        <div class="p-8 sm:p-12 overflow-y-auto bg-slate-50/50 flex justify-center">
          <div class="w-full max-w-2xl bg-white border-8 border-double border-brand-800/40 p-8 sm:p-10 rounded-2xl shadow-lg relative text-center space-y-6">
            <!-- Emblema Superior -->
            <div class="flex flex-col items-center justify-center space-y-2">
              <div class="w-14 h-14 rounded-2xl bg-brand-800 text-white flex items-center justify-center shadow-md">
                <svg viewBox="0 0 24 24" fill="none" class="w-8 h-8 stroke-current stroke-2">
                  <path d="M12 2L3 7v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V7l-9-5z" />
                  <path d="M12 7l-4 8h8l-4-8z" />
                </svg>
              </div>
              <h2 class="text-xs font-black uppercase tracking-widest text-brand-900">
                COOPERATIVA DE CRÉDITO INTEGRADA · SISTEMA NACIONAL
              </h2>
            </div>

            <!-- Título do Certificado -->
            <div class="space-y-1">
              <h3 class="text-2xl sm:text-3xl font-serif font-black text-slate-900 tracking-tight">
                CERTIFICADO DE QUALIFICAÇÃO
              </h3>
              <p class="text-xs text-slate-500 font-sans uppercase tracking-widest">
                Programa de Integração e Conformidade Regulatória 2026
              </p>
            </div>

            <!-- Texto de Concessão -->
            <p class="text-xs sm:text-sm text-slate-700 leading-relaxed font-serif max-w-xl mx-auto pt-2">
              Certificamos que <strong class="text-slate-950 font-sans text-base">{{ authStore.currentUser.name }}</strong>,
              lotação <strong class="text-slate-950 font-sans">{{ authStore.currentUser.department }}</strong>,
              concluiu com êxito o programa institucional de onboarding cooperativista, cumprindo com distinção a grade curricular obrigatória,
              as normativas do Conselho Monetário Nacional (CMN/BACEN) e os testes de fixação com aproveitamento superior à média exigida.
            </p>

            <!-- Detalhes e Carga Horária -->
            <div class="pt-4 grid grid-cols-2 gap-4 text-xs border-t border-slate-100 max-w-md mx-auto">
              <div>
                <p class="text-[10px] text-slate-400 font-semibold uppercase">Carga Horária</p>
                <p class="font-bold text-slate-800">40 Horas / Aula</p>
              </div>
              <div>
                <p class="text-[10px] text-slate-400 font-semibold uppercase">Data de Emissão</p>
                <p class="font-bold text-slate-800">26 de Setembro de 2026</p>
              </div>
            </div>

            <!-- Assinaturas e Código Hash -->
            <div class="pt-6 grid grid-cols-2 gap-8 items-end max-w-md mx-auto">
              <div class="border-t border-slate-300 pt-2 text-center">
                <p class="text-[11px] font-bold text-slate-900">Roberto Mendes</p>
                <p class="text-[9px] text-slate-500">Diretoria de Gente & Gestão</p>
              </div>
              <div class="border-t border-slate-300 pt-2 text-center">
                <p class="text-[11px] font-bold text-slate-900">Conselho de Administração</p>
                <p class="text-[9px] text-slate-500">Governança Cooperativa</p>
              </div>
            </div>

            <!-- Hash de Autenticidade -->
            <div class="pt-4 flex items-center justify-between text-[9px] text-slate-400 border-t border-slate-100 font-mono">
              <span>Chave de Validação: {{ certificateHash }}</span>
              <span class="text-emerald-700 font-bold font-sans">✓ Registro Válido</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
