<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import MarkdownIt from 'markdown-it';
import { useTrackStore } from '@/stores/tracks';
import { useAuthStore } from '@/stores/auth';
import { useAiTutorStore } from '@/stores/aiTutor';
import QuizGeneratorModal from '@/components/ai/QuizGeneratorModal.vue';
import ProgressBar from '@/components/common/ProgressBar.vue';
import {
  ChevronLeft,
  ChevronRight,
  CheckCircle,
  Circle,
  Clock,
  Sparkles,
  BookOpen,
  ArrowLeft,
  Menu,
  X,
  FileQuestion,
  Award,
} from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const trackStore = useTrackStore();
const authStore = useAuthStore();
const aiTutorStore = useAiTutorStore();

const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true,
});

const isSidebarOpenMobile = ref(false);
const isQuizModalOpen = ref(false);

const trackId = computed(() => (route.params.trackId as string) || trackStore.activeTrackId || '');
const lessonId = computed(() => (route.params.lessonId as string) || trackStore.activeLessonId || '');

// Synchronize store with route params
watch(
  [trackId, lessonId],
  ([newTrackId, newLessonId]) => {
    if (newTrackId && newLessonId) {
      trackStore.selectLesson(newTrackId, newLessonId);
    }
  },
  { immediate: true }
);

const currentTrack = computed(() => {
  return trackStore.tracks.find((t) => t.id === trackId.value) || trackStore.tracks[0];
});

const currentLesson = computed(() => {
  if (!currentTrack.value) return undefined;
  for (const m of currentTrack.value.modules) {
    const found = m.lessons.find((l) => l.id === lessonId.value);
    if (found) return found;
  }
  return currentTrack.value.modules[0]?.lessons[0];
});

// All lessons flattened to handle previous/next navigation
const allTrackLessons = computed(() => {
  if (!currentTrack.value) return [];
  return currentTrack.value.modules.flatMap((m) => m.lessons);
});

const currentLessonIndex = computed(() => {
  return allTrackLessons.value.findIndex((l) => l.id === currentLesson.value?.id);
});

const previousLesson = computed(() => {
  if (currentLessonIndex.value > 0) {
    return allTrackLessons.value[currentLessonIndex.value - 1];
  }
  return null;
});

const nextLesson = computed(() => {
  if (currentLessonIndex.value >= 0 && currentLessonIndex.value < allTrackLessons.value.length - 1) {
    return allTrackLessons.value[currentLessonIndex.value + 1];
  }
  return null;
});

const isCurrentLessonCompleted = computed(() => {
  return currentLesson.value ? trackStore.isLessonCompleted(currentLesson.value.id) : false;
});

