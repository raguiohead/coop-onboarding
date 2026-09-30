<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import QuizGeneratorModal from '@/components/ai/QuizGeneratorModal.vue';
import {
  Users,
  TrendingUp,
  AlertTriangle,
  Award,
  Sparkles,
  Calendar,
  CheckCircle2,
  Clock,
  ArrowUpRight,
  ShieldAlert,
  Plus,
  Pencil,
  Trash2,
  UserPlus,
  Shield,
  X,
  Check,
  Eye,
  Send,
  BookOpen,
} from 'lucide-vue-next';

export interface MemberLessonStep {
  title: string;
  module: string;
  duration: string;
  completed: boolean;
  completedAt?: string;
}

export interface TeamMember {
  id: string;
  name: string;
  email: string;
  role: 'COLABORADOR' | 'GESTOR' | 'ADMIN';
  department: string;
  trackTitle: string;
  currentModule: string;
  currentLesson: string;
  completedCount: number;
  totalCount: number;
  progress: number;
  slaDaysLeft: number;
  status: 'NO_PRAZO' | 'ALERTA' | 'CONCLUIDO';
  avatar: string;
  quizzesCompleted: string;
  quizScoreAverage: string;
  lastActive: string;
  lessons: MemberLessonStep[];
}

const defaultMembers: TeamMember[] = [
  {
    id: '6cc3d873-5688-4063-8d52-e88c8421488b',
    name: 'Lucas Almeida',
    email: 'lucas.colaborador@coop.local',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    trackTitle: 'Cultura & Governança Cooperativista',
    currentModule: 'Módulo 1: Princípios e História',
    currentLesson: 'Lição 1.1: Origens em Rochdale e os 7 Princípios da ACI',
    completedCount: 0,
    totalCount: 6,
    progress: 0,
    slaDaysLeft: 14,
    status: 'NO_PRAZO',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '0 de 3',
    quizScoreAverage: 'Pendente',
    lastActive: 'Aguardando Início',
    lessons: [
      { module: 'Módulo 1: Princípios e História', title: 'Origens em Rochdale e os 7 Princípios da ACI', duration: '12 min', completed: false },
      { module: 'Módulo 1: Princípios e História', title: 'A Legislação Cooperativista Brasileira (Lei 5.764/71)', duration: '18 min', completed: false },
      { module: 'Módulo 1: Princípios e História', title: 'Cooperativas de Crédito x Bancos Comerciais', duration: '15 min', completed: false },
      { module: 'Módulo 2: Governança Cooperativa', title: 'Estrutura da Assembleia Geral Ordinária (AGO)', duration: '20 min', completed: false },
      { module: 'Módulo 2: Governança Cooperativa', title: 'Atos Cooperativos e Não Cooperativos na Prática', duration: '15 min', completed: false },
      { module: 'Módulo 2: Governança Cooperativa', title: 'O Papel dos Conselhos de Administração e Fiscal', duration: '25 min', completed: false },
    ],
  },
  {
    id: '670a8cad-248b-41bb-86ff-adb72cab13cb',
    name: 'Mariana Ribeiro',
    email: 'mariana.gestora@coop.local',
    role: 'GESTOR',
    department: 'Desenvolvimento Humano e Organizacional (DHO)',
    trackTitle: 'Supervisão de Onboarding & DHO',
    currentModule: 'Supervisão Ativa da Turma',
    currentLesson: 'Gestora Responsável pela Turma de Integração',
    completedCount: 6,
    totalCount: 6,
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: 'Supervisão Ativa',
    quizScoreAverage: '100% (Gabarito)',
    lastActive: 'Ativa agora',
    lessons: [],
  },
  {
    id: '07c89e60-4ea6-4ab0-ac0f-96c0fef51e88',
    name: 'Rodrigo Martins',
    email: 'rodrigo.admin@coop.local',
    role: 'ADMIN',
    department: 'Governança & TI',
    trackTitle: 'Governança & Segurança IAM',
    currentModule: 'Supervisão de Infraestrutura',
    currentLesson: 'Administrador do Sistema & Keycloak',
    completedCount: 6,
    totalCount: 6,
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: 'Supervisão Ativa',
    quizScoreAverage: '100%',
    lastActive: 'Ativo agora',
    lessons: [],
  },
];

const authStore = useAuthStore();
const isQuizModalOpen = ref(false);

