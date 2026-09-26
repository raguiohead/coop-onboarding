<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useAiTutorStore } from '@/stores/aiTutor';
import { useQuizStore, type QuizItem } from '@/stores/quizzes';
import QuizGeneratorModal from '@/components/ai/QuizGeneratorModal.vue';
import {
  HelpCircle,
  Sparkles,
  CheckCircle2,
  Clock,
  Award,
  Play,
  Check,
  X,
  RotateCcw,
  BookOpen,
  ArrowRight,
  TrendingUp,
  Eye,
  ShieldCheck,
} from 'lucide-vue-next';

const authStore = useAuthStore();
const aiTutorStore = useAiTutorStore();
const quizStore = useQuizStore();

const isQuizModalOpen = ref(false);
const activeQuiz = ref<any | null>(null);
const currentQuestionIndex = ref(0);
const selectedAnswers = ref<Record<number, number>>({});
const showResults = ref(false);

// Modo de visualização de gabarito para gestor
const isInspectionMode = ref(false);

function startQuiz(quiz: any) {
  if (authStore.isGestor || authStore.isAdmin) {
    // Gestores entram direto em modo de inspeção pedagógica (apenas visualização)
    inspectQuiz(quiz);
    return;
  }
  activeQuiz.value = quiz;
  isInspectionMode.value = false;
  currentQuestionIndex.value = 0;
  selectedAnswers.value = quiz.answers ? { ...quiz.answers } : {};
  showResults.value = quiz.status === 'COMPLETED';
}

function inspectQuiz(quiz: any) {
  activeQuiz.value = quiz;
  isInspectionMode.value = true;
  currentQuestionIndex.value = 0;
  showResults.value = false;
}

function selectAnswer(questionIdx: number, optionIdx: number) {
  if (showResults.value || isInspectionMode.value) return;
  selectedAnswers.value[questionIdx] = optionIdx;
}

function nextQuestion() {
  if (!activeQuiz.value) return;
  if (currentQuestionIndex.value < activeQuiz.value.questions.length - 1) {
    currentQuestionIndex.value++;
  } else if (!isInspectionMode.value) {
    finishQuiz();
  }
}

function previousQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
  }
}

function finishQuiz() {
  if (!activeQuiz.value || isInspectionMode.value) return;
  let correctCount = 0;
  activeQuiz.value.questions.forEach((q: any, idx: number) => {
    if (selectedAnswers.value[idx] === q.correctIndex) {
      correctCount++;
    }
  });

  const percentage = Math.round((correctCount / activeQuiz.value.questions.length) * 100);
  quizStore.saveQuizAttempt(activeQuiz.value.id, percentage, selectedAnswers.value);
  showResults.value = true;
}

function resetActiveQuiz() {
  if (activeQuiz.value && !isInspectionMode.value) {
    quizStore.resetQuizAttempt(activeQuiz.value.id);
    selectedAnswers.value = {};
    currentQuestionIndex.value = 0;
    showResults.value = false;
  }
}

function closeQuizRunner() {
  activeQuiz.value = null;
  isInspectionMode.value = false;
  showResults.value = false;
  selectedAnswers.value = {};
  currentQuestionIndex.value = 0;
}
</script>

