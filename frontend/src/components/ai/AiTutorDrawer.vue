<script setup lang="ts">
import { ref, computed, nextTick, watch } from 'vue';
import MarkdownIt from 'markdown-it';
import { useAiTutorStore } from '@/stores/aiTutor';
import { useTrackStore } from '@/stores/tracks';
import {
  X,
  Send,
  Sparkles,
  Bot,
  User,
  BookOpen,
  ChevronDown,
  ChevronUp,
  RotateCcw,
  ShieldCheck,
  HelpCircle,
} from 'lucide-vue-next';

const aiTutorStore = useAiTutorStore();
const trackStore = useTrackStore();
const md = new MarkdownIt({
  html: false,
  breaks: true,
  linkify: true,
});

const userInput = ref('');
const messagesContainer = ref<HTMLDivElement | null>(null);
const expandedSources = ref<Record<string, boolean>>({});

const currentLesson = computed(() => trackStore.activeLesson);

const quickSuggestions = [
  'Resuma os pontos principais desta aula',
  'Qual a diferença entre lucro e sobras?',
  'Quais são as regras de segurança e LGPD?',
  'Como este tema se aplica à governança?',
];

function toggleSources(messageId: string) {
  expandedSources.value[messageId] = !expandedSources.value[messageId];
}

function renderMarkdown(content: string): string {
  return md.render(content);
}

async function scrollToBottom() {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
}

watch(
  () => aiTutorStore.messages.length,
  () => {
    scrollToBottom();
  }
);

watch(
  () => aiTutorStore.isThinking,
  (thinking) => {
    if (thinking) {
      scrollToBottom();
    }
  }
);

async function handleSendMessage(customPrompt?: string) {
  const query = customPrompt || userInput.value;
  if (!query.trim()) return;

  const lessonId = currentLesson.value?.id || 'les-general';
  const lessonTitle = currentLesson.value?.title;
  const lessonContent = currentLesson.value?.contentMarkdown;

  userInput.value = '';
  await aiTutorStore.askQuestion(lessonId, query, lessonTitle, lessonContent);
  scrollToBottom();
}

function handleKeyDown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    handleSendMessage();
  }
}
</script>