// Purga de membros legados do LocalStorage
if (typeof window !== 'undefined') {
  const savedTeamRaw = localStorage.getItem('coop_team_members');
  if (savedTeamRaw) {
    try {
      const parsed = JSON.parse(savedTeamRaw) as TeamMember[];
      const validEmails = ['lucas.colaborador@coop.local', 'mariana.gestora@coop.local', 'rodrigo.admin@coop.local'];
      const hasOnlyValid = parsed.every((m) => validEmails.includes(m.email));
      if (!hasOnlyValid || parsed.length !== defaultMembers.length) {
        localStorage.removeItem('coop_team_members');
        localStorage.removeItem('coop_zeroed_v3');
      }
    } catch {
      localStorage.removeItem('coop_team_members');
    }
  }
}

// Persistência de Membros da Equipe no LocalStorage
const savedTeam = typeof window !== 'undefined' ? localStorage.getItem('coop_team_members') : null;
const teamMembers = ref<TeamMember[]>(savedTeam ? JSON.parse(savedTeam) : defaultMembers);

function saveTeamMembers() {
  localStorage.setItem('coop_team_members', JSON.stringify(teamMembers.value));
}

// Filtro por Papel na tabela
const selectedRoleFilter = ref<'TODOS' | 'COLABORADOR' | 'GESTOR' | 'ADMIN'>('TODOS');

const filteredMembers = computed(() => {
  if (selectedRoleFilter.value === 'TODOS') return teamMembers.value;
  return teamMembers.value.filter((m) => m.role === selectedRoleFilter.value);
});

// KPIs da Turma
const colaboradoresCount = computed(() => teamMembers.value.filter((m) => m.role === 'COLABORADOR').length);
const gestoresCount = computed(() => teamMembers.value.filter((m) => m.role === 'GESTOR').length);
const adminsCount = computed(() => teamMembers.value.filter((m) => m.role === 'ADMIN').length);
const averageColabProgress = computed(() => {
  const colabs = teamMembers.value.filter((m) => m.role === 'COLABORADOR');
  if (colabs.length === 0) return 0;
  const sum = colabs.reduce((acc, curr) => acc + curr.progress, 0);
  return Math.round(sum / colabs.length);
});
const alertsCount = computed(() => teamMembers.value.filter((m) => m.status === 'ALERTA').length);

// Modal de Criação / Edição pelo Administrador
const isMemberModalOpen = ref(false);
const editingMemberId = ref<string | null>(null);
const memberForm = ref({
  name: '',
  email: '',
  role: 'COLABORADOR' as 'COLABORADOR' | 'GESTOR' | 'ADMIN',
  department: '',
  trackTitle: 'Cultura & Governança Cooperativista',
  progress: 0,
  slaDaysLeft: 14,
  status: 'NO_PRAZO' as 'NO_PRAZO' | 'ALERTA' | 'CONCLUIDO',
});

function openCreateMemberModal() {
  editingMemberId.value = null;
  memberForm.value = {
    name: '',
    email: '',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    trackTitle: 'Cultura & Governança Cooperativista',
    progress: 0,
    slaDaysLeft: 14,
    status: 'NO_PRAZO',
  };
  isMemberModalOpen.value = true;
}

function openEditMemberModal(member: TeamMember) {
  editingMemberId.value = member.id;
  memberForm.value = {
    name: member.name,
    email: member.email,
    role: member.role,
    department: member.department,
    trackTitle: member.trackTitle,
    progress: member.progress,
    slaDaysLeft: member.slaDaysLeft,
    status: member.status,
  };
  isMemberModalOpen.value = true;
}

function handleSaveMember() {
  if (!memberForm.value.name.trim() || !memberForm.value.email.trim()) {
    alert('Por favor, preencha o nome e o e-mail do membro.');
    return;
  }

  if (editingMemberId.value) {
    // Modo Edição
    const idx = teamMembers.value.findIndex((m) => m.id === editingMemberId.value);
    if (idx !== -1) {
      teamMembers.value[idx] = {
        ...teamMembers.value[idx],
        ...memberForm.value,
      };
      // Atualiza também se existir em authStore.profiles
      authStore.updateUser(editingMemberId.value, {
        name: memberForm.value.name,
        email: memberForm.value.email,
        role: memberForm.value.role,
        department: memberForm.value.department,
      });
    }
  } else {
    // Modo Criação
    const newId = `user-${Date.now()}`;
    const avatar = memberForm.value.role === 'GESTOR'
      ? 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80'
      : 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80';

    const newMember: TeamMember = {
      id: newId,
      name: memberForm.value.name,
      email: memberForm.value.email,
      role: memberForm.value.role,
      department: memberForm.value.department,
      trackTitle: memberForm.value.trackTitle,
      currentModule: 'Módulo 1: Introdução Institucional',
      currentLesson: 'Lição 1.1: Boas-vindas e Visão Geral',
      completedCount: 0,
      totalCount: 5,
      quizScoreAverage: '0%',
      lastActive: 'Cadastrado agora',
      lessons: [
        { module: 'Módulo 1: Introdução Institucional', title: 'Boas-vindas e Princípios Cooperativistas', duration: '15 min', completed: false },
        { module: 'Módulo 1: Introdução Institucional', title: 'Regulamentação e Governança Básica', duration: '20 min', completed: false },
      ],
      progress: memberForm.value.progress,
      slaDaysLeft: memberForm.value.slaDaysLeft,
      status: memberForm.value.status,
      avatar,
      quizzesCompleted: memberForm.value.role === 'GESTOR' ? 'Supervisão Ativa' : '0 de 3',
    };
    teamMembers.value.push(newMember);

    // Registra também no authStore para que possa ser simulado no switcher de perfis
    authStore.createUser({
      name: memberForm.value.name,
      email: memberForm.value.email,
      role: memberForm.value.role,
      department: memberForm.value.department,
      avatarUrl: avatar,
      joinDate: new Date().toLocaleDateString('pt-BR'),
    });
  }

  saveTeamMembers();
  isMemberModalOpen.value = false;
}