<template>
  <div class="space-y-8 pb-16">
    <!-- Top Header Banner -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gradient-to-r from-indigo-900 via-slate-900 to-brand-950 p-6 sm:p-8 rounded-3xl text-white shadow-xl border border-slate-700/50">
      <div>
        <div class="inline-flex items-center space-x-2 px-2.5 py-1 rounded-md text-[11px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 uppercase tracking-wider mb-2">
          <Award class="w-3.5 h-3.5" />
          <span v-if="authStore.isGestor || authStore.isAdmin">Banco Pedagógico de Avaliações (Modo Consulta)</span>
          <span v-else>Avaliações & Fixação Pedagógica</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight font-sans">
          {{ (authStore.isGestor || authStore.isAdmin) ? 'Banco de Quizzes da Turma' : 'Central de Quizzes & Simulados' }}
        </h1>
        <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-2xl">
          <span v-if="authStore.isGestor || authStore.isAdmin">
            Supervisão pedagógica dos simulados da turma. Como gestor ou administrador, você visualiza todas as questões e gabaritos comentados com fundamentação regulatória para orientar a equipe. A realização com notas é exclusiva para os colaboradores.
          </span>
          <span v-else>
            Teste seus conhecimentos nos pilares cooperativistas, diretrizes normativas e políticas de crédito com justificativas pedagógicas detalhadas.
          </span>
        </p>
      </div>

      <!-- Action Button for Gestores/Admins -->
      <div v-if="authStore.canGenerateQuiz" class="shrink-0">
        <button
          @click="isQuizModalOpen = true"
          class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/25 transition-all hover:scale-[1.02] cursor-pointer"
        >
          <Sparkles class="w-4 h-4 text-ai-100" />
          <span>Gerar Novo Quiz com IA</span>
        </button>
      </div>
    </div>

    <!-- Metrics Cards (Diferenciados por Papel) -->
    <div v-if="authStore.isColaborador" class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-teal-50 border border-teal-100 flex items-center justify-center text-teal-600">
          <CheckCircle2 class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Seus Quizzes Concluídos</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">
            {{ quizStore.completedQuizzesCount }} de {{ quizStore.quizzes.length }}
          </p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600">
          <TrendingUp class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Sua Média de Acertos</p>
          <p class="text-xl font-extrabold text-indigo-700 mt-0.5">
            {{ quizStore.averageQuizScore > 0 ? `${quizStore.averageQuizScore}%` : 'Pendente' }}
          </p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-amber-50 border border-amber-100 flex items-center justify-center text-amber-600">
          <Clock class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Pendentes na sua Trilha</p>
          <p class="text-xl font-extrabold text-amber-700 mt-0.5">
            {{ quizStore.quizzes.length - quizStore.completedQuizzesCount }} avaliações
          </p>
        </div>
      </div>
    </div>

    <!-- Metrics Cards para Gestor / Admin -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600">
          <BookOpen class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Quizzes no Banco</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">
            {{ quizStore.quizzes.length }} Simulados
          </p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-teal-50 border border-teal-100 flex items-center justify-center text-teal-600">
          <ShieldCheck class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Aproveitamento Médio da Turma</p>
          <p class="text-xl font-extrabold text-teal-700 mt-0.5">92%</p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600">
          <Award class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Papel Operacional</p>
          <p class="text-xl font-extrabold text-indigo-900 mt-0.5">Supervisão Pedagógica</p>
        </div>
      </div>
    </div>

    <!-- Active Quiz Runner / Inspection View -->
    <div v-if="activeQuiz" class="bg-white rounded-3xl border border-indigo-100 shadow-xl p-6 sm:p-8 animate-fade-in">
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
        <div>
          <div class="flex items-center space-x-2">
            <span class="text-[11px] font-bold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-100">
              {{ activeQuiz.category }} · Questão {{ currentQuestionIndex + 1 }} de {{ activeQuiz.questions.length }}
            </span>
            <span v-if="isInspectionMode" class="text-[11px] font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200 uppercase">
              Modo Consulta Pedagógica ({{ authStore.isAdmin ? 'Admin' : 'Gestor' }})
            </span>
          </div>
          <h2 class="text-lg sm:text-xl font-bold text-slate-900 mt-1">
            {{ activeQuiz.title }}
          </h2>
        </div>
        <button
          @click="closeQuizRunner"
          class="text-xs text-slate-500 hover:text-slate-800 font-bold px-3 py-1.5 rounded-xl hover:bg-slate-100 transition-colors cursor-pointer"
        >
          Voltar para Lista
        </button>
      </div>

      <!-- Modo Inspeção para Gestor: Exibe Questão com Gabarito e Justificativa -->
      <div v-if="isInspectionMode" class="space-y-6">
        <div class="p-4 sm:p-5 rounded-2xl bg-indigo-50/50 border border-indigo-100">
          <p class="text-xs font-bold text-indigo-700 uppercase tracking-wide mb-1">Enunciado da Questão:</p>
          <p class="text-sm sm:text-base font-semibold text-slate-900 leading-relaxed">
            {{ activeQuiz.questions[currentQuestionIndex].text }}
          </p>
        </div>

        <div class="space-y-3">
          <div
            v-for="(option, optIdx) in activeQuiz.questions[currentQuestionIndex].options"
            :key="optIdx"
            :class="[
              'p-4 rounded-2xl border text-xs sm:text-sm font-medium transition-all flex items-start space-x-3',
              optIdx === activeQuiz.questions[currentQuestionIndex].correctIndex
                ? 'bg-emerald-50 border-emerald-500 text-emerald-950 font-bold ring-2 ring-emerald-400/20'
                : 'bg-white border-slate-200 text-slate-600 opacity-75'
            ]"
          >
            <span
              :class="[
                'w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold shrink-0 mt-0.5',
                optIdx === activeQuiz.questions[currentQuestionIndex].correctIndex
                  ? 'bg-emerald-600 text-white'
                  : 'bg-slate-100 text-slate-500'
              ]"
            >
              {{ String.fromCharCode(65 + optIdx) }}
            </span>
            <div class="flex-1">
              <span>{{ option }}</span>
              <span v-if="optIdx === activeQuiz.questions[currentQuestionIndex].correctIndex" class="ml-2 text-xs text-emerald-700 font-bold">
                (Gabarito Correto)
              </span>
            </div>
          </div>
        </div>

        <!-- Explicação Pedagógica e Legal -->
        <div class="p-4 rounded-2xl bg-emerald-50/80 border border-emerald-200 text-xs sm:text-sm text-emerald-950 space-y-1">
          <p class="font-bold flex items-center gap-1.5 text-emerald-900">
            <CheckCircle2 class="w-4 h-4 text-emerald-600" /> Fundamentação Regulatória & Doutrinária:
          </p>
          <p class="leading-relaxed">
            {{ activeQuiz.questions[currentQuestionIndex].explanation }}
          </p>
        </div>

        <!-- Controles de Navegação do Gestor -->
        <div class="flex items-center justify-between pt-4 border-t border-slate-100">
          <button
            @click="previousQuestion"
            :disabled="currentQuestionIndex === 0"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 text-slate-700 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 transition-colors"
          >
            Questão Anterior
          </button>

          <span class="text-xs text-slate-500 font-medium">
            Questão {{ currentQuestionIndex + 1 }} de {{ activeQuiz.questions.length }}
          </span>

          <button
            v-if="currentQuestionIndex < activeQuiz.questions.length - 1"
            @click="nextQuestion"
            class="px-4 py-2 rounded-xl text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white transition-colors"
          >
            Próxima Questão
          </button>
          <button
            v-else
            @click="closeQuizRunner"
            class="px-4 py-2 rounded-xl text-xs font-bold bg-slate-900 hover:bg-slate-800 text-white transition-colors"
          >
            Concluir Inspeção
          </button>
        </div>
      </div>

      <!-- Modo Colaborador: Realização do Quiz -->
      <div v-else-if="!showResults" class="space-y-6">
        <div class="p-4 sm:p-5 rounded-2xl bg-slate-50 border border-slate-200/80">
          <p class="text-sm sm:text-base font-semibold text-slate-900 leading-relaxed">
            {{ activeQuiz.questions[currentQuestionIndex].text }}
          </p>
        </div>

        <!-- Lista de Alternativas -->
        <div class="space-y-3">
          <button
            v-for="(option, optIdx) in activeQuiz.questions[currentQuestionIndex].options"
            :key="optIdx"
            @click="selectAnswer(currentQuestionIndex, optIdx)"
            :class="[
              'w-full text-left p-4 rounded-2xl border text-xs sm:text-sm font-medium transition-all flex items-start space-x-3 cursor-pointer',
              selectedAnswers[currentQuestionIndex] === optIdx
                ? 'bg-indigo-50/80 border-indigo-500 text-indigo-950 shadow-xs ring-2 ring-indigo-400/20'
                : 'bg-white border-slate-200 hover:border-slate-300 hover:bg-slate-50/50 text-slate-800'
            ]"
          >
            <span
              :class="[
                'w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold shrink-0 mt-0.5',
                selectedAnswers[currentQuestionIndex] === optIdx
                  ? 'bg-indigo-600 text-white'
                  : 'bg-slate-100 text-slate-600'
              ]"
            >
              {{ String.fromCharCode(65 + optIdx) }}
            </span>
            <span class="flex-1">{{ option }}</span>
          </button>
        </div>

        <!-- Controles de Navegação -->
        <div class="flex items-center justify-between pt-4 border-t border-slate-100">
          <button
            @click="previousQuestion"
            :disabled="currentQuestionIndex === 0"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 text-slate-700 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-50 transition-colors cursor-pointer"
          >
            Anterior
          </button>

          <span class="text-xs text-slate-400 font-medium">
            Questão {{ currentQuestionIndex + 1 }} de {{ activeQuiz.questions.length }}
          </span>

          <button
            @click="nextQuestion"
            :disabled="selectedAnswers[currentQuestionIndex] === undefined"
            class="px-5 py-2.5 rounded-xl text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white shadow-md shadow-indigo-600/20 disabled:opacity-40 disabled:cursor-not-allowed transition-all cursor-pointer"
          >
            {{ currentQuestionIndex < activeQuiz.questions.length - 1 ? 'Próxima Questão' : 'Finalizar Simulado' }}
          </button>
        </div>
      </div>

      <!-- Tela de Resultados do Colaborador -->
      <div v-else class="space-y-6">
        <div class="p-6 sm:p-8 rounded-3xl bg-gradient-to-br from-indigo-50 via-white to-teal-50 border border-indigo-100 text-center space-y-3">
          <div class="w-16 h-16 mx-auto rounded-2xl bg-indigo-600 text-white flex items-center justify-center shadow-lg shadow-indigo-600/25">
            <Award class="w-8 h-8" />
          </div>
          <h3 class="text-xl sm:text-2xl font-black text-slate-900">
            Simulado Concluído com Sucesso!
          </h3>
          <p class="text-xs sm:text-sm text-slate-600 max-w-md mx-auto">
            Sua nota foi computada no plano de capacitação da cooperativa. Confira abaixo o gabarito comentado.
          </p>

          <div class="pt-4 flex items-center justify-center space-x-6 text-sm">
            <div>
              <p class="text-xs text-slate-400 uppercase font-bold">Aproveitamento</p>
              <p class="text-3xl font-black text-indigo-700">{{ activeQuiz.score ?? 100 }}%</p>
            </div>
            <div class="w-px h-10 bg-slate-200"></div>
            <div>
              <p class="text-xs text-slate-400 uppercase font-bold">Status</p>
              <p class="text-sm font-bold text-teal-600 flex items-center gap-1 mt-1">
                <CheckCircle2 class="w-4 h-4" /> Qualificado
              </p>
            </div>
          </div>
        </div>

        <!-- Gabarito Comentado -->
        <div class="space-y-4">
          <h4 class="text-xs font-bold uppercase tracking-wider text-slate-400">
            Gabarito & Fundamentação Normativa
          </h4>

          <div
            v-for="(q, idx) in activeQuiz.questions"
            :key="q.id"
            class="p-4 sm:p-5 rounded-2xl border space-y-3"
            :class="selectedAnswers[idx] === q.correctIndex ? 'bg-emerald-50/40 border-emerald-200' : 'bg-rose-50/40 border-rose-200'"
          >
            <div class="flex items-start justify-between gap-2">
              <p class="text-xs sm:text-sm font-bold text-slate-900">
                Questão {{ idx + 1 }}: {{ q.text }}
              </p>
              <span
                class="shrink-0 text-[10px] font-bold px-2 py-0.5 rounded border uppercase"
                :class="selectedAnswers[idx] === q.correctIndex ? 'bg-emerald-100 text-emerald-800 border-emerald-300' : 'bg-rose-100 text-rose-800 border-rose-300'"
              >
                {{ selectedAnswers[idx] === q.correctIndex ? 'Acertou' : 'Errou' }}
              </span>
            </div>

            <div class="text-xs space-y-1">
              <p class="text-slate-600">
                <strong>Sua Resposta:</strong> {{ q.options[selectedAnswers[idx]] ?? 'Não respondida' }}
              </p>
              <p class="text-emerald-700 font-semibold">
                <strong>Resposta Correta:</strong> {{ q.options[q.correctIndex] }}
              </p>
            </div>

            <div class="p-3 rounded-xl bg-white/80 border border-slate-200/60 text-xs text-slate-600">
              <p class="font-semibold text-slate-800">Por que esta resposta está correta?</p>
              <p class="mt-0.5 leading-relaxed">{{ q.explanation }}</p>
            </div>
          </div>
        </div>

        <!-- Botões de Ação Final -->
        <div class="flex flex-wrap items-center justify-between gap-4 pt-4 border-t border-slate-100">
          <button
            @click="resetActiveQuiz"
            class="px-4 py-2.5 rounded-xl text-xs font-bold border border-slate-200 text-slate-700 hover:bg-slate-50 transition-colors flex items-center space-x-1.5 cursor-pointer"
          >
            <RotateCcw class="w-3.5 h-3.5" />
            <span>Refazer Avaliação</span>
          </button>

          <button
            @click="closeQuizRunner"
            class="px-5 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-slate-900 hover:bg-slate-800 text-white shadow-xs transition-colors cursor-pointer"
          >
            Finalizar & Voltar à Lista
          </button>
        </div>
      </div>
    </div>

    <!-- Quizzes Cards Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="quiz in quizStore.quizzesForCurrentUser"
        :key="quiz.id"
        class="bg-white rounded-3xl p-6 border border-slate-200/80 shadow-xs hover:shadow-md transition-all flex flex-col justify-between"
      >
        <div>
          <!-- Badge Header -->
          <div class="flex items-center justify-between gap-2 mb-3">
            <span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-indigo-50 text-indigo-700 border border-indigo-100">
              {{ quiz.category }}
            </span>
            <span
              v-if="authStore.isColaborador"
              :class="[
                'text-[10px] font-bold px-2 py-0.5 rounded border uppercase',
                quiz.status === 'COMPLETED'
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                  : 'bg-amber-50 text-amber-700 border-amber-200'
              ]"
            >
              {{ quiz.status === 'COMPLETED' ? 'Concluído' : 'Pendente' }}
            </span>
            <span
              v-else
              class="text-[10px] font-bold px-2 py-0.5 rounded border uppercase bg-indigo-50 text-indigo-700 border-indigo-200"
            >
              Supervisão
            </span>
          </div>

          <h3 class="text-base font-bold text-slate-900 leading-snug mb-2">
            {{ quiz.title }}
          </h3>

          <div class="flex items-center space-x-3 text-xs text-slate-500 mb-6">
            <span class="flex items-center gap-1">
              <HelpCircle class="w-3.5 h-3.5 text-slate-400" /> {{ quiz.questionsCount }} questões
            </span>
            <span>·</span>
            <span class="flex items-center gap-1">
              <Clock class="w-3.5 h-3.5 text-slate-400" /> ~{{ quiz.estimatedMinutes }} min
            </span>
          </div>
        </div>

        <div>
          <!-- Nota para colaborador -->
          <div v-if="authStore.isColaborador && quiz.status === 'COMPLETED'" class="mb-4 p-3 rounded-xl bg-emerald-50/60 border border-emerald-100 flex items-center justify-between text-xs">
            <span class="text-emerald-800 font-semibold">Sua Nota Registrada:</span>
            <span class="text-sm font-black text-emerald-700">{{ quiz.score }}%</span>
          </div>

          <!-- Botão para Gestor / Admin: Visualizar Questões & Gabarito -->
          <button
            v-if="authStore.isGestor || authStore.isAdmin"
            @click="inspectQuiz(quiz)"
            class="w-full flex items-center justify-center space-x-2 py-2.5 px-4 rounded-xl text-xs sm:text-sm font-bold bg-indigo-50 text-indigo-700 hover:bg-indigo-100 border border-indigo-200 transition-all cursor-pointer"
          >
            <Eye class="w-4 h-4" />
            <span>Visualizar Questões & Gabarito</span>
          </button>

          <!-- Botão para Colaborador: Fazer Simulado / Revisar -->
          <button
            v-else
            @click="startQuiz(quiz)"
            class="w-full flex items-center justify-center space-x-2 py-2.5 px-4 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer"
            :class="quiz.status === 'COMPLETED'
              ? 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              : 'bg-gradient-to-r from-brand-600 to-brand-800 text-white hover:from-brand-700 hover:to-brand-900 shadow-sm'"
          >
            <Play v-if="quiz.status === 'PENDING'" class="w-4 h-4 fill-current" />
            <RotateCcw v-else class="w-3.5 h-3.5" />
            <span>{{ quiz.status === 'COMPLETED' ? 'Revisar / Refazer Quiz' : 'Iniciar Avaliação' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Modal do Gerador de Quiz (para Gestores/Admins) -->
    <QuizGeneratorModal
      :is-open="isQuizModalOpen"
      lesson-id="d1a2b3c4-0001-4000-8000-000000000001"
      lesson-title="Diretrizes Cooperativistas & Políticas de Crédito"
      @close="isQuizModalOpen = false"
    />
  </div>
</template>
