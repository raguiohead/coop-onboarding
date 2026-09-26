<script setup lang="ts">
import { ref } from 'vue';
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
} from 'lucide-vue-next';

const authStore = useAuthStore();
const isQuizModalOpen = ref(false);

const teamMembers = ref([
  {
    id: 'user-01',
    name: 'Ana Carolina Silva',
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
    department: 'Controladoria & Contabilidade',
    trackTitle: 'Contabilidade Cooperativa & Sobras Líquidas',
    progress: 100,
    slaDaysLeft: 0,
    status: 'CONCLUIDO',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
    quizzesCompleted: '3 de 3',
  },
]);
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
          Esta área foi projetada para <strong>Gestores de Equipe</strong> e <strong>Administradores</strong>. Você pode simular o perfil de <strong>Roberto Mendes</strong> ou <strong>Fernanda Lima</strong> no menu superior para ter acesso completo de edição e aprovações.
        </p>
      </div>
    </div>

    <!-- Header Banner -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gradient-to-r from-slate-900 via-indigo-950 to-brand-950 p-6 sm:p-8 rounded-3xl text-white shadow-xl border border-slate-700/50">
      <div>
        <div class="inline-flex items-center space-x-2 px-2.5 py-1 rounded-md text-[11px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 uppercase tracking-wider mb-2">
          <Users class="w-3.5 h-3.5" />
          <span>Liderança & Gestão de Talentos</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight font-sans">
          Cockpit de Onboarding da Equipe
        </h1>
        <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-2xl">
          Monitore em tempo real o engajamento, cumprimento de SLAs e aprovações nos testes pedagógicos da sua turma de integração.
        </p>
      </div>

      <div class="shrink-0 flex items-center space-x-2">
        <button
          @click="isQuizModalOpen = true"
          class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/25 transition-all cursor-pointer"
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
        <p class="text-2xl font-black text-slate-900">4 Membros</p>
        <p class="text-[11px] text-teal-600 font-semibold mt-1 flex items-center gap-1">
          <ArrowUpRight class="w-3 h-3" /> 100% da turma ativa
        </p>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Conclusão Média das Trilhas</span>
          <TrendingUp class="w-4 h-4 text-teal-600" />
        </div>
        <p class="text-2xl font-black text-slate-900">70%</p>
        <p class="text-[11px] text-slate-400 mt-1">Meta corporativa: 80% em 14 dias</p>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs">
        <div class="flex items-center justify-between text-slate-500 text-xs mb-2">
          <span>Alertas de SLA em Risco</span>
          <AlertTriangle class="w-4 h-4 text-amber-500" />
        </div>
        <p class="text-2xl font-black text-amber-600">1 Alerta</p>
        <p class="text-[11px] text-amber-700 font-semibold mt-1">Juliana Pires (2 dias restantes)</p>
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

    <!-- Team Members Table -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
      <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
        <div>
          <h3 class="text-base font-bold text-slate-900">Progresso Individual dos Colaboradores</h3>
          <p class="text-xs text-slate-500">Acompanhamento contínuo de cada profissional em integração.</p>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/80 text-[11px] font-bold uppercase tracking-wider text-slate-400 border-b border-slate-100">
              <th class="px-6 py-3.5">Colaborador</th>
              <th class="px-6 py-3.5">Trilha Atribuída</th>
              <th class="px-6 py-3.5">Progresso</th>
              <th class="px-6 py-3.5">SLA Restante</th>
              <th class="px-6 py-3.5">Quizzes</th>
              <th class="px-6 py-3.5">Status</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 text-xs">
            <tr v-for="member in teamMembers" :key="member.id" class="hover:bg-slate-50/60 transition-colors">
              <td class="px-6 py-4 flex items-center space-x-3">
                <img :src="member.avatar" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
                <div>
                  <p class="font-bold text-slate-900">{{ member.name }}</p>
                  <p class="text-[11px] text-slate-500">{{ member.department }}</p>
                </div>
              </td>
              <td class="px-6 py-4 text-slate-700 font-medium">
                {{ member.trackTitle }}
              </td>
              <td class="px-6 py-4 w-44">
                <div class="flex items-center space-x-2">
                  <div class="flex-1 bg-slate-100 h-2 rounded-full overflow-hidden">
                    <div class="bg-brand-600 h-full rounded-full transition-all" :style="{ width: `${member.progress}%` }"></div>
                  </div>
                  <span class="font-bold text-slate-700 text-[11px]">{{ member.progress }}%</span>
                </div>
              </td>
              <td class="px-6 py-4 font-medium text-slate-600">
                <span v-if="member.status === 'CONCLUIDO'" class="text-teal-700 font-semibold flex items-center gap-1">
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
            </tr>
          </tbody>
        </table>
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
