<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useTrackStore } from '@/stores/tracks';
import { useAiTutorStore } from '@/stores/aiTutor';
import ProgressBar from '@/components/common/ProgressBar.vue';
import SkeletonLoader from '@/components/common/SkeletonLoader.vue';
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
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const trackStore = useTrackStore();
const aiTutorStore = useAiTutorStore();

onMounted(async () => {
  if (!authStore.token) {
    await authStore.syncKeycloakToken(authStore.currentUser.id);
  }
  await trackStore.fetchTracks();
});

function openTrackLesson(trackId: string) {
  const track = trackStore.tracks.find((t) => t.id === trackId);
  if (!track || !track.modules || track.modules.length === 0) return;

  // Find first uncompleted lesson or default to first lesson
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
    <!-- Welcome Hero Banner -->
    <div class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900 via-slate-800 to-brand-950 text-white p-6 sm:p-8 md:p-10 shadow-xl border border-slate-700/50">
      <!-- Background subtle decorative shapes -->
      <div class="absolute -right-16 -top-16 w-80 h-80 rounded-full bg-brand-500/10 blur-3xl pointer-events-none"></div>
      <div class="absolute right-1/3 -bottom-16 w-64 h-64 rounded-full bg-ai-500/10 blur-2xl pointer-events-none"></div>

      <div class="relative z-10 flex flex-col lg:flex-row items-start lg:items-center justify-between gap-8">
        <div class="space-y-3 max-w-2xl">
          <div class="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-xs border border-white/10 text-xs font-semibold text-brand-300">
            <span class="w-2 h-2 rounded-full bg-brand-400 animate-pulse"></span>
            <span>Programa Oficial de Integração Cooperativa 2026</span>
          </div>

          <h1 class="text-2xl sm:text-3xl md:text-4xl font-extrabold tracking-tight text-white leading-tight">
            Olá, {{ authStore.currentUser.name.split(' ')[0] }}! 👋
          </h1>

          <p class="text-sm sm:text-base text-slate-300 leading-relaxed">
            Bem-vindo(a) ao departamento de <span class="text-white font-semibold">{{ authStore.currentUser.department }}</span>. Aqui você desenvolverá competências sobre o modelo cooperativista, conformidade regulatória e nossos sistemas operacionais.
          </p>

          <div class="pt-2 flex flex-wrap items-center gap-4 text-xs text-slate-300">
            <div class="flex items-center space-x-1.5 bg-black/20 px-3 py-1.5 rounded-lg border border-white/5">
              <Calendar class="w-4 h-4 text-brand-400" />
              <span>Início: {{ authStore.currentUser.joinDate || '15/09/2026' }}</span>
            </div>
            <div class="flex items-center space-x-1.5 bg-black/20 px-3 py-1.5 rounded-lg border border-white/5">
              <Clock class="w-4 h-4 text-indigo-400" />
              <span>SLA Máximo Geral: <strong class="text-white">14 dias</strong></span>
            </div>
          </div>
        </div>

        <!-- Progress Card inside Hero -->
        <div class="w-full lg:w-80 bg-white/10 backdrop-blur-md border border-white/15 rounded-2xl p-5 shadow-lg shrink-0">
          <div class="flex items-center justify-between text-xs font-semibold text-slate-200 mb-2">
            <span>Progresso Geral do Onboarding</span>
            <span class="text-lg font-extrabold text-brand-300">{{ trackStore.overallProgressPercent }}%</span>
          </div>

          <ProgressBar
            :value="trackStore.overallProgressPercent"
            variant="brand"
            size="md"
          />

          <div class="mt-4 pt-3 border-t border-white/10 grid grid-cols-2 gap-2 text-center text-xs">
            <div>
              <p class="text-slate-400 text-[11px]">Aulas Concluídas</p>
              <p class="text-sm font-bold text-white mt-0.5">
                {{ trackStore.completedCount }} / {{ trackStore.totalLessonsCount }}
              </p>
            </div>
            <div>
              <p class="text-slate-400 text-[11px]">Dias Restantes</p>
              <p class="text-sm font-bold text-emerald-400 mt-0.5">12 dias</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-teal-50 border border-teal-100 flex items-center justify-center text-brand-600 shrink-0">
          <BookOpen class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Trilhas Ativas</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">{{ trackStore.tracks.length }} Trilhas</p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-emerald-50 border border-emerald-100 flex items-center justify-center text-emerald-600 shrink-0">
          <CheckCircle2 class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Lições Finalizadas</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">{{ trackStore.completedCount }} Aulas</p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600 shrink-0">
          <Clock class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Carga Horária</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">36 Horas</p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-violet-50 border border-violet-100 flex items-center justify-center text-violet-600 shrink-0">
          <Sparkles class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Tutor RAG IA</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">Disponível 24/7</p>
        </div>
      </div>
    </div>

    <!-- AI Tutor Banner Promotion -->
    <div class="rounded-2xl bg-gradient-to-r from-ai-500/10 via-indigo-500/5 to-brand-500/10 border border-ai-200/60 p-5 flex flex-col sm:flex-row items-center justify-between gap-4">
      <div class="flex items-center space-x-4">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-ai-500 to-indigo-600 flex items-center justify-center text-white shrink-0 shadow-md shadow-ai-500/20">
          <Sparkles class="w-5 h-5" />
        </div>
        <div>
          <h4 class="text-sm font-bold text-slate-900">Dúvidas sobre o conteúdo ou normas do estatuto?</h4>
          <p class="text-xs text-slate-600">Nosso Tutor de IA responde suas perguntas citando diretamente as fontes e artigos das resoluções.</p>
        </div>
      </div>
      <button
        @click="aiTutorStore.openDrawer"
        class="shrink-0 px-4 py-2 rounded-xl text-xs font-bold bg-white text-indigo-700 border border-indigo-200 hover:bg-indigo-50 shadow-xs transition-all flex items-center space-x-2"
      >
        <span>Abrir Chat com o Tutor</span>
        <ArrowRight class="w-3.5 h-3.5" />
      </button>
    </div>

    <!-- Tracks Section -->
    <div id="trilhas" class="space-y-5">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold tracking-tight text-slate-900 flex items-center gap-2">
            <Layers class="w-5 h-5 text-brand-600" />
            Trilhas de Aprendizado Corporativo
          </h2>
          <p class="text-xs text-slate-500">Conclua cada módulo para avançar em seu plano de onboarding institucional.</p>
        </div>
      </div>

      <!-- Loading Skeletons -->
      <div v-if="trackStore.isLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <SkeletonLoader v-for="i in 3" :key="i" type="card" />
      </div>

      <!-- Tracks Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="track in trackStore.tracks"
          :key="track.id"
          class="bg-white rounded-2xl border border-slate-200/80 shadow-xs hover:shadow-md hover:border-brand-300 transition-all duration-200 flex flex-col justify-between overflow-hidden group"
        >
          <!-- Card Top -->
          <div class="p-6">
            <div class="flex items-center justify-between mb-3 text-xs">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full font-semibold bg-brand-50 text-brand-700 border border-brand-200">
                {{ track.targetDepartment }}
              </span>
              <span class="flex items-center gap-1 text-slate-500 font-medium text-[11px]">
                <Clock class="w-3.5 h-3.5 text-slate-400" />
                {{ track.estimatedHours }}h estimadas
              </span>
            </div>

            <h3 class="text-base font-bold text-slate-900 group-hover:text-brand-700 transition-colors leading-snug">
              {{ track.title }}
            </h3>

            <p class="text-xs text-slate-600 mt-2 line-clamp-3 leading-relaxed">
              {{ track.description }}
            </p>

            <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span class="flex items-center gap-1">
                <Calendar class="w-3.5 h-3.5 text-amber-500" /> SLA: {{ track.slaDays }} dias
              </span>
              <span>
                {{ track.modules?.length || 0 }} {{ (track.modules?.length || 0) === 1 ? 'Módulo' : 'Módulos' }}
              </span>
            </div>
          </div>

          <!-- Card Bottom with Progress and Action -->
          <div class="px-6 py-4 bg-slate-50/70 border-t border-slate-100 space-y-3">
            <div>
              <div class="flex justify-between items-center text-xs font-semibold text-slate-700 mb-1.5">
                <span>Progresso</span>
                <span class="text-brand-700 font-bold">{{ trackStore.getTrackProgressPercent(track.id) }}%</span>
              </div>
              <ProgressBar
                :value="trackStore.getTrackProgressPercent(track.id)"
                variant="brand"
                size="sm"
              />
            </div>

            <button
              @click="openTrackLesson(track.id)"
              class="w-full py-2.5 px-4 rounded-xl text-xs font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-xs hover:shadow transition-all flex items-center justify-center space-x-2 group-hover:scale-[1.01]"
            >
              <span>{{ trackStore.getTrackProgressPercent(track.id) > 0 ? 'Continuar Trilha' : 'Iniciar Trilha' }}</span>
              <ArrowRight class="w-3.5 h-3.5 transition-transform group-hover:translate-x-1" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
