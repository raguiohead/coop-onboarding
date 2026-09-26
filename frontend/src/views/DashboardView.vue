<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useTrackStore } from '@/stores/tracks';
import { useAiTutorStore } from '@/stores/aiTutor';
import ProgressBar from '@/components/common/ProgressBar.vue';
import SkeletonLoader from '@/components/common/SkeletonLoader.vue';
import QuizGeneratorModal from '@/components/ai/QuizGeneratorModal.vue';
import {
  Sparkles,
  BookOpen,
  Clock,
  Calendar,
  CheckCircle2,
  ArrowRight,
  TrendingUp,
  Shield,
  Layers,
  Award,
  Users,
  AlertTriangle,
  Server,
  Activity,
  Database,
  ExternalLink,
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const trackStore = useTrackStore();
const aiTutorStore = useAiTutorStore();

const isQuizModalOpen = ref(false);

onMounted(async () => {
  if (!authStore.token) {
    await authStore.syncKeycloakToken(authStore.currentUser.id);
  }
  await trackStore.fetchTracks();
});

function openTrackLesson(trackId: string) {
  const track = trackStore.tracks.find((t) => t.id === trackId);
  if (!track || !track.modules || track.modules.length === 0) return;

  let targetLessonId = track.modules[0].lessons[0]?.id;
  for (const mod of track.modules) {
    const uncompleted = mod.lessons.find((l) => !trackStore.isLessonCompleted(l.id));
    if (uncompleted) {
      targetLessonId = uncompleted.id;
      break;
    }
  }

  if (targetLessonId) {
    trackStore.selectLesson(trackId, targetLessonId);
    router.push(`/tracks/${trackId}/lessons/${targetLessonId}`);
  }
}
</script>

<template>
  <div class="space-y-8 pb-12">
    <!-- Role Switch Context Callout Banner -->
    <div
      class="p-4 sm:p-5 rounded-3xl border flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 shadow-xs"
      :class="[
        authStore.isAdmin
          ? 'bg-rose-50/80 border-rose-200 text-rose-950'
          : authStore.isGestor
          ? 'bg-indigo-50/80 border-indigo-200 text-indigo-950'
          : 'bg-teal-50/80 border-teal-200 text-teal-950'
      ]"
    >
      <div class="flex items-center space-x-3.5">
        <img
          :src="authStore.currentUser.avatarUrl"
          :alt="authStore.currentUser.name"
          class="w-11 h-11 rounded-2xl object-cover border-2 shadow-xs shrink-0"
          :class="[
            authStore.isAdmin ? 'border-rose-400' : authStore.isGestor ? 'border-indigo-400' : 'border-teal-400'
          ]"
        />
        <div>
          <div class="flex items-center space-x-2">
            <span class="text-xs font-black uppercase tracking-wider px-2 py-0.5 rounded border"
              :class="authStore.roleBadge.bg"
            >
              {{ authStore.roleBadge.label }}
            </span>
            <span class="text-xs text-slate-500 font-medium">· Conectado como <strong>{{ authStore.currentUser.name }}</strong></span>
          </div>
          <p class="text-xs sm:text-sm font-semibold mt-1">
            <span v-if="authStore.isColaborador">
              🎓 Modo Colaborador: Foco no seu plano individual de capacitação, lições normativas e simulados.
            </span>
            <span v-else-if="authStore.isGestor">
              📊 Modo Gestor: Painel de liderança com métricas da turma, alertas de SLA e criação de quizzes com IA.
            </span>
            <span v-else>
              🛡️ Modo Administrador: Governança completa do sistema, base vetorial pgvector e integração Keycloak.
            </span>
          </p>
        </div>
      </div>

      <!-- Action buttons depending on role -->
      <div class="flex items-center space-x-2 shrink-0">
        <button
          v-if="authStore.canGenerateQuiz"
          @click="isQuizModalOpen = true"
          class="px-3.5 py-1.5 rounded-xl text-xs font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm transition-all flex items-center space-x-1.5 cursor-pointer"
        >
          <Sparkles class="w-3.5 h-3.5" />
          <span>Gerar Quiz com IA</span>
        </button>

        <router-link
          v-if="authStore.isGestor || authStore.isAdmin"
          to="/gestao"
          class="px-3.5 py-1.5 rounded-xl text-xs font-bold border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 transition-colors"
        >
          Painel de Equipe
        </router-link>

        <button
          @click="aiTutorStore.openDrawer"
          class="px-3.5 py-1.5 rounded-xl text-xs font-bold bg-slate-900 hover:bg-slate-800 text-white shadow-sm transition-colors flex items-center space-x-1.5 cursor-pointer"
        >
          <Sparkles class="w-3.5 h-3.5 text-indigo-300" />
          <span>Abrir Tutor IA</span>
        </button>
      </div>
    </div>

    <!-- Dynamic Metrics KPIs Based on Role -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
      <!-- 1. COLABORADOR KPIs -->
      <template v-if="authStore.isColaborador">
        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Seu Progresso Global</span>
            <TrendingUp class="w-4 h-4 text-teal-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">
              {{ trackStore.overallProgressPercent }}%
            </span>
            <span class="text-xs text-teal-600 font-semibold">Em dia</span>
          </div>
          <ProgressBar :value="trackStore.overallProgressPercent" variant="success" class="mt-3" />
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Prazo de Conclusão (SLA)</span>
            <Calendar class="w-4 h-4 text-brand-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">8 dias</span>
            <span class="text-xs text-slate-400">restantes</span>
          </div>
          <p class="text-xs text-slate-500 mt-3 flex items-center gap-1.5">
            <Clock class="w-3.5 h-3.5 text-brand-500" /> SLA máximo: 14 dias
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Quizzes & Avaliações</span>
            <Award class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-indigo-700 tracking-tight">100%</span>
            <span class="text-xs text-slate-400">média</span>
          </div>
          <router-link to="/quizzes" class="text-xs text-indigo-600 font-bold mt-3 hover:underline inline-flex items-center gap-1">
            Ver 2 quizzes pendentes <ArrowRight class="w-3 h-3" />
          </router-link>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Tutor Virtual Local</span>
            <Sparkles class="w-4 h-4 text-ai-500" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">RAG</span>
            <span class="text-xs text-emerald-600 font-semibold">100% On-Premise</span>
          </div>
          <p class="text-xs text-slate-500 mt-3">
            Tire dúvidas normativas em tempo real.
          </p>
        </div>
      </template>

      <!-- 2. GESTOR KPIs -->
      <template v-else-if="authStore.isGestor">
        <div class="bg-white p-6 rounded-3xl border border-indigo-100 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Turma em Onboarding</span>
            <Users class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-indigo-900 tracking-tight">4 Membros</span>
          </div>
          <p class="text-xs text-teal-600 font-semibold mt-3 flex items-center gap-1">
            <CheckCircle2 class="w-3.5 h-3.5" /> 100% participando ativamente
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Conclusão Média da Equipe</span>
            <TrendingUp class="w-4 h-4 text-teal-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">70%</span>
            <span class="text-xs text-slate-400">meta: 80%</span>
          </div>
          <ProgressBar :value="70" variant="brand" class="mt-3" />
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Alertas de SLA em Risco</span>
            <AlertTriangle class="w-4 h-4 text-amber-500" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-amber-600 tracking-tight">1 Alerta</span>
          </div>
          <router-link to="/gestao" class="text-xs text-amber-700 font-bold mt-3 hover:underline inline-flex items-center gap-1">
            Juliana Pires (2 dias) <ArrowRight class="w-3 h-3" />
          </router-link>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Quizzes Avaliados</span>
            <Award class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-indigo-700 tracking-tight">92%</span>
            <span class="text-xs text-slate-400">taxa de acerto</span>
          </div>
          <p class="text-xs text-slate-500 mt-3">
            Excelente aproveitamento da turma.
          </p>
        </div>
      </template>

      <!-- 3. ADMIN KPIs -->
      <template v-else>
        <div class="bg-white p-6 rounded-3xl border border-rose-100 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Serviços Enterprise</span>
            <Server class="w-4 h-4 text-rose-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">4/4 UP</span>
          </div>
          <p class="text-xs text-emerald-600 font-bold mt-3 flex items-center gap-1">
            <CheckCircle2 class="w-3.5 h-3.5" /> PostgreSQL, pgvector, KC, Ollama
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Base Vetorial pgvector</span>
            <Database class="w-4 h-4 text-indigo-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">768 Dim</span>
            <span class="text-xs text-slate-400">HNSW Cosine</span>
          </div>
          <p class="text-xs text-slate-500 mt-3">Índice HNSW de alta performance</p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Segurança IAM Keycloak</span>
            <Shield class="w-4 h-4 text-emerald-600" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">5 Perfis</span>
            <span class="text-xs text-emerald-600 font-bold">RBAC Ativo</span>
          </div>
          <a href="http://localhost:8180" target="_blank" class="text-xs text-brand-700 font-bold mt-3 hover:underline inline-flex items-center gap-1">
            Console Keycloak <ExternalLink class="w-3 h-3" />
          </a>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs">
          <div class="flex items-center justify-between text-slate-500 mb-2">
            <span class="text-xs font-medium">Inferência LLM Local</span>
            <Activity class="w-4 h-4 text-ai-500" />
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-slate-900 tracking-tight">Llama 3.2</span>
          </div>
          <p class="text-xs text-teal-600 font-semibold mt-3">
            Keep-Alive 24h & Warm-Up Ativo
          </p>
        </div>
      </template>
    </div>

    <!-- Tracks List Section -->
    <div class="space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold tracking-tight text-slate-900 font-sans">
            <span v-if="authStore.isGestor">Trilhas Monitoradas da sua Turma</span>
            <span v-else-if="authStore.isAdmin">Trilhas Cadastradas no Ecossistema</span>
            <span v-else>Suas Trilhas de Aprendizagem</span>
          </h2>
          <p class="text-xs text-slate-500">
            Trilhas corporativas organizadas por competências e conformidade normativa.
          </p>
        </div>

        <router-link
          to="/quizzes"
          class="text-xs font-bold text-brand-700 hover:text-brand-900 flex items-center gap-1"
        >
          Acessar Quizzes & Simulados <ArrowRight class="w-3.5 h-3.5" />
        </router-link>
      </div>

      <!-- Loading skeleton -->
      <div v-if="trackStore.isLoading" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <SkeletonLoader count="card" />
        <SkeletonLoader count="card" />
      </div>

      <!-- Track Cards Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div
          v-for="track in trackStore.tracks"
          :key="track.id"
          class="group bg-white rounded-3xl p-6 sm:p-7 border border-slate-200/80 shadow-xs hover:shadow-xl hover:border-brand-500/30 transition-all duration-300 flex flex-col justify-between"
        >
          <div>
            <!-- Header Badges -->
            <div class="flex items-center justify-between gap-2 mb-4">
              <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold uppercase tracking-wider bg-brand-50 text-brand-800 border border-brand-200">
                {{ track.targetDepartment }}
              </span>

              <div class="flex items-center space-x-2 text-xs text-slate-400">
                <span class="flex items-center gap-1 font-medium text-slate-600">
                  <Clock class="w-3.5 h-3.5" /> {{ track.estimatedHours }}h estimadas
                </span>
              </div>
            </div>

            <!-- Title & Description -->
            <h3 class="text-lg font-bold text-slate-900 group-hover:text-brand-700 transition-colors mb-2">
              {{ track.title }}
            </h3>
            <p class="text-xs sm:text-sm text-slate-600 leading-relaxed mb-6">
              {{ track.description }}
            </p>

            <!-- Modules and Lessons Count -->
            <div class="flex items-center space-x-4 text-xs text-slate-500 mb-6 pb-6 border-b border-slate-100">
              <span class="flex items-center gap-1.5 font-medium">
                <Layers class="w-4 h-4 text-brand-600" />
                {{ track.modules.length }} Módulos
              </span>
              <span>·</span>
              <span class="flex items-center gap-1.5 font-medium">
                <BookOpen class="w-4 h-4 text-brand-600" />
                {{ track.modules.reduce((acc, m) => acc + m.lessons.length, 0) }} Lições Interativas
              </span>
            </div>
          </div>

          <!-- Bottom Action -->
          <div class="flex items-center justify-between pt-2">
            <div class="text-xs">
              <span class="text-slate-400">SLA de Conclusão:</span>
              <span class="ml-1 font-bold text-slate-800">{{ track.slaDays }} dias</span>
            </div>

            <button
              @click="openTrackLesson(track.id)"
              class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-brand-700 hover:bg-brand-800 text-white shadow-md shadow-brand-700/20 group-hover:bg-brand-600 transition-all cursor-pointer"
            >
              <span>Continuar Trilha</span>
              <ArrowRight class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal do Gerador de Quiz (para Gestores/Admins) -->
    <QuizGeneratorModal
      :is-open="isQuizModalOpen"
      lesson-id="d1a2b3c4-0001-4000-8000-000000000001"
      lesson-title="Formação de Novos Cooperados & Colaboradores"
      @close="isQuizModalOpen = false"
    />
  </div>
</template>
