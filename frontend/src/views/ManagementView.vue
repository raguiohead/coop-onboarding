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
} from 'lucide-vue-next';

interface TeamMember {
  id: string;
  name: string;
  email: string;
  role: 'COLABORADOR' | 'GESTOR';
  department: string;
  trackTitle: string;
  progress: number;
  slaDaysLeft: number;
  status: 'NO_PRAZO' | 'ALERTA' | 'CONCLUIDO';
  avatar: string;
  quizzesCompleted: string;
}

const defaultMembers: TeamMember[] = [
  {
    id: 'user-01',
    name: 'Ana Carolina Silva',
    email: 'ana.silva@coop.local',
    role: 'COLABORADOR',
    department: 'Atendimento & Cooperados',
    trackTitle: 'Cultura & Governança Cooperativista',
    progress: 85,
    slaDaysLeft: 8,
    status: 'NO_PRAZO',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '2 de 3',
  },
  {
    id: 'user-02',
    name: 'Carlos Souza',
    email: 'carlos.souza@coop.local',
    role: 'COLABORADOR',
    department: 'Engenharia de TI & Inovação',
    trackTitle: 'Arquitetura de Sistemas & Segurança Bancária',
    progress: 60,
    slaDaysLeft: 12,
    status: 'NO_PRAZO',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '1 de 3',
  },
  {
    id: 'user-03',
    name: 'Juliana Pires',
    email: 'juliana.pires@coop.local',
    role: 'COLABORADOR',
    department: 'Crédito Imobiliário & Rural',
    trackTitle: 'Políticas de Crédito & Gestão de Riscos',
    progress: 35,
    slaDaysLeft: 2,
    status: 'ALERTA',
    avatar: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '0 de 2',
  },
  {
    id: 'user-04',
    name: 'Lucas Antunes',
    email: 'lucas.antunes@coop.local',
    role: 'COLABORADOR',
    department: 'Controladoria & Contabilidade',
    trackTitle: 'Contabilidade Cooperativa & Sobras Líquidas',
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '3 de 3',
  },
  {
    id: 'user-05',
    name: 'Roberto Mendes',
    email: 'roberto.mendes@coop.local',
    role: 'GESTOR',
    department: 'Gente & Gestão (RH)',
    trackTitle: 'Liderança & Governança Cooperativa',
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: 'Supervisão Ativa',
  },
  {
    id: 'user-06',
    name: 'Fernanda Lima',
    email: 'fernanda.lima@coop.local',
    role: 'GESTOR',
    department: 'Gerência de Crédito & Riscos',
    trackTitle: 'Supervisão de Crédito e Conformidade BACEN',
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: 'Supervisão Ativa',
  },
];

const authStore = useAuthStore();
const isQuizModalOpen = ref(false);

// Persistência de Membros da Equipe no LocalStorage
const savedTeam = localStorage.getItem('coop_team_members');
const teamMembers = ref<TeamMember[]>(savedTeam ? JSON.parse(savedTeam) : defaultMembers);

function saveTeamMembers() {
  localStorage.setItem('coop_team_members', JSON.stringify(teamMembers.value));
}

// Filtro por Papel na tabela
const selectedRoleFilter = ref<'TODOS' | 'COLABORADOR' | 'GESTOR'>('TODOS');

const filteredMembers = computed(() => {
  if (selectedRoleFilter.value === 'TODOS') return teamMembers.value;
  return teamMembers.value.filter((m) => m.role === selectedRoleFilter.value);
});

// KPIs da Turma
const colaboradoresCount = computed(() => teamMembers.value.filter((m) => m.role === 'COLABORADOR').length);
const gestoresCount = computed(() => teamMembers.value.filter((m) => m.role === 'GESTOR').length);
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
  role: 'COLABORADOR' as 'COLABORADOR' | 'GESTOR',
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
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/80 text-[11px] font-bold uppercase tracking-wider text-slate-400 border-b border-slate-100">
              <th class="px-6 py-3.5">Profissional</th>
              <th class="px-6 py-3.5">Papel</th>
              <th class="px-6 py-3.5">Trilha Atribuída</th>
              <th class="px-6 py-3.5">Progresso</th>
              <th class="px-6 py-3.5">SLA Restante</th>
              <th class="px-6 py-3.5">Quizzes</th>
              <th class="px-6 py-3.5">Status</th>
              <th v-if="authStore.isAdmin" class="px-6 py-3.5 text-right">Ações Admin</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 text-xs">
            <tr v-for="member in filteredMembers" :key="member.id" class="hover:bg-slate-50/60 transition-colors">
              <td class="px-6 py-4 flex items-center space-x-3">
                <img :src="member.avatar" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
                <div>
                  <p class="font-bold text-slate-900">{{ member.name }}</p>
                  <p class="text-[11px] text-slate-500">{{ member.email }} · {{ member.department }}</p>
                </div>
              </td>
              <td class="px-6 py-4">
                <span
                  :class="[
                    'px-2 py-0.5 rounded text-[10px] font-bold uppercase border',
                    member.role === 'GESTOR' ? 'bg-indigo-50 text-indigo-700 border-indigo-200' : 'bg-teal-50 text-teal-700 border-teal-200'
                  ]"
                >
                  {{ member.role }}
                </span>
              </td>
              <td class="px-6 py-4 text-slate-700 font-medium">
                {{ member.trackTitle }}
              </td>
              <td class="px-6 py-4 w-44">
                <div v-if="member.role === 'COLABORADOR'" class="flex items-center space-x-2">
                  <div class="flex-1 bg-slate-100 h-2 rounded-full overflow-hidden">
                    <div class="bg-brand-600 h-full rounded-full transition-all" :style="{ width: `${member.progress}%` }"></div>
                  </div>
                  <span class="font-bold text-slate-700 text-[11px]">{{ member.progress }}%</span>
                </div>
                <span v-else class="text-[11px] font-semibold text-slate-400">
                  Liderança Pedagógica
                </span>
              </td>
              <td class="px-6 py-4 font-medium text-slate-600">
                <span v-if="member.role === 'GESTOR'" class="text-slate-400 text-[11px]">
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
              <td class="px-6 py-4 font-semibold text-slate-700">
                {{ member.quizzesCompleted }}
              </td>
              <td class="px-6 py-4">
                <span
                  :class="[
                    'px-2 py-0.5 rounded text-[10px] font-bold uppercase border',
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

              <!-- Coluna de Ações Admin -->
              <td v-if="authStore.isAdmin" class="px-6 py-4 text-right">
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

    <!-- Modal Gerador de Quiz -->
    <QuizGeneratorModal
      :is-open="isQuizModalOpen"
      lesson-id="d1a2b3c4-0001-4000-8000-000000000001"
      lesson-title="Formação de Novos Cooperados & Colaboradores"
      @close="isQuizModalOpen = false"
    />
  </div>
</template>
