<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useTrackStore } from '@/stores/tracks';
import { useAuthStore } from '@/stores/auth';
import ProgressBar from '@/components/common/ProgressBar.vue';
import SkeletonLoader from '@/components/common/SkeletonLoader.vue';
import {
  BookOpen,
  Layers,
  Clock,
  Calendar,
  CheckCircle2,
  Circle,
  ArrowRight,
  Search,
  Filter,
  ChevronDown,
  ChevronUp,
  Sparkles,
  Award,
  Eye,
  Users,
} from 'lucide-vue-next';

const router = useRouter();
const trackStore = useTrackStore();
const authStore = useAuthStore();

const searchQuery = ref('');
const selectedDepartment = ref('TODOS');
const expandedModules = ref<Record<string, boolean>>({});

onMounted(async () => {
  await trackStore.fetchTracks();
});

const departments = computed(() => {
  const set = new Set<string>();
  trackStore.tracks.forEach((t) => {
    if (t.targetDepartment) set.add(t.targetDepartment);
  });
  return ['TODOS', ...Array.from(set)];
});

const filteredTracks = computed(() => {
  return trackStore.tracks.filter((track) => {
    const matchesDept =
      selectedDepartment.value === 'TODOS' ||
      track.targetDepartment.toLowerCase() === selectedDepartment.value.toLowerCase();
    const query = searchQuery.value.toLowerCase();
    const matchesQuery =
      !query ||
      track.title.toLowerCase().includes(query) ||
      track.description.toLowerCase().includes(query) ||
      track.modules.some((m) => m.title.toLowerCase().includes(query));
    return matchesDept && matchesQuery;
  });
});

function toggleModule(modId: string) {
  expandedModules.value[modId] = !expandedModules.value[modId];
}

function openLesson(trackId: string, lessonId: string) {
  trackStore.selectLesson(trackId, lessonId);
  router.push(`/tracks/${trackId}/lessons/${lessonId}`);
}

function continueTrack(trackId: string) {
  const track = trackStore.tracks.find((t) => t.id === trackId);
  if (!track || !track.modules || track.modules.length === 0) return;

  // Busca a primeira lição não concluída
  for (const mod of track.modules) {
    for (const les of mod.lessons) {
      if (!trackStore.isLessonCompleted(les.id)) {
        openLesson(track.id, les.id);
        return;
      }
    }
  }

  // Se todas estiverem concluídas, abre a primeira
  openLesson(track.id, track.modules[0].lessons[0].id);
}
</script>