function handleDeleteMember(memberId: string) {
  const member = teamMembers.value.find((m) => m.id === memberId);
  if (!member) return;

  if (confirm(`Tem certeza que deseja remover ${member.name} (${member.role}) do ecossistema?`)) {
    teamMembers.value = teamMembers.value.filter((m) => m.id !== memberId);
    saveTeamMembers();
    authStore.deleteUser(memberId);
  }
}

// Modal de Raio-X de Aprendizagem (Gestor e Admin)
const isRaioXModalOpen = ref(false);
const selectedRaioXMember = ref<TeamMember | null>(null);
const reminderFeedback = ref<string | null>(null);

function openRaioXModal(member: TeamMember) {
  selectedRaioXMember.value = member;
  reminderFeedback.value = null;
  isRaioXModalOpen.value = true;
}

function sendSupportReminder(member: TeamMember) {
  reminderFeedback.value = `Lembrete pedagógico enviado com sucesso para ${member.name} (${member.email})!`;
  setTimeout(() => {
    reminderFeedback.value = null;
  }, 4000);
}
</script>

<template>
  <div class="space-y-8 pb-16">
    <!-- Non-manager warning banner if accessed as colaborador -->
    <div
      v-if="!authStore.isGestor && !authStore.isAdmin"
      class="p-5 rounded-2xl bg-amber-50 border border-amber-200 text-amber-900 flex items-start space-x-3"
    >
      <ShieldAlert class="w-6 h-6 text-amber-600 shrink-0 mt-0.5" />
      <div>
        <h3 class="text-sm font-bold">Modo de Visualização para Colaborador</h3>
        <p class="text-xs text-amber-700 mt-0.5">
          Esta área foi projetada para <strong>Gestores de Equipe</strong> e <strong>Administradores</strong>. Você pode simular o perfil de <strong>Roberto Mendes (Gestor)</strong> ou <strong>Mariana Duarte (Admin)</strong> no menu superior para ter acesso completo de edição e aprovações.
        </p>
      </div>
    </div>

    <!-- Header Banner -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gradient-to-r from-slate-900 via-indigo-950 to-brand-950 p-6 sm:p-8 rounded-3xl text-white shadow-xl border border-slate-700/50">
      <div>
        <div class="inline-flex items-center space-x-2 px-2.5 py-1 rounded-md text-[11px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 uppercase tracking-wider mb-2">
          <Users class="w-3.5 h-3.5" />
          <span>Liderança & Governança de Talentos</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight font-sans">
          Cockpit de Gestão da Turma & Usuários
        </h1>
        <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-2xl">
          <span v-if="authStore.isAdmin">
            Controle de Governança Total: Cadastre, edite e promova colaboradores e gestores de equipe, além de gerenciar turmas e aprovações.
          </span>
          <span v-else>
            Monitore em tempo real o engajamento, cumprimento de SLAs e aprovações nos testes pedagógicos da sua turma de integração.
          </span>
        </p>
      </div>

      <div class="shrink-0 flex flex-wrap items-center gap-2">
        <!-- Botão Novo Membro (Exclusivo para Admin) -->
        <button
          v-if="authStore.isAdmin"
          @click="openCreateMemberModal"
          class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-rose-600 hover:bg-rose-700 text-white shadow-md shadow-rose-600/25 transition-all hover:scale-[1.02] cursor-pointer"
        >
          <UserPlus class="w-4 h-4" />
          <span>Novo Membro</span>
        </button>

        <!-- Botão Gerar Quiz com IA (para Gestor e Admin) -->
        <button
          @click="isQuizModalOpen = true"
          class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/25 transition-all hover:scale-[1.02] cursor-pointer"
        >
          <Sparkles class="w-4 h-4 text-ai-100" />
          <span>Criar Quiz com IA</span>
        </button>
      </div>
    </div>

    <!-- KPI Summary Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Colaboradores em Formação</span>
          <Users class="w-4 h-4 text-indigo-600" />
        </div>
        <p class="text-2xl font-black text-slate-900">{{ colaboradoresCount }} Membros</p>
        <p class="text-[11px] text-teal-600 font-semibold mt-1 flex items-center gap-1">
          <ArrowUpRight class="w-3 h-3" /> {{ gestoresCount }} Gestores Ativos
        </p>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Conclusão Média das Trilhas</span>
          <TrendingUp class="w-4 h-4 text-teal-600" />
        </div>
        <p class="text-2xl font-black text-slate-900">{{ averageColabProgress }}%</p>
        <p class="text-[11px] text-slate-400 mt-1">Meta corporativa: 80% em 14 dias</p>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Alertas de SLA em Risco</span>
          <AlertTriangle class="w-4 h-4 text-amber-500" />
        </div>
        <p class="text-2xl font-black text-amber-600">{{ alertsCount }} Alerta{{ alertsCount !== 1 ? 's' : '' }}</p>
        <p class="text-[11px] text-amber-700 font-semibold mt-1">Acompanhamento prioritário</p>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Aproveitamento nos Quizzes</span>
          <Award class="w-4 h-4 text-indigo-600" />
        </div>
        <p class="text-2xl font-black text-indigo-700">92%</p>
        <p class="text-[11px] text-teal-600 font-semibold mt-1">Acima da média institucional</p>
      </div>
    </div>

    <!-- Filtros de Papel e Tabela -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
      <div class="px-6 py-5 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 class="text-base font-bold text-slate-900">Membros da Turma e Equipe</h3>
          <p class="text-xs text-slate-500">
            Acompanhamento contínuo dos colaboradores em formação e gestores vinculados.
          </p>
        </div>

        <!-- Abas de Filtro -->
        <div class="flex items-center space-x-1.5 bg-slate-100 p-1 rounded-xl">
          <button
            @click="selectedRoleFilter = 'TODOS'"
            :class="[
              'px-3 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer',
              selectedRoleFilter === 'TODOS' ? 'bg-white text-slate-900 shadow-2xs' : 'text-slate-500 hover:text-slate-700'
            ]"
          >
            Todos ({{ teamMembers.length }})
          </button>
          <button
            @click="selectedRoleFilter = 'COLABORADOR'"
            :class="[
              'px-3 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer',
              selectedRoleFilter === 'COLABORADOR' ? 'bg-white text-slate-900 shadow-2xs' : 'text-slate-500 hover:text-slate-700'
            ]"
          >
            Colaboradores ({{ colaboradoresCount }})
          </button>
          <button
            @click="selectedRoleFilter = 'GESTOR'"
            :class="[
              'px-3 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer',
              selectedRoleFilter === 'GESTOR' ? 'bg-white text-slate-900 shadow-2xs' : 'text-slate-500 hover:text-slate-700'
            ]"
          >
            Gestores ({{ gestoresCount }})
          </button>
          <button
            @click="selectedRoleFilter = 'ADMIN'"
            :class="[
              'px-3 py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer',
              selectedRoleFilter === 'ADMIN' ? 'bg-white text-slate-900 shadow-2xs' : 'text-slate-500 hover:text-slate-700'
            ]"
          >
            Administradores ({{ adminsCount }})
          </button>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/80 text-[11px] font-bold uppercase tracking-wider text-slate-400 border-b border-slate-100">
              <th class="px-6 py-3.5 whitespace-nowrap">Profissional</th>
              <th class="px-6 py-3.5 whitespace-nowrap">Papel</th>
              <th class="px-6 py-3.5 whitespace-nowrap">Trilha Atribuída & Onde Está</th>
              <th class="px-6 py-3.5 whitespace-nowrap">Progresso</th>
              <th class="px-6 py-3.5 whitespace-nowrap">SLA Restante</th>
              <th class="px-6 py-3.5 whitespace-nowrap">Quizzes</th>
              <th class="px-6 py-3.5 whitespace-nowrap">Status</th>
              <th class="px-6 py-3.5 text-center whitespace-nowrap">Raio-X</th>
              <th v-if="authStore.isAdmin" class="px-6 py-3.5 text-right whitespace-nowrap">Ações Admin</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 text-xs">
            <tr v-for="member in filteredMembers" :key="member.id" class="hover:bg-slate-50/60 transition-colors">
              <td class="px-6 py-4 flex items-center space-x-3 whitespace-nowrap">
                <img :src="member.avatar" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
                <div>
                  <p class="font-bold text-slate-900">{{ member.name }}</p>
                  <p class="text-[11px] text-slate-500">{{ member.email }} · {{ member.department }}</p>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-0.5 rounded text-[10px] font-bold uppercase border whitespace-nowrap',
                    member.role === 'ADMIN'
                      ? 'bg-rose-50 text-rose-700 border-rose-200'
                      : member.role === 'GESTOR'
                      ? 'bg-indigo-50 text-indigo-700 border-indigo-200'
                      : 'bg-teal-50 text-teal-700 border-teal-200'
                  ]"
                >
                  {{ member.role }}
                </span>
              </td>
              <td class="px-6 py-4">
                <p class="text-slate-800 font-bold leading-tight">{{ member.trackTitle }}</p>
                <div v-if="member.role === 'COLABORADOR'" class="mt-1.5 flex items-center space-x-1.5">
                  <span
                    class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-lg text-[10px] font-semibold border max-w-[280px] whitespace-nowrap"
                    :class="[
                      member.status === 'CONCLUIDO'
                        ? 'bg-teal-50 text-teal-800 border-teal-200'
                        : member.status === 'ALERTA'
                        ? 'bg-rose-50 text-rose-800 border-rose-200'
                        : 'bg-indigo-50/70 text-indigo-900 border-indigo-200/60'
                    ]"
                  >
                    <span
                      class="w-1.5 h-1.5 rounded-full shrink-0"
                      :class="member.status === 'CONCLUIDO' ? 'bg-teal-500' : member.status === 'ALERTA' ? 'bg-rose-500 animate-pulse' : 'bg-indigo-500 animate-pulse'"
                    ></span>
                    <span class="truncate" :title="member.currentLesson">
                      {{ member.currentLesson }}
                    </span>
                  </span>
                </div>
                <p v-else class="text-[11px] text-slate-400 mt-0.5">Visão de Governança & Supervisão</p>
              </td>
              <td class="px-6 py-4 w-44 whitespace-nowrap">
                <div v-if="member.role === 'COLABORADOR'" class="flex items-center space-x-2">
                  <div class="flex-1 bg-slate-100 h-2 rounded-full overflow-hidden">
                    <div class="bg-brand-600 h-full rounded-full transition-all" :style="{ width: `${member.progress}%` }"></div>
                  </div>
                  <span class="font-bold text-slate-700 text-[11px]">{{ member.progress }}%</span>
                </div>
                <span v-else class="text-[11px] font-semibold text-slate-400">
                  Liderança Pedagógica & TI
                </span>
              </td>
              <td class="px-6 py-4 font-medium text-slate-600 whitespace-nowrap">
                <span v-if="member.role === 'GESTOR' || member.role === 'ADMIN'" class="text-slate-400 text-[11px]">
                  Permanente
                </span>
                <span v-else-if="member.status === 'CONCLUIDO'" class="text-teal-700 font-semibold flex items-center gap-1">
                  <CheckCircle2 class="w-3.5 h-3.5" /> Concluído
                </span>
                <span v-else-if="member.status === 'ALERTA'" class="text-rose-600 font-bold flex items-center gap-1">
                  <AlertTriangle class="w-3.5 h-3.5" /> {{ member.slaDaysLeft }} dias
                </span>
                <span v-else class="text-slate-600">
                  {{ member.slaDaysLeft }} dias
                </span>
              </td>
              <td class="px-6 py-4 font-semibold text-slate-700 whitespace-nowrap">
                {{ member.quizzesCompleted }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2.5 py-1 rounded-md text-[10px] font-bold uppercase border whitespace-nowrap inline-flex items-center gap-1',
                    member.status === 'CONCLUIDO'
                      ? 'bg-teal-50 text-teal-700 border-teal-200'
                      : member.status === 'ALERTA'
                      ? 'bg-rose-50 text-rose-700 border-rose-200'
                      : 'bg-indigo-50 text-indigo-700 border-indigo-200'
                  ]"
                >
                  {{ member.status === 'CONCLUIDO' ? 'Finalizado' : member.status === 'ALERTA' ? 'SLA em Risco' : 'Em Andamento' }}
                </span>
              </td>

              <!-- Raio-X Detalhado -->
              <td class="px-6 py-4 text-center whitespace-nowrap">
                <button
                  v-if="member.role === 'COLABORADOR'"
                  @click="openRaioXModal(member)"
                  class="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-bold text-indigo-700 bg-indigo-50 hover:bg-indigo-100 border border-indigo-200/80 transition-all cursor-pointer shadow-2xs whitespace-nowrap"
                  title="Consultar exatamente quais lições e módulos este colaborador concluiu"
                >
                  <Eye class="w-3.5 h-3.5" />
                  <span>Raio-X</span>
                </button>
                <span v-else class="text-[10px] text-slate-400 font-semibold uppercase">Gestão</span>
              </td>

              <!-- Coluna de Ações Admin -->
              <td v-if="authStore.isAdmin" class="px-6 py-4 text-right whitespace-nowrap">
                <div class="flex items-center justify-end space-x-2">
                  <button
                    @click="openEditMemberModal(member)"
                    class="p-1.5 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 transition-colors cursor-pointer"
                    title="Editar informações do membro"
                  >
                    <Pencil class="w-4 h-4" />
                  </button>
                  <button
                    @click="handleDeleteMember(member.id)"
                    class="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors cursor-pointer"
                    title="Excluir membro"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal de Criação / Edição de Membros (Exclusivo Admin) -->
    <div
      v-if="isMemberModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-lg w-full overflow-hidden">
        <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <div class="flex items-center space-x-2.5">
            <div class="w-8 h-8 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center font-bold">
              <Shield class="w-4 h-4" />
            </div>
            <div>
              <h3 class="text-base font-bold text-slate-900">
                {{ editingMemberId ? 'Editar Membro da Equipe' : 'Cadastrar Novo Membro' }}
              </h3>
              <p class="text-[11px] text-slate-400">Poder Administrativo de Gestão de Identidades</p>
            </div>
          </div>
          <button
            @click="isMemberModalOpen = false"
            class="text-slate-400 hover:text-slate-600 p-1.5 rounded-lg hover:bg-slate-100 transition-colors cursor-pointer"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="handleSaveMember" class="p-6 space-y-4 text-xs">
          <!-- Nome -->
          <div>
            <label class="block font-bold text-slate-700 mb-1">Nome Completo:</label>
            <input
              v-model="memberForm.name"
              type="text"
              required
              placeholder="Ex: Gabriel Monteiro"
              class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block font-bold text-slate-700 mb-1">E-mail Institucional:</label>
            <input
              v-model="memberForm.email"
              type="email"
              required
              placeholder="Ex: gabriel.monteiro@coop.local"
              class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <!-- Papel RBAC -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">Papel (Role RBAC):</label>
              <select
                v-model="memberForm.role"
                class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              >
                <option value="COLABORADOR">Colaborador em Formação</option>
                <option value="GESTOR">Gestor de Equipe</option>
                <option value="ADMIN">Administrador TI & Governança</option>
              </select>
            </div>

            <div>
              <label class="block font-bold text-slate-700 mb-1">Status Operacional:</label>
              <select
                v-model="memberForm.status"
                class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              >
                <option value="NO_PRAZO">Em Andamento (No Prazo)</option>
                <option value="ALERTA">Alerta de SLA</option>
                <option value="CONCLUIDO">Concluído / Certificado</option>
              </select>
            </div>
          </div>

          <!-- Departamento -->
          <div>
            <label class="block font-bold text-slate-700 mb-1">Área / Departamento:</label>
            <input
              v-model="memberForm.department"
              type="text"
              required
              placeholder="Ex: Atendimento, Riscos, TI, RH..."
              class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <!-- Trilha Atribuída -->
          <div>
            <label class="block font-bold text-slate-700 mb-1">Trilha de Aprendizagem:</label>
            <input
              v-model="memberForm.trackTitle"
              type="text"
              placeholder="Ex: Cultura & Governança Cooperativista"
              class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
            />
          </div>

          <!-- Progresso e SLA -->
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block font-bold text-slate-700 mb-1">Progresso (%):</label>
              <input
                v-model.number="memberForm.progress"
                type="number"
                min="0"
                max="100"
                class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Dias de SLA Restantes:</label>
              <input
                v-model.number="memberForm.slaDaysLeft"
                type="number"
                min="0"
                class="w-full px-3 py-2 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-rose-500 focus:outline-none"
              />
            </div>
          </div>

          <!-- Botões de Ação do Modal -->
          <div class="flex items-center justify-end space-x-2.5 pt-4 border-t border-slate-100">
            <button
              type="button"
              @click="isMemberModalOpen = false"
              class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 font-bold hover:bg-slate-50 transition-colors cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              class="px-5 py-2 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-bold shadow-md shadow-rose-600/25 transition-all cursor-pointer"
            >
              {{ editingMemberId ? 'Salvar Alterações' : 'Cadastrar Membro' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal Raio-X de Aprendizagem (Gestor & Admin) -->
    <div
      v-if="isRaioXModalOpen && selectedRaioXMember"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-fade-in"
    >
      <div class="bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-2xl w-full overflow-hidden flex flex-col max-h-[90vh]">
        <!-- Cabeçalho do Modal -->
        <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between bg-slate-50/70">
          <div class="flex items-center space-x-3.5">
            <img
              :src="selectedRaioXMember.avatar"
              :alt="selectedRaioXMember.name"
              class="w-12 h-12 rounded-2xl object-cover border-2 border-indigo-400 shadow-xs shrink-0"
            />
            <div>
              <div class="flex items-center space-x-2">
                <h3 class="text-base font-extrabold text-slate-900">{{ selectedRaioXMember.name }}</h3>
                <span
                  :class="[
                    'px-2 py-0.5 rounded text-[10px] font-bold uppercase border',
                    selectedRaioXMember.status === 'CONCLUIDO'
                      ? 'bg-teal-50 text-teal-700 border-teal-200'
                      : selectedRaioXMember.status === 'ALERTA'
                      ? 'bg-rose-50 text-rose-700 border-rose-200'
                      : 'bg-indigo-50 text-indigo-700 border-indigo-200'
                  ]"
                >
                  {{ selectedRaioXMember.status === 'CONCLUIDO' ? 'Finalizado' : selectedRaioXMember.status === 'ALERTA' ? 'SLA em Risco' : 'No Prazo' }}
                </span>
              </div>
              <p class="text-xs text-slate-500 mt-0.5">
                {{ selectedRaioXMember.email }} · {{ selectedRaioXMember.department }}
              </p>
            </div>
          </div>

          <button
            @click="isRaioXModalOpen = false"
            class="text-slate-400 hover:text-slate-600 p-2 rounded-xl hover:bg-slate-200/50 transition-colors cursor-pointer"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- Feedback de Lembrete Enviado -->
        <div v-if="reminderFeedback" class="bg-emerald-50 border-b border-emerald-200 px-6 py-2.5 flex items-center justify-between text-xs text-emerald-800 font-semibold animate-fade-in">
          <span class="flex items-center gap-1.5">
            <CheckCircle2 class="w-4 h-4 text-emerald-600" />
            {{ reminderFeedback }}
          </span>
          <button @click="reminderFeedback = null" class="text-emerald-700 hover:text-emerald-950 font-bold">×</button>
        </div>

        <!-- Conteúdo Rolável -->
        <div class="p-6 overflow-y-auto space-y-6 text-xs">
          <!-- Banner Destaque de Posição Atual -->
          <div class="p-4 rounded-2xl bg-gradient-to-r from-indigo-900 via-brand-900 to-indigo-950 text-white shadow-sm space-y-2">
            <div class="flex items-center justify-between">
              <span class="text-[10px] font-bold uppercase tracking-wider text-indigo-200 bg-white/10 px-2 py-0.5 rounded">
                📍 Posicionamento Atual na Trilha
              </span>
              <span class="text-[11px] text-slate-300">
                Última atividade: <strong>{{ selectedRaioXMember.lastActive }}</strong>
              </span>
            </div>
            <p class="text-sm sm:text-base font-bold text-white flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse shrink-0"></span>
              <span>{{ selectedRaioXMember.currentLesson }}</span>
            </p>
            <p class="text-[11px] text-slate-300">
              Trilha: <strong class="text-white">{{ selectedRaioXMember.trackTitle }}</strong>
              <span class="mx-1.5">·</span>
              {{ selectedRaioXMember.currentModule }}
            </p>
          </div>

          <!-- Cards de Métricas Rápidas -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div class="p-3.5 rounded-2xl bg-slate-50 border border-slate-200/80">
              <p class="text-[10px] font-bold uppercase tracking-wide text-slate-400">Progresso Geral</p>
              <div class="flex items-baseline space-x-2 mt-1">
                <span class="text-2xl font-black text-slate-900">{{ selectedRaioXMember.progress }}%</span>
                <span class="text-[11px] text-slate-500 font-medium">({{ selectedRaioXMember.completedCount }}/{{ selectedRaioXMember.totalCount }} lições)</span>
              </div>
              <div class="w-full bg-slate-200 h-1.5 rounded-full overflow-hidden mt-2">
                <div class="bg-brand-600 h-full rounded-full" :style="{ width: `${selectedRaioXMember.progress}%` }"></div>
              </div>
            </div>

            <div class="p-3.5 rounded-2xl bg-slate-50 border border-slate-200/80">
              <p class="text-[10px] font-bold uppercase tracking-wide text-slate-400">Prazo SLA</p>
              <div class="flex items-baseline space-x-2 mt-1">
                <span class="text-2xl font-black text-slate-900">{{ selectedRaioXMember.slaDaysLeft }}</span>
                <span class="text-[11px] text-slate-500 font-medium">dias restantes</span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2">
                Status: <strong :class="selectedRaioXMember.status === 'ALERTA' ? 'text-rose-600' : 'text-emerald-600'">{{ selectedRaioXMember.status }}</strong>
              </p>
            </div>

            <div class="p-3.5 rounded-2xl bg-slate-50 border border-slate-200/80">
              <p class="text-[10px] font-bold uppercase tracking-wide text-slate-400">Aproveitamento Quizzes</p>
              <div class="flex items-baseline space-x-2 mt-1">
                <span class="text-2xl font-black text-indigo-700">{{ selectedRaioXMember.quizScoreAverage }}</span>
                <span class="text-[11px] text-slate-500 font-medium">média</span>
              </div>
              <p class="text-[11px] text-slate-500 mt-2">
                {{ selectedRaioXMember.quizzesCompleted }} finalizados
              </p>
            </div>
          </div>

          <!-- Grade Curricular & Status de Cada Lição -->
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <h4 class="text-xs font-bold uppercase tracking-wider text-slate-700 flex items-center gap-1.5">
                <BookOpen class="w-4 h-4 text-brand-600" />
                <span>Roteiro de Lições & Atividades Normativas</span>
              </h4>
              <span class="text-[11px] text-slate-400">Auditoria Granular</span>
            </div>

            <div class="space-y-2">
              <div
                v-for="(step, idx) in selectedRaioXMember.lessons"
                :key="idx"
                class="p-3 rounded-2xl border transition-colors flex items-center justify-between gap-3"
                :class="[
                  step.completed
                    ? 'bg-teal-50/50 border-teal-200/80 text-teal-950'
                    : step.title === selectedRaioXMember.currentLesson
                    ? 'bg-indigo-50 border-indigo-300 text-indigo-950 shadow-2xs'
                    : 'bg-white border-slate-200 text-slate-600'
                ]"
              >
                <div class="flex items-center space-x-3 min-w-0">
                  <div
                    class="w-6 h-6 rounded-full flex items-center justify-center shrink-0 text-xs font-bold"
                    :class="[
                      step.completed
                        ? 'bg-teal-600 text-white'
                        : step.title === selectedRaioXMember.currentLesson
                        ? 'bg-indigo-600 text-white ring-2 ring-indigo-200 animate-pulse'
                        : 'bg-slate-100 text-slate-400 border border-slate-200'
                    ]"
                  >
                    <Check v-if="step.completed" class="w-3.5 h-3.5" />
                    <span v-else>{{ idx + 1 }}</span>
                  </div>
                  <div class="min-w-0">
                    <p class="font-bold text-xs truncate">{{ step.title }}</p>
                    <p class="text-[10px] text-slate-400">{{ step.module }} · {{ step.duration }}</p>
                  </div>
                </div>

                <div class="shrink-0 text-right">
                  <span
                    v-if="step.completed"
                    class="inline-block px-2 py-0.5 rounded text-[10px] font-semibold bg-teal-100 text-teal-800"
                  >
                    ✓ {{ step.completedAt || 'Concluído' }}
                  </span>
                  <span
                    v-else-if="step.title === selectedRaioXMember.currentLesson"
                    class="inline-block px-2 py-0.5 rounded text-[10px] font-bold bg-indigo-100 text-indigo-800"
                  >
                    Em Andamento
                  </span>
                  <span
                    v-else
                    class="inline-block px-2 py-0.5 rounded text-[10px] font-medium text-slate-400"
                  >
                    Pendente
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Rodapé de Ações do Gestor -->
        <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex items-center justify-between">
          <button
            type="button"
            @click="sendSupportReminder(selectedRaioXMember)"
            class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-bold text-indigo-700 bg-indigo-100/70 hover:bg-indigo-200/80 transition-colors cursor-pointer"
          >
            <Send class="w-3.5 h-3.5" />
            <span>Enviar Notificação de Apoio</span>
          </button>

          <button
            type="button"
            @click="isRaioXModalOpen = false"
            class="px-5 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs transition-colors cursor-pointer"
          >
            Fechar Raio-X
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Gerador de Quiz -->
    <QuizGeneratorModal
      :is-open="isQuizModalOpen"
      lesson-id="d1a2b3c4-0001-4000-8000-000000000001"
      lesson-title="Formação de Novos Cooperados & Colaboradores"
      @close="isQuizModalOpen = false"
    />
  </div>
</template>