function navigateToLesson(targetLessonId: string) {
  router.push(`/tracks/${trackId.value}/lessons/${targetLessonId}`);
  isSidebarOpenMobile.value = false;
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function handleToggleComplete() {
  if (currentLesson.value) {
    trackStore.toggleLessonCompletion(currentLesson.value.id);
  }
}

function renderLessonMarkdown(content?: string): string {
  if (!content) return '<p>Nenhum conteúdo disponível nesta lição.</p>';
  return md.render(content);
}
</script>

<template>
  <div class="space-y-6 pb-16">
    <!-- Top Breadcrumbs & Back Navigation -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div class="flex items-center space-x-2 text-xs sm:text-sm">
        <router-link
          to="/"
          class="flex items-center space-x-1 font-semibold text-slate-500 hover:text-brand-600 transition-colors"
        >
          <ArrowLeft class="w-4 h-4" />
          <span>Voltar ao Painel</span>
        </router-link>
        <span class="text-slate-300">/</span>
        <span class="text-slate-600 font-medium truncate max-w-[200px] sm:max-w-none">
          {{ currentTrack?.title }}
        </span>
        <span class="text-slate-300">/</span>
        <span class="text-brand-700 font-bold truncate max-w-[180px] sm:max-w-none">
          {{ currentLesson?.title }}
        </span>
      </div>

      <!-- Action buttons in header -->
      <div class="flex items-center space-x-2.5">
        <!-- AI Quiz Generation for Managers / Admins -->
        <button
          v-if="authStore.canGenerateQuiz"
          @click="isQuizModalOpen = true"
          class="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-bold bg-indigo-50 text-indigo-700 border border-indigo-200 hover:bg-indigo-100 transition-colors shadow-xs"
          title="Gerar Quiz Pedagógico com IA"
        >
          <Sparkles class="w-3.5 h-3.5 text-indigo-500" />
          <span>Gerar Quiz com IA</span>
        </button>

        <!-- Ask AI Tutor Button -->
        <button
          @click="aiTutorStore.openDrawer"
          class="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-bold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-xs transition-all"
        >
          <Sparkles class="w-3.5 h-3.5" />
          <span>Perguntar ao Tutor</span>
        </button>

        <!-- Mobile sidebar toggle -->
        <button
          @click="isSidebarOpenMobile = !isSidebarOpenMobile"
          class="lg:hidden p-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-100"
          title="Ver índice de aulas"
        >
          <Menu class="w-4 h-4" />
        </button>
      </div>
    </div>

    <!-- Main Grid: Sidebar + Markdown Reader -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
      <!-- Sidebar / Checklist Navigation -->
      <aside
        :class="[
          'lg:col-span-4 bg-white rounded-2xl border border-slate-200/90 shadow-xs overflow-hidden lg:sticky lg:top-20 z-20',
          isSidebarOpenMobile
            ? 'fixed inset-x-4 top-20 bottom-8 z-50 overflow-y-auto block shadow-2xl'
            : 'hidden lg:block'
        ]"
      >
        <div class="p-5 border-b border-slate-100 bg-slate-50/70 flex items-center justify-between">
          <div>
            <h3 class="text-sm font-bold text-slate-900 leading-tight">Módulos & Lições</h3>
            <p class="text-xs text-slate-500 mt-0.5">Progresso da trilha: {{ trackStore.getTrackProgressPercent(trackId) }}%</p>
          </div>
          <button
            v-if="isSidebarOpenMobile"
            @click="isSidebarOpenMobile = false"
            class="p-1 rounded-lg text-slate-400 hover:text-slate-600"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <div class="px-5 py-3 border-b border-slate-100">
          <ProgressBar
            :value="trackStore.getTrackProgressPercent(trackId)"
            variant="brand"
            size="sm"
          />
        </div>

        <div class="p-4 space-y-6 max-h-[calc(100vh-280px)] overflow-y-auto">
          <div
            v-for="(mod, mIdx) in currentTrack?.modules"
            :key="mod.id"
            class="space-y-2"
          >
            <h4 class="text-xs font-bold uppercase tracking-wider text-slate-400">
              {{ mod.title }}
            </h4>

            <div class="space-y-1">
              <div
                v-for="les in mod.lessons"
                :key="les.id"
                role="button"
                tabindex="0"
                @click="navigateToLesson(les.id)"
                @keydown.enter="navigateToLesson(les.id)"
                :class="[
                  'w-full flex items-start space-x-3 p-3 rounded-xl text-left text-xs transition-all cursor-pointer select-none focus:outline-none focus:ring-2 focus:ring-brand-500/30',
                  les.id === currentLesson?.id
                    ? 'bg-brand-50 border border-brand-200/80 text-brand-950 font-bold shadow-xs'
                    : 'hover:bg-slate-50 text-slate-700 border border-transparent'
                ]"
              >
                <!-- Checkbox status icon -->
                <button
                  type="button"
                  @click.stop="trackStore.toggleLessonCompletion(les.id)"
                  class="mt-0.5 shrink-0 text-slate-300 hover:text-emerald-500 transition-colors focus:outline-none"
                  :title="trackStore.isLessonCompleted(les.id) ? 'Marcar como não concluída' : 'Marcar como concluída'"
                >
                  <CheckCircle
                    v-if="trackStore.isLessonCompleted(les.id)"
                    class="w-4 h-4 text-emerald-600 fill-emerald-100"
                  />
                  <Circle v-else class="w-4 h-4 text-slate-300 hover:text-emerald-500" />
                </button>

                <div class="flex-1 min-w-0">
                  <p class="truncate leading-snug">{{ les.title }}</p>
                  <div class="flex items-center space-x-2 mt-1 text-[11px] text-slate-400">
                    <span class="flex items-center gap-1">
                      <Clock class="w-3 h-3" /> {{ les.estimatedMinutes }} min
                    </span>
                    <span v-if="trackStore.isLessonCompleted(les.id)" class="text-emerald-600 font-semibold">
                      · Concluída
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- Center / Right: Lesson Content Reader -->
      <main class="lg:col-span-8 bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
        <!-- Lesson Top Header -->
        <div class="p-6 sm:p-8 border-b border-slate-100 bg-gradient-to-b from-slate-50/80 to-white">
          <div class="flex flex-wrap items-center justify-between gap-3 text-xs text-slate-500 mb-2">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full font-semibold bg-brand-50 text-brand-700 border border-brand-200">
              {{ currentTrack?.title }}
            </span>
            <span class="flex items-center gap-1.5 font-medium">
              <Clock class="w-4 h-4 text-slate-400" />
              Tempo estimado: {{ currentLesson?.estimatedMinutes }} minutos
            </span>
          </div>

          <h1 class="text-xl sm:text-2xl md:text-3xl font-extrabold text-slate-900 tracking-tight leading-snug">
            {{ currentLesson?.title }}
          </h1>

          <!-- Toggle completion button inside header -->
          <div class="mt-6 flex flex-wrap items-center gap-3">
            <button
              @click="handleToggleComplete"
              :class="[
                'inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold transition-all shadow-xs',
                isCurrentLessonCompleted
                  ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                  : 'bg-brand-600 hover:bg-brand-700 text-white'
              ]"
            >
              <CheckCircle class="w-4 h-4" />
              <span>{{ isCurrentLessonCompleted ? 'Lição Concluída ✓' : 'Marcar Lição como Concluída' }}</span>
            </button>

            <!-- Quiz button for managers / admins -->
            <button
              v-if="authStore.canGenerateQuiz"
              @click="isQuizModalOpen = true"
              class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-white text-indigo-700 border border-indigo-200 hover:bg-indigo-50 transition-colors shadow-xs"
            >
              <Sparkles class="w-4 h-4 text-indigo-500" />
              <span>Gerar Quiz Desta Aula</span>
            </button>
          </div>
        </div>

        <!-- Rendered Markdown Body -->
        <article
          class="p-6 sm:p-10 markdown-body"
          v-html="renderLessonMarkdown(currentLesson?.contentMarkdown)"
        ></article>

        <!-- Bottom Lesson Navigation -->
        <div class="p-6 sm:p-8 bg-slate-50 border-t border-slate-100 flex items-center justify-between gap-4">
          <div>
            <button
              v-if="previousLesson"
              @click="navigateToLesson(previousLesson.id)"
              class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-semibold bg-white border border-slate-200 text-slate-700 hover:bg-slate-100 transition-colors shadow-xs"
            >
              <ChevronLeft class="w-4 h-4" />
              <span class="hidden sm:inline">Anterior:</span>
              <span class="truncate max-w-[120px] sm:max-w-[200px]">{{ previousLesson.title }}</span>
            </button>
          </div>

          <div class="flex items-center space-x-2">
            <button
              v-if="nextLesson"
              @click="navigateToLesson(nextLesson.id)"
              class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-bold bg-brand-600 text-white hover:bg-brand-700 transition-colors shadow-xs"
            >
              <span class="hidden sm:inline">Próxima:</span>
              <span class="truncate max-w-[120px] sm:max-w-[200px]">{{ nextLesson.title }}</span>
              <ChevronRight class="w-4 h-4" />
            </button>
            <router-link
              v-else
              to="/"
              class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-bold bg-emerald-600 text-white hover:bg-emerald-700 transition-colors shadow-xs"
            >
              <span>Concluir Trilha</span>
              <Award class="w-4 h-4" />
            </router-link>
          </div>
        </div>
      </main>
    </div>

    <!-- Quiz Generator Modal -->
    <QuizGeneratorModal
      :is-open="isQuizModalOpen"
      :lesson="currentLesson"
      @close="isQuizModalOpen = false"
    />
  </div>
</template>