<template>
  <div class="space-y-8 pb-16 animate-fade-in max-w-7xl mx-auto">
    <!-- Hero Banner -->
    <div class="bg-gradient-to-br from-slate-900 via-brand-950 to-slate-900 rounded-3xl p-8 sm:p-10 text-white relative overflow-hidden shadow-xl border border-slate-800">
      <div class="absolute -right-10 -bottom-10 w-96 h-96 bg-brand-500/10 rounded-full blur-3xl pointer-events-none"></div>
      <div class="relative z-10 max-w-3xl space-y-4">
        <div v-if="authStore.isGestor" class="inline-flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
          <Eye class="w-3.5 h-3.5" />
          <span>Visão Consultiva do Gestor — Catálogo de Conteúdos</span>
        </div>
        <div v-else class="inline-flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/30">
          <BookOpen class="w-3.5 h-3.5" />
          <span>Matriz de Capacitação Institucional</span>
        </div>
        <h1 class="text-2xl sm:text-4xl font-extrabold tracking-tight font-sans">
          {{ authStore.isGestor ? 'Catálogo de Trilhas da Turma' : 'Trilhas de Aprendizagem & Formação' }}
        </h1>
        <p class="text-sm sm:text-base text-slate-300 leading-relaxed">
          <span v-if="authStore.isGestor">
            Como gestor, você visualiza a grade curricular completa, módulos e ementas pedagógicas para orientar o plano de desenvolvimento dos membros da sua equipe.
          </span>
          <span v-else>
            Explore as jornadas estruturadas de integração para aprofundar seu conhecimento em governança cooperativa, regulação financeira, crédito consciente e ferramentas corporativas.
          </span>
        </p>

        <!-- Resumo Rápido de Progresso do Usuário ou da Turma -->
        <div class="pt-4 flex flex-wrap items-center gap-4 sm:gap-6 text-xs sm:text-sm">
          <template v-if="authStore.isColaborador">
            <div class="bg-white/10 backdrop-blur-md px-4 py-2 rounded-2xl border border-white/10 flex items-center space-x-2">
              <span class="text-slate-300">Seu Progresso:</span>
              <span class="font-bold text-white">{{ trackStore.overallProgressPercent }}%</span>
            </div>
            <div class="bg-white/10 backdrop-blur-md px-4 py-2 rounded-2xl border border-white/10 flex items-center space-x-2">
              <span class="text-slate-300">Lições Concluídas:</span>
              <span class="font-bold text-white">{{ trackStore.completedCount }} de {{ trackStore.totalLessonsCount }}</span>
            </div>
          </template>
          <template v-else-if="authStore.isGestor">
            <div class="bg-white/10 backdrop-blur-md px-4 py-2 rounded-2xl border border-white/10 flex items-center space-x-2">
              <span class="text-slate-300">Papel:</span>
              <span class="font-bold text-indigo-300">Gestor de Equipe</span>
            </div>
            <div class="bg-white/10 backdrop-blur-md px-4 py-2 rounded-2xl border border-white/10 flex items-center space-x-2">
              <span class="text-slate-300">Trilhas no Catálogo:</span>
              <span class="font-bold text-white">{{ trackStore.tracks.length }} Formações</span>
            </div>
            <router-link
              to="/gestao"
              class="bg-indigo-500/80 hover:bg-indigo-500 text-white font-bold px-4 py-2 rounded-2xl flex items-center space-x-1.5 transition-colors"
            >
              <Users class="w-3.5 h-3.5" />
              <span>Abrir Gestão da Turma</span>
            </router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- Filtros & Busca -->
    <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
      <!-- Campo de Busca -->
      <div class="relative flex-1 max-w-md">
        <Search class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Buscar por trilha, módulo ou conceito..."
          class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-xs sm:text-sm focus:ring-2 focus:ring-brand-500 focus:outline-hidden"
        />
      </div>

      <!-- Filtro por Departamento -->
      <div class="flex items-center space-x-2 overflow-x-auto pb-1 md:pb-0">
        <span class="text-xs font-bold text-slate-400 uppercase tracking-wide shrink-0">Área:</span>
        <button
          v-for="dept in departments"
          :key="dept"
          @click="selectedDepartment = dept"
          :class="[
            'px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer',
            selectedDepartment === dept
              ? 'bg-brand-600 text-white shadow-xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200',
          ]"
        >
          {{ dept }}
        </button>
      </div>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="trackStore.isLoading" class="grid grid-cols-1 gap-6">
      <SkeletonLoader count="card" />
      <SkeletonLoader count="card" />
    </div>

    <!-- Lista de Trilhas -->
    <div v-else class="space-y-6">
      <div
        v-for="track in filteredTracks"
        :key="track.id"
        class="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden transition-all duration-300 hover:border-brand-300"
      >
        <!-- Topo do Card da Trilha -->
        <div class="p-6 sm:p-8 border-b border-slate-100 flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div class="space-y-3 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold uppercase tracking-wider bg-brand-50 text-brand-800 border border-brand-200">
                {{ track.targetDepartment }}
              </span>
              <span class="inline-flex items-center gap-1 text-xs text-slate-500 font-medium">
                <Clock class="w-3.5 h-3.5 text-slate-400" /> {{ track.estimatedHours }}h estimadas
              </span>
              <span class="inline-flex items-center gap-1 text-xs text-slate-500 font-medium">
                <Calendar class="w-3.5 h-3.5 text-slate-400" /> SLA: {{ track.slaDays }} dias
              </span>
            </div>

            <h2 class="text-xl sm:text-2xl font-bold text-slate-900 font-sans">
              {{ track.title }}
            </h2>
            <p class="text-xs sm:text-sm text-slate-600 leading-relaxed max-w-3xl">
              {{ track.description }}
            </p>
          </div>

          <!-- Lado Direito: Progresso e Ação -->
          <div class="shrink-0 flex flex-col sm:flex-row lg:flex-col items-start sm:items-center lg:items-end justify-between gap-4">
            <div v-if="authStore.isColaborador" class="w-full sm:w-48 text-left sm:text-right">
              <div class="flex items-center justify-between text-xs font-bold text-slate-700 mb-1">
                <span>Progresso</span>
                <span>{{ trackStore.getTrackProgressPercent(track.id) }}%</span>
              </div>
              <ProgressBar :value="trackStore.getTrackProgressPercent(track.id)" variant="brand" />
            </div>
            <div v-else-if="authStore.isGestor" class="px-3 py-1.5 rounded-xl bg-indigo-50 text-indigo-800 border border-indigo-200 text-xs font-bold flex items-center gap-1.5">
              <Eye class="w-3.5 h-3.5 text-indigo-600" />
              <span>Grade Curricular da Turma</span>
            </div>

            <button
              @click="continueTrack(track.id)"
              class="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-5 py-3 rounded-2xl text-xs sm:text-sm font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-md shadow-brand-600/20 hover:scale-[1.02] active:scale-[0.98] transition-all cursor-pointer"
            >
              <span>{{ authStore.isGestor ? 'Consultar Módulos da Trilha' : 'Continuar Trilha' }}</span>
              <ArrowRight class="w-4 h-4" />
            </button>
          </div>
        </div>

        <!-- Lista de Módulos & Lições da Trilha -->
        <div class="bg-slate-50/50 p-6 sm:p-8 space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-xs font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1.5">
              <Layers class="w-4 h-4 text-brand-600" />
              <span>Módulos de Formação ({{ track.modules.length }})</span>
            </h3>
            <span class="text-xs text-slate-400">Clique no módulo para expandir as lições</span>
          </div>

          <div class="space-y-3">
            <div
              v-for="module in track.modules"
              :key="module.id"
              class="bg-white rounded-2xl border border-slate-200/70 overflow-hidden shadow-2xs"
            >
              <!-- Cabeçalho do Módulo -->
              <button
                @click="toggleModule(module.id)"
                class="w-full p-4 text-left flex items-center justify-between hover:bg-slate-50/80 transition-colors cursor-pointer"
              >
                <div class="flex items-center space-x-3">
                  <div class="w-7 h-7 rounded-lg bg-brand-50 text-brand-700 flex items-center justify-center text-xs font-bold">
                    {{ module.orderIndex }}
                  </div>
                  <div>
                    <h4 class="text-xs sm:text-sm font-bold text-slate-900">{{ module.title }}</h4>
                    <p class="text-[11px] text-slate-500 line-clamp-1">{{ module.description }}</p>
                  </div>
                </div>

                <div class="flex items-center space-x-3 text-xs text-slate-400">
                  <span>{{ module.lessons.length }} lições</span>
                  <ChevronUp v-if="expandedModules[module.id]" class="w-4 h-4 text-slate-500" />
                  <ChevronDown v-else class="w-4 h-4 text-slate-500" />
                </div>
              </button>

              <!-- Lições do Módulo (Expandível, aberto por padrão se não definido) -->
              <div
                v-if="expandedModules[module.id] !== false"
                class="border-t border-slate-100 divide-y divide-slate-100 bg-slate-50/30"
              >
                <div
                  v-for="lesson in module.lessons"
                  :key="lesson.id"
                  @click="openLesson(track.id, lesson.id)"
                  class="p-3.5 sm:px-6 flex items-center justify-between hover:bg-white transition-colors cursor-pointer group"
                >
                  <div class="flex items-center space-x-3">
                    <template v-if="authStore.isColaborador">
                      <CheckCircle2
                        v-if="trackStore.isLessonCompleted(lesson.id)"
                        class="w-4 h-4 text-emerald-500 shrink-0"
                      />
                      <Circle
                        v-else
                        class="w-4 h-4 text-slate-300 group-hover:text-brand-500 shrink-0 transition-colors"
                      />
                    </template>
                    <BookOpen
                      v-else
                      class="w-4 h-4 text-indigo-500 group-hover:text-brand-600 shrink-0 transition-colors"
                    />
                    <div>
                      <p class="text-xs sm:text-sm font-medium text-slate-800 group-hover:text-brand-700 transition-colors">
                        {{ lesson.title }}
                      </p>
                    </div>
                  </div>

                  <div class="flex items-center space-x-3 text-xs text-slate-400">
                    <span class="hidden sm:inline">{{ lesson.estimatedMinutes }} min</span>
                    <span
                      v-if="authStore.isColaborador && trackStore.isLessonCompleted(lesson.id)"
                      class="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200"
                    >
                      Concluída
                    </span>
                    <span
                      v-else-if="authStore.isGestor"
                      class="px-2 py-0.5 rounded text-[10px] font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200"
                    >
                      Leitura Pedagógica
                    </span>
                    <span
                      v-else
                      class="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-600 group-hover:bg-brand-50 group-hover:text-brand-700 transition-colors"
                    >
                      Iniciar
                    </span>
                    <ArrowRight class="w-3.5 h-3.5 text-slate-400 group-hover:text-brand-600 group-hover:translate-x-0.5 transition-all" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