<template>
  <div>
    <!-- Backdrop Blur Overlay -->
    <transition
      enter-active-class="transition-opacity duration-300 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-200 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="aiTutorStore.isDrawerOpen"
        @click="aiTutorStore.closeDrawer"
        class="fixed inset-0 z-40 bg-slate-900/50 backdrop-blur-xs transition-all"
        aria-hidden="true"
      ></div>
    </transition>

    <!-- Slide-over Drawer Panel -->
    <transition
      enter-active-class="transform transition ease-out duration-300 sm:duration-300"
      enter-from-class="translate-x-full"
      enter-to-class="translate-x-0"
      leave-active-class="transform transition ease-in duration-200 sm:duration-200"
      leave-from-class="translate-x-0"
      leave-to-class="translate-x-full"
    >
      <div
        v-if="aiTutorStore.isDrawerOpen"
        class="fixed inset-y-0 right-0 z-50 flex max-w-full pl-10 w-screen max-w-md sm:max-w-lg focus:outline-hidden"
      >
        <div class="w-full flex flex-col bg-white shadow-2xl border-l border-slate-200">
          <!-- Drawer Header -->
          <div class="px-5 py-4 border-b border-slate-100 bg-gradient-to-r from-slate-900 via-slate-800 to-indigo-950 text-white flex items-center justify-between shadow-xs">
            <div class="flex items-center space-x-3">
              <div class="relative w-10 h-10 rounded-xl bg-gradient-to-tr from-ai-500 to-indigo-500 flex items-center justify-center text-white shadow-md shadow-ai-500/30 ring-2 ring-indigo-400/40">
                <Sparkles class="w-5 h-5 text-indigo-100" />
                <span class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-emerald-400 border-2 border-slate-900 rounded-full"></span>
              </div>
              <div>
                <h3 class="text-sm font-bold text-white flex items-center gap-1.5 tracking-tight">
                  Tutor Virtual de Onboarding
                  <span class="px-1.5 py-0.2 rounded text-[10px] font-semibold bg-ai-500/30 text-ai-200 border border-ai-400/30">
                    RAG IA
                  </span>
                </h3>
                <p class="text-xs text-slate-300 truncate max-w-[240px]">
                  Contexto: {{ currentLesson ? currentLesson.title : 'Visão Geral' }}
                </p>
              </div>
            </div>

            <div class="flex items-center space-x-1">
              <button
                @click="aiTutorStore.clearHistory"
                class="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-white/10 transition-colors"
                title="Limpar histórico do chat"
              >
                <RotateCcw class="w-4 h-4" />
              </button>
              <button
                @click="aiTutorStore.closeDrawer"
                class="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-white/10 transition-colors"
                title="Fechar drawer"
              >
                <X class="w-5 h-5" />
              </button>
            </div>
          </div>

          <!-- Quick Context / Info Banner -->
          <div class="px-4 py-2 bg-brand-50/70 border-b border-brand-100 flex items-center justify-between text-[11px] text-brand-800">
            <span class="flex items-center gap-1 font-medium">
              <ShieldCheck class="w-3.5 h-3.5 text-brand-600" /> Respostas fundamentadas nas normativas e no estatuto
            </span>
            <span class="text-slate-500">RAG Vetorial Ativo</span>
          </div>

          <!-- Messages Container -->
          <div
            ref="messagesContainer"
            class="flex-1 overflow-y-auto p-4 sm:p-5 space-y-4 bg-slate-50/50"
          >
            <div
              v-for="msg in aiTutorStore.messages"
              :key="msg.id"
              :class="[
                'flex gap-3',
                msg.sender === 'user' ? 'justify-end' : 'justify-start'
              ]"
            >
              <!-- Tutor Avatar -->
              <div
                v-if="msg.sender === 'tutor'"
                class="w-8 h-8 rounded-xl bg-gradient-to-tr from-ai-500 to-indigo-600 text-white flex items-center justify-center shrink-0 shadow-xs mt-0.5"
              >
                <Bot class="w-4 h-4" />
              </div>

              <!-- Message Body -->
              <div
                :class="[
                  'max-w-[85%] rounded-2xl p-4 shadow-xs text-xs sm:text-sm',
                  msg.sender === 'user'
                    ? 'bg-brand-700 text-white rounded-br-xs'
                    : 'bg-white text-slate-800 border border-slate-200/80 rounded-bl-xs'
                ]"
              >
                <!-- Text or Rendered Markdown -->
                <div
                  v-if="msg.sender === 'tutor'"
                  class="markdown-body leading-relaxed text-slate-700 text-xs sm:text-[13px]"
                  v-html="renderMarkdown(msg.text)"
                ></div>
                <div v-else class="whitespace-pre-wrap leading-relaxed">
                  {{ msg.text }}
                </div>

                <!-- Timestamp -->
                <div
                  :class="[
                    'text-[10px] mt-2 flex items-center justify-between',
                    msg.sender === 'user' ? 'text-teal-200' : 'text-slate-400'
                  ]"
                >
                  <span>{{ msg.timestamp }}</span>
                  <span v-if="msg.sender === 'tutor'" class="flex items-center gap-1 text-ai-600 font-semibold">
                    <Sparkles class="w-2.5 h-2.5" /> IA Verificada
                  </span>
                </div>

                <!-- RAG Sources Accordion -->
                <div
                  v-if="msg.sources && msg.sources.length > 0"
                  class="mt-3 pt-2.5 border-t border-slate-100"
                >
                  <button
                    @click="toggleSources(msg.id)"
                    class="w-full flex items-center justify-between text-[11px] font-semibold text-slate-600 hover:text-ai-600 transition-colors"
                  >
                    <span class="flex items-center gap-1.5">
                      <BookOpen class="w-3.5 h-3.5 text-ai-500" />
                      Fontes e Trechos Citados ({{ msg.sources.length }})
                    </span>
                    <ChevronUp v-if="expandedSources[msg.id]" class="w-3.5 h-3.5" />
                    <ChevronDown v-else class="w-3.5 h-3.5" />
                  </button>

                  <div
                    v-if="expandedSources[msg.id]"
                    class="mt-2 space-y-1.5 pl-2 border-l-2 border-ai-300 text-[11px] text-slate-600"
                  >
                    <div
                      v-for="(source, sIdx) in msg.sources"
                      :key="sIdx"
                      class="bg-slate-100/80 p-2 rounded-md font-mono text-[10.5px] leading-tight text-slate-700"
                    >
                      {{ source }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- User Avatar -->
              <div
                v-if="msg.sender === 'user'"
                class="w-8 h-8 rounded-xl bg-slate-800 text-white flex items-center justify-center shrink-0 shadow-xs mt-0.5"
              >
                <User class="w-4 h-4" />
              </div>
            </div>

            <!-- Thinking Indicator -->
            <div v-if="aiTutorStore.isThinking" class="flex gap-3 justify-start animate-fade-in">
              <div class="w-8 h-8 rounded-xl bg-gradient-to-tr from-ai-500 to-indigo-600 text-white flex items-center justify-center shrink-0 shadow-xs">
                <Bot class="w-4 h-4 animate-spin" />
              </div>
              <div class="bg-white border border-indigo-100 text-slate-700 rounded-2xl rounded-bl-xs p-3.5 shadow-xs text-xs flex items-center space-x-2">
                <div class="flex space-x-1">
                  <span class="w-2 h-2 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.3s]"></span>
                  <span class="w-2 h-2 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.15s]"></span>
                  <span class="w-2 h-2 bg-indigo-500 rounded-full animate-bounce"></span>
                </div>
                <span class="text-xs font-medium text-indigo-900">Consultando base vetorial RAG e sintetizando resposta...</span>
              </div>
            </div>
          </div>

          <!-- Quick Suggestion Chips -->
          <div class="p-3 bg-white border-t border-slate-100 flex flex-wrap gap-1.5">
            <button
              v-for="(chip, cIdx) in quickSuggestions"
              :key="cIdx"
              @click="handleSendMessage(chip)"
              :disabled="aiTutorStore.isThinking"
              class="text-[11px] px-2.5 py-1 rounded-full bg-slate-100 hover:bg-ai-50 hover:text-ai-700 text-slate-600 transition-colors border border-slate-200/80 disabled:opacity-50"
            >
              {{ chip }}
            </button>
          </div>

          <!-- Chat Input -->
          <div class="p-4 bg-white border-t border-slate-200">
            <div class="relative flex items-center">
              <input
                v-model="userInput"
                @keydown="handleKeyDown"
                :disabled="aiTutorStore.isThinking"
                type="text"
                placeholder="Pergunte qualquer coisa sobre a lição ou normas..."
                class="w-full pl-4 pr-12 py-3 rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-ai-500 focus:border-ai-500 text-sm text-slate-900 placeholder:text-slate-400 bg-slate-50 focus:bg-white transition-all disabled:opacity-60"
              />
              <button
                @click="() => handleSendMessage()"
                :disabled="!userInput.trim() || aiTutorStore.isThinking"
                class="absolute right-2 p-2 rounded-lg bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white disabled:opacity-40 transition-all"
                title="Enviar pergunta"
              >
                <Send class="w-4 h-4" />
              </button>
            </div>
            <p class="text-[10px] text-slate-400 mt-2 text-center">
              Pressione <kbd class="px-1 py-0.5 bg-slate-100 border border-slate-200 rounded text-[9px]">Enter</kbd> para enviar. O Tutor cita fontes regulatórias oficiais.
            </p>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>
