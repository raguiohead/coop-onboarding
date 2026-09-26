<script setup lang="ts">
import { ref } from 'vue';
import {
  Palette,
  Type,
  Maximize2,
  Box,
  Layers,
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  Info,
  Clock,
  BookOpen,
  User,
  ShieldCheck,
  Search,
  Check,
  Copy,
  Sliders,
  Eye,
  ArrowRight,
  TrendingUp,
  MessageSquare,
  HelpCircle,
  ExternalLink,
} from 'lucide-vue-next';
import ProgressBar from '@/components/common/ProgressBar.vue';
import SkeletonLoader from '@/components/common/SkeletonLoader.vue';
import { useAiTutorStore } from '@/stores/aiTutor';

const aiTutorStore = useAiTutorStore();

// Interactive State
const activeTab = ref<'tokens' | 'typography' | 'components' | 'forms' | 'voice'>('tokens');
const copiedHex = ref<string | null>(null);
const sliderProgress = ref(68);
const isSkeletonSimulated = ref(false);
const isDemoModalOpen = ref(false);
const demoToggleValue = ref(true);
const demoInputValue = ref('Maria Silva');
const demoSearchValue = ref('');

// Copy Hex to Clipboard with toast
async function copyToClipboard(hex: string) {
  try {
    await navigator.clipboard.writeText(hex);
    copiedHex.value = hex;
    setTimeout(() => {
      if (copiedHex.value === hex) copiedHex.value = null;
    }, 2000);
  } catch (err) {
    console.error('Falha ao copiar:', err);
  }
}

// Token Definitions
const coopColors = [
  { name: 'coop-50', hex: '#E6F4EA', textDark: true, note: 'Superfícies de badge ativa e fundos suaves' },
  { name: 'coop-100', hex: '#C2E7CC', textDark: true, note: 'Bordas de cartões e divisores sutis' },
  { name: 'coop-200', hex: '#8ED5A3', textDark: true, note: 'Destaques suaves intermediários' },
  { name: 'coop-300', hex: '#55BE76', textDark: false, note: 'Acentos visuais e elementos decorativos' },
  { name: 'coop-400', hex: '#29A752', textDark: false, note: 'Ícones de apoio cooperativo' },
  { name: 'coop-500', hex: '#008751', textDark: false, note: 'Verde Cooperativo Canônico — Botões primários' },
  { name: 'coop-600', hex: '#008751', textDark: false, note: 'Ação primária de navegação' },
  { name: 'coop-700', hex: '#006E42', textDark: false, note: 'Hover e active states' },
  { name: 'coop-800', hex: '#005A36', textDark: false, note: 'Verde Floresta Profundo — Títulos de alto contraste' },
  { name: 'coop-900', hex: '#00472B', textDark: false, note: 'Contraste máximo de marca' },
];

const aiColors = [
  { name: 'ai-50', hex: '#EEF2FF', textDark: true, note: 'Superfície de respostas do Tutor IA' },
  { name: 'ai-100', hex: '#E0E7FF', textDark: true, note: 'Bordas e chips de perguntas sugeridas' },
  { name: 'ai-300', hex: '#A5B4FC', textDark: false, note: 'Acentos secundários e anéis sutis' },
  { name: 'ai-500', hex: '#6366F1', textDark: false, note: 'Acento primário de IA — Botão Flutuante' },
  { name: 'ai-600', hex: '#4F46E5', textDark: false, note: 'Gradiente secundário para ações cognitivas' },
  { name: 'ai-violet', hex: '#8B5CF6', textDark: false, note: 'Gerador de Quizzes com IA' },
];

const slateColors = [
  { name: 'slate-50', hex: '#F8FAFC', textDark: true, note: 'Plano de fundo geral da aplicação' },
  { name: 'slate-100', hex: '#F1F5F9', textDark: true, note: 'Cards neutros e blocos de código' },
  { name: 'slate-200', hex: '#E2E8F0', textDark: true, note: 'Bordas padrão e divisores' },
  { name: 'slate-400', hex: '#94A3B8', textDark: false, note: 'Placeholders e ícones secundários' },
  { name: 'slate-600', hex: '#475569', textDark: false, note: 'Rótulos de formulário e metadados' },
  { name: 'slate-800', hex: '#1E293B', textDark: false, note: 'Títulos de seções e corpo escuro' },
  { name: 'slate-900', hex: '#0F172A', textDark: false, note: 'Títulos de página com máxima nitidez' },
];

const semanticColors = [
  { name: 'Sucesso', hex: '#10B981', bg: 'bg-emerald-50', border: 'border-emerald-200', text: 'text-emerald-700', label: 'Concluído / Aprovado' },
  { name: 'Alerta', hex: '#F59E0B', bg: 'bg-amber-50', border: 'border-amber-200', text: 'text-amber-700', label: 'Prazo próximo / Revisão' },
  { name: 'Perigo', hex: '#EF4444', bg: 'bg-rose-50', border: 'border-rose-200', text: 'text-rose-700', label: 'Erro / SLA expirado' },
  { name: 'Info', hex: '#0EA5E9', bg: 'bg-sky-50', border: 'border-sky-200', text: 'text-sky-700', label: 'Informativo / Regulatório' },
];

const typographyScale = [
  { level: 'Display', size: '36px (2.25rem)', leading: '44px', weight: 'ExtraBold (800)', tracking: '-0.03em', example: 'Coop Onboarding' },
  { level: 'Heading 1', size: '28px (1.75rem)', leading: '36px', weight: 'Bold (700)', tracking: '-0.025em', example: 'Fundamentos do Cooperativismo de Crédito' },
  { level: 'Heading 2', size: '22px (1.375rem)', leading: '28px', weight: 'Bold (700)', tracking: '-0.02em', example: 'Módulo 1: Os 7 Princípios Universais' },
  { level: 'Heading 3', size: '18px (1.125rem)', leading: '24px', weight: 'SemiBold (600)', tracking: '-0.015em', example: 'Governança e Participação Democrática' },
  { level: 'Body Large', size: '16px (1.00rem)', leading: '24px', weight: 'Regular (400)', tracking: 'normal', example: 'As cooperativas são organizações democráticas geridas pelos seus associados.' },
  { level: 'Body Base', size: '14px (0.875rem)', leading: '20px', weight: 'Medium (500)', tracking: 'normal', example: 'Complete esta lição para avançar na sua trilha de integração.' },
  { level: 'Caption', size: '12px (0.75rem)', leading: '16px', weight: 'Medium (500)', tracking: '+0.01em', example: 'Tempo estimado de leitura: 15 minutos · SLA em dia' },
  { level: 'Micro / Overline', size: '10px (0.625rem)', leading: '14px', weight: 'Bold (700)', tracking: '+0.05em', example: 'PERFIL KEYCLOAK · COLABORADOR' },
];
</script>

<template>
  <div class="space-y-10 pb-20 animate-fade-in">
    <!-- Header Hero do Design System -->
    <section class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900 via-coop-950 to-slate-900 text-white p-8 sm:p-12 shadow-xl border border-coop-800/40">
      <div class="absolute -right-16 -top-16 w-80 h-80 rounded-full bg-coop-500/10 blur-3xl pointer-events-none"></div>
      <div class="absolute -left-16 -bottom-16 w-80 h-80 rounded-full bg-ai-500/10 blur-3xl pointer-events-none"></div>

      <div class="relative z-10 max-w-4xl space-y-4">
        <div class="flex flex-wrap items-center gap-2">
          <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold uppercase tracking-wider bg-coop-500/20 text-coop-300 border border-coop-400/30">
            Design System Canônico
          </span>
          <span class="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-semibold bg-white/10 text-white/90 border border-white/10">
            v1.0.0
          </span>
          <span class="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-400/30">
            <CheckCircle2 class="w-3.5 h-3.5" /> WCAG 2.1 Nível AA
          </span>
        </div>

        <h1 class="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight text-white">
          Coop Onboarding <span class="text-coop-400">Design System</span>
        </h1>

        <p class="text-base sm:text-lg text-slate-300 leading-relaxed">
          Guia de design canônico, tokens corporativos, acessibilidade e biblioteca de componentes vivos desenvolvidos para potencializar o ecossistema cooperativo com fluidez, calor humano e assistência de IA.
        </p>

        <div class="pt-2 flex flex-wrap gap-4 text-xs text-slate-400">
          <span class="flex items-center gap-1.5"><ShieldCheck class="w-4 h-4 text-coop-400" /> Contraste Mínimo 4.5:1</span>
          <span class="flex items-center gap-1.5"><Maximize2 class="w-4 h-4 text-coop-400" /> Grid Estrito de 4px</span>
          <span class="flex items-center gap-1.5"><Sparkles class="w-4 h-4 text-ai-400" /> Tokens Semânticos de IA</span>
        </div>
      </div>
    </section>

    <!-- Navigation Tabs -->
    <div class="sticky top-20 z-20 bg-slate-50/95 backdrop-blur-md py-2 border-b border-slate-200">
      <div class="flex items-center space-x-2 overflow-x-auto no-scrollbar">
        <button
          @click="activeTab = 'tokens'"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-semibold transition-all whitespace-nowrap flex items-center gap-2',
            activeTab === 'tokens'
              ? 'bg-coop-600 text-white shadow-sm'
              : 'bg-white text-slate-600 hover:text-slate-900 border border-slate-200/80 hover:bg-slate-100',
          ]"
        >
          <Palette class="w-4 h-4" />
          <span>Tokens de Cores</span>
        </button>

        <button
          @click="activeTab = 'typography'"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-semibold transition-all whitespace-nowrap flex items-center gap-2',
            activeTab === 'typography'
              ? 'bg-coop-600 text-white shadow-sm'
              : 'bg-white text-slate-600 hover:text-slate-900 border border-slate-200/80 hover:bg-slate-100',
          ]"
        >
          <Type class="w-4 h-4" />
          <span>Tipografia & Escala</span>
        </button>

        <button
          @click="activeTab = 'components'"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-semibold transition-all whitespace-nowrap flex items-center gap-2',
            activeTab === 'components'
              ? 'bg-coop-600 text-white shadow-sm'
              : 'bg-white text-slate-600 hover:text-slate-900 border border-slate-200/80 hover:bg-slate-100',
          ]"
        >
          <Box class="w-4 h-4" />
          <span>Componentes & Anatomia</span>
        </button>

        <button
          @click="activeTab = 'forms'"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-semibold transition-all whitespace-nowrap flex items-center gap-2',
            activeTab === 'forms'
              ? 'bg-coop-600 text-white shadow-sm'
              : 'bg-white text-slate-600 hover:text-slate-900 border border-slate-200/80 hover:bg-slate-100',
          ]"
        >
          <Sliders class="w-4 h-4" />
          <span>Formulários & Controles</span>
        </button>

        <button
          @click="activeTab = 'voice'"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-semibold transition-all whitespace-nowrap flex items-center gap-2',
            activeTab === 'voice'
              ? 'bg-coop-600 text-white shadow-sm'
              : 'bg-white text-slate-600 hover:text-slate-900 border border-slate-200/80 hover:bg-slate-100',
          ]"
        >
          <MessageSquare class="w-4 h-4" />
          <span>Tom de Voz & Redação</span>
        </button>
      </div>
    </div>

    <!-- TOKENS DE CORES TAB -->
    <div v-show="activeTab === 'tokens'" class="space-y-10">
      <!-- 1. Brand Verde Cooperativo -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-4">
          <div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 rounded-full bg-coop-500"></div>
              <h2 class="text-xl font-bold text-slate-900">Brand Verde Cooperativo</h2>
            </div>
            <p class="text-sm text-slate-500 mt-1">
              Identidade mestra do cooperativismo de crédito. Transmite sustentabilidade, segurança financeira e acolhimento.
            </p>
          </div>
          <span class="text-xs text-slate-400 font-mono">Destaques: #008751 (Canônico) & #005A36 (Deep)</span>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3">
          <div
            v-for="color in coopColors"
            :key="color.name"
            @click="copyToClipboard(color.hex)"
            class="group cursor-pointer rounded-2xl p-3 border border-slate-100 hover:border-coop-300 transition-all hover:shadow-md relative"
          >
            <div
              :style="{ backgroundColor: color.hex }"
              class="h-16 rounded-xl flex items-end justify-between p-2 shadow-inner"
            >
              <span
                :class="[
                  'text-[10px] font-mono font-bold px-1.5 py-0.5 rounded backdrop-blur-xs',
                  color.textDark ? 'text-slate-900 bg-white/70' : 'text-white bg-black/30',
                ]"
              >
                {{ color.hex }}
              </span>
              <component
                :is="copiedHex === color.hex ? Check : Copy"
                :class="[
                  'w-3.5 h-3.5 opacity-0 group-hover:opacity-100 transition-opacity',
                  color.textDark ? 'text-slate-900' : 'text-white',
                ]"
              />
            </div>
            <div class="mt-2">
              <p class="text-xs font-bold text-slate-800">{{ color.name }}</p>
              <p class="text-[10px] text-slate-500 truncate" :title="color.note">{{ color.note }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 2. AI Tokens & Inteligência Cognitiva -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-4">
          <div>
            <div class="flex items-center gap-2">
              <Sparkles class="w-5 h-5 text-ai-500" />
              <h2 class="text-xl font-bold text-slate-900">Paleta de Inteligência Artificial & RAG</h2>
            </div>
            <p class="text-sm text-slate-500 mt-1">
              Reservada exclusivamente para módulos impulsionados por IA (Tutor Virtual RAG, gerador de quizzes e insights).
            </p>
          </div>
          <span class="text-xs text-ai-600 font-semibold bg-ai-50 px-2.5 py-1 rounded-lg border border-ai-100">
            Cognitive Layer
          </span>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-3">
          <div
            v-for="color in aiColors"
            :key="color.name"
            @click="copyToClipboard(color.hex)"
            class="group cursor-pointer rounded-2xl p-3 border border-slate-100 hover:border-ai-300 transition-all hover:shadow-md"
          >
            <div
              :style="{ backgroundColor: color.hex }"
              class="h-16 rounded-xl flex items-end justify-between p-2 shadow-inner"
            >
              <span
                :class="[
                  'text-[10px] font-mono font-bold px-1.5 py-0.5 rounded backdrop-blur-xs',
                  color.textDark ? 'text-slate-900 bg-white/70' : 'text-white bg-black/30',
                ]"
              >
                {{ color.hex }}
              </span>
              <component
                :is="copiedHex === color.hex ? Check : Copy"
                :class="[
                  'w-3.5 h-3.5 opacity-0 group-hover:opacity-100 transition-opacity',
                  color.textDark ? 'text-slate-900' : 'text-white',
                ]"
              />
            </div>
            <div class="mt-2">
              <p class="text-xs font-bold text-slate-800">{{ color.name }}</p>
              <p class="text-[10px] text-slate-500 truncate" :title="color.note">{{ color.note }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. Apoio Neutro Slate & Semântico -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Slate -->
        <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
          <div class="border-b border-slate-100 pb-3">
            <h2 class="text-lg font-bold text-slate-900">Apoio Neutro (Slate 50 - 950)</h2>
            <p class="text-xs text-slate-500">Fundação para legibilidade e ausência de fadiga ocular.</p>
          </div>

          <div class="space-y-2">
            <div
              v-for="color in slateColors"
              :key="color.name"
              @click="copyToClipboard(color.hex)"
              class="group flex items-center justify-between p-2.5 rounded-xl border border-slate-100 hover:bg-slate-50 cursor-pointer transition-colors"
            >
              <div class="flex items-center space-x-3">
                <div :style="{ backgroundColor: color.hex }" class="w-7 h-7 rounded-lg border border-slate-200 shadow-xs"></div>
                <div>
                  <span class="text-xs font-bold text-slate-800">{{ color.name }}</span>
                  <span class="text-[11px] text-slate-500 ml-2 hidden sm:inline">{{ color.note }}</span>
                </div>
              </div>
              <span class="text-xs font-mono text-slate-400 group-hover:text-slate-700">{{ color.hex }}</span>
            </div>
          </div>
        </div>

        <!-- Semânticos -->
        <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
          <div class="border-b border-slate-100 pb-3">
            <h2 class="text-lg font-bold text-slate-900">Tokens Semânticos de Feedback</h2>
            <p class="text-xs text-slate-500">Estados de alerta, confirmação, risco e avisos com alto contraste.</p>
          </div>

          <div class="space-y-3">
            <div
              v-for="sem in semanticColors"
              :key="sem.name"
              :class="['p-4 rounded-2xl border flex items-center justify-between', sem.bg, sem.border]"
            >
              <div class="flex items-center space-x-3">
                <span class="w-3 h-3 rounded-full" :style="{ backgroundColor: sem.hex }"></span>
                <div>
                  <p :class="['text-xs font-bold uppercase tracking-wider', sem.text]">{{ sem.name }}</p>
                  <p class="text-xs text-slate-600 mt-0.5">{{ sem.label }}</p>
                </div>
              </div>
              <span class="text-xs font-mono font-bold text-slate-600 bg-white/80 px-2 py-0.5 rounded border border-slate-200">
                {{ sem.hex }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TIPOGRAFIA TAB -->
    <div v-show="activeTab === 'typography'" class="space-y-8">
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div>
          <h2 class="text-xl font-bold text-slate-900">Escala Tipográfica Modular</h2>
          <p class="text-sm text-slate-500 mt-1">
            Família primária: <strong>Plus Jakarta Sans</strong>, com fallbacks para <strong>Inter</strong> e <strong>system-ui</strong>.
          </p>
        </div>

        <div class="divide-y divide-slate-100">
          <div
            v-for="item in typographyScale"
            :key="item.level"
            class="py-5 flex flex-col md:flex-row md:items-baseline justify-between gap-4"
          >
            <div class="w-48 shrink-0">
              <span class="text-xs font-bold text-coop-700 bg-coop-50 px-2 py-0.5 rounded border border-coop-200">
                {{ item.level }}
              </span>
              <p class="text-xs text-slate-400 mt-1">{{ item.size }} / {{ item.leading }}</p>
              <p class="text-[11px] text-slate-500">{{ item.weight }}</p>
            </div>
            <div class="flex-1">
              <p
                :class="[
                  'text-slate-900',
                  item.level === 'Display' ? 'text-4xl font-extrabold tracking-tight' : '',
                  item.level === 'Heading 1' ? 'text-2xl font-bold tracking-tight' : '',
                  item.level === 'Heading 2' ? 'text-xl font-bold' : '',
                  item.level === 'Heading 3' ? 'text-lg font-semibold' : '',
                  item.level === 'Body Large' ? 'text-base font-normal' : '',
                  item.level === 'Body Base' ? 'text-sm font-medium' : '',
                  item.level === 'Caption' ? 'text-xs text-slate-500 font-medium' : '',
                  item.level === 'Micro / Overline' ? 'text-[10px] font-bold text-slate-600 uppercase tracking-widest' : '',
                ]"
              >
                {{ item.example }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- COMPONENTES TAB -->
    <div v-show="activeTab === 'components'" class="space-y-10">
      <!-- 1. Botões -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="border-b border-slate-100 pb-3">
          <h2 class="text-xl font-bold text-slate-900">Anatomia de Botões (Buttons)</h2>
          <p class="text-sm text-slate-500">Mapeamento tátil para estados default, hover, active, focus-visible e disabled.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- Primary Coop -->
          <div class="p-5 rounded-2xl border border-slate-200/80 space-y-3">
            <span class="text-xs font-bold text-slate-500 uppercase">Primário Cooperativo</span>
            <div class="flex flex-wrap items-center gap-3">
              <button
                type="button"
                class="px-4 py-2.5 rounded-xl font-semibold text-sm bg-coop-600 hover:bg-coop-700 text-white shadow-sm hover:shadow transition-all duration-200 active:scale-[0.98] focus-visible:ring-2 focus-visible:ring-coop-500"
              >
                Iniciar Lição
              </button>
              <button
                type="button"
                disabled
                class="px-4 py-2.5 rounded-xl font-semibold text-sm bg-coop-600 text-white opacity-40 cursor-not-allowed"
              >
                Desabilitado
              </button>
            </div>
          </div>

          <!-- AI Action -->
          <div class="p-5 rounded-2xl border border-slate-200/80 space-y-3">
            <span class="text-xs font-bold text-ai-600 uppercase">Ação com IA (Cognitive)</span>
            <div class="flex flex-wrap items-center gap-3">
              <button
                type="button"
                @click="aiTutorStore.openDrawer"
                class="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl text-sm font-semibold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/20 hover:scale-[1.02] active:scale-[0.98] transition-all"
              >
                <Sparkles class="w-4 h-4 text-ai-100" />
                <span>Consultar Tutor IA</span>
              </button>
            </div>
          </div>

          <!-- Outline Secondary -->
          <div class="p-5 rounded-2xl border border-slate-200/80 space-y-3">
            <span class="text-xs font-bold text-slate-500 uppercase">Secundário Outline</span>
            <div class="flex flex-wrap items-center gap-3">
              <button
                type="button"
                class="px-4 py-2.5 rounded-xl font-medium text-sm border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 shadow-xs transition-colors"
              >
                Ver Detalhes
              </button>
              <button
                type="button"
                class="px-3 py-2 rounded-lg font-medium text-xs text-slate-600 hover:bg-slate-100 transition-colors"
              >
                Ghost Action
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 2. Badges & Tags -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="border-b border-slate-100 pb-3">
          <h2 class="text-xl font-bold text-slate-900">Badges & Tags Semânticas</h2>
          <p class="text-sm text-slate-500">Classificação de papéis do Keycloak RBAC e status de avanço do colaborador.</p>
        </div>

        <div class="space-y-4">
          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Perfis Keycloak RBAC</p>
            <div class="flex flex-wrap gap-3">
              <span class="px-2.5 py-1 rounded-md text-[11px] font-bold uppercase tracking-wider bg-coop-50 text-coop-700 border border-coop-200">
                Colaborador
              </span>
              <span class="px-2.5 py-1 rounded-md text-[11px] font-bold uppercase tracking-wider bg-indigo-50 text-indigo-700 border border-indigo-200">
                Gestor
              </span>
              <span class="px-2.5 py-1 rounded-md text-[11px] font-bold uppercase tracking-wider bg-rose-50 text-rose-700 border border-rose-200">
                Admin
              </span>
            </div>
          </div>

          <div>
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Status de Aprendizagem</p>
            <div class="flex flex-wrap gap-3">
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                <CheckCircle2 class="w-3.5 h-3.5" /> Concluído
              </span>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                <Clock class="w-3.5 h-3.5" /> Em Andamento
              </span>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
                Não Iniciado
              </span>
              <span class="px-2.5 py-1 rounded-lg text-xs font-bold bg-coop-100/60 text-coop-800">
                Obrigatório
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. Barras de Progresso Interativas -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
          <div>
            <h2 class="text-xl font-bold text-slate-900">Barras de Progresso (`ProgressBar`)</h2>
            <p class="text-sm text-slate-500">Transições fluidas com 500ms ease-out e atributos de acessibilidade ARIA.</p>
          </div>
          <!-- Slider Interativo -->
          <div class="flex items-center space-x-3 bg-slate-50 px-4 py-2 rounded-2xl border border-slate-200">
            <span class="text-xs font-bold text-slate-700">Ajuste de Teste:</span>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="sliderProgress"
              class="w-32 accent-coop-600 cursor-pointer"
            />
            <span class="text-xs font-mono font-bold text-coop-700 w-9 text-right">{{ sliderProgress }}%</span>
          </div>
        </div>

        <div class="space-y-6 max-w-2xl">
          <div>
            <div class="flex justify-between text-xs font-semibold text-slate-700 mb-1.5">
              <span>Variante Cooperativa (`variant="coop"`)</span>
              <span>{{ sliderProgress }}%</span>
            </div>
            <ProgressBar :value="sliderProgress" variant="coop" size="md" />
          </div>

          <div>
            <div class="flex justify-between text-xs font-semibold text-slate-700 mb-1.5">
              <span>Variante Inteligência Artificial (`variant="ai"`)</span>
              <span>{{ sliderProgress }}%</span>
            </div>
            <ProgressBar :value="sliderProgress" variant="ai" size="md" />
          </div>

          <div>
            <div class="flex justify-between text-xs font-semibold text-slate-700 mb-1.5">
              <span>Variante Sucesso / Conclusão (`variant="success"`)</span>
              <span>{{ sliderProgress }}%</span>
            </div>
            <ProgressBar :value="sliderProgress" variant="success" size="lg" />
          </div>
        </div>
      </div>

      <!-- 4. Cards de Exemplo & Skeleton Loader -->
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div class="flex items-center justify-between border-b border-slate-100 pb-3">
          <div>
            <h2 class="text-xl font-bold text-slate-900">Cards de Trilha & Skeleton Shimmer</h2>
            <p class="text-sm text-slate-500">Prevenção de Cumulative Layout Shift (CLS) com estados de carga nativos.</p>
          </div>
          <button
            @click="isSkeletonSimulated = !isSkeletonSimulated"
            class="px-3.5 py-1.5 rounded-xl text-xs font-bold border border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-700 transition-colors flex items-center gap-1.5"
          >
            <Eye class="w-3.5 h-3.5" />
            <span>{{ isSkeletonSimulated ? 'Ver Conteúdo Real' : 'Simular Skeleton' }}</span>
          </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Card Real vs Skeleton -->
          <div v-if="!isSkeletonSimulated" class="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-xs hover:shadow-md hover:border-coop-300 transition-all duration-300 flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between mb-4">
                <span class="px-2.5 py-1 rounded-lg text-xs font-semibold bg-coop-50 text-coop-700 border border-coop-200">
                  Trilha Institucional
                </span>
                <span class="text-xs text-slate-500 flex items-center gap-1">
                  <Clock class="w-3.5 h-3.5" /> 45 min
                </span>
              </div>
              <h3 class="text-lg font-bold text-slate-900 mb-2">Fundamentos do Cooperativismo</h3>
              <p class="text-sm text-slate-600 mb-6">
                Aprenda a história dos pioneiros de Rochdale, a constituição das cooperativas de crédito e os direitos dos cooperados.
              </p>
            </div>
            <div class="space-y-4">
              <ProgressBar :value="60" variant="coop" show-label />
              <div class="flex justify-between items-center pt-2 border-t border-slate-100">
                <span class="text-xs text-slate-500">3 de 5 lições concluídas</span>
                <button class="text-xs font-bold text-coop-700 hover:text-coop-800 flex items-center gap-1">
                  Continuar <ArrowRight class="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          </div>
          <SkeletonLoader v-else type="card" />

          <!-- KPI Metric Card -->
          <div class="bg-gradient-to-br from-coop-50/60 to-white rounded-2xl border border-coop-200/80 p-6 shadow-xs flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between mb-3">
                <span class="text-xs font-bold uppercase tracking-wider text-coop-800">Aproveitamento Médio</span>
                <div class="w-8 h-8 rounded-lg bg-coop-500/10 flex items-center justify-center text-coop-700">
                  <TrendingUp class="w-4 h-4" />
                </div>
              </div>
              <div class="text-4xl font-extrabold text-slate-900 mb-1">94.8%</div>
              <p class="text-xs text-slate-500">Taxa de acertos em quizzes de fixação nesta turma</p>
            </div>
            <div class="mt-4 pt-3 border-t border-coop-100 text-xs text-coop-700 font-semibold flex items-center gap-1">
              <CheckCircle2 class="w-3.5 h-3.5" /> +12% acima da meta regulatória
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- FORMULÁRIOS & CONTROLES TAB -->
    <div v-show="activeTab === 'forms'" class="space-y-8">
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div>
          <h2 class="text-xl font-bold text-slate-900">Campos de Formulário & Controles de Entrada</h2>
          <p class="text-sm text-slate-500">Anéis de foco visíveis, microcopy de suporte e validação semântica.</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl">
          <!-- Text Input Padrão -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wide">Nome Completo do Colaborador</label>
            <input
              type="text"
              v-model="demoInputValue"
              class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:border-coop-500 focus:ring-2 focus:ring-coop-500/20 text-sm text-slate-800 transition-all outline-hidden"
              placeholder="Digite seu nome..."
            />
            <p class="text-[11px] text-slate-500">Conforme registrado no cadastro institucional.</p>
          </div>

          <!-- Search Input -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wide">Busca por Trilha ou Módulo</label>
            <div class="relative">
              <Search class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                v-model="demoSearchValue"
                class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 focus:border-coop-500 focus:ring-2 focus:ring-coop-500/20 text-sm text-slate-800 transition-all outline-hidden"
                placeholder="Ex: LGPD, Crédito, Governança..."
              />
            </div>
            <p class="text-[11px] text-slate-500">Filtre instantaneamente entre os manuais e lições.</p>
          </div>

          <!-- Select -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wide">Área de Atuação</label>
            <select class="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:border-coop-500 focus:ring-2 focus:ring-coop-500/20 text-sm text-slate-800 transition-all outline-hidden bg-white">
              <option>Crédito & Riscos</option>
              <option>Atendimento & Relacionamento</option>
              <option>Compliance & Auditoria</option>
              <option>Tecnologia & Inovação</option>
            </select>
          </div>

          <!-- Switch Toggle -->
          <div class="space-y-1.5">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wide">Notificações do Tutor IA</label>
            <div class="flex items-center space-x-3 pt-2">
              <button
                type="button"
                @click="demoToggleValue = !demoToggleValue"
                :class="[
                  'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-hidden focus:ring-2 focus:ring-coop-500 focus:ring-offset-2',
                  demoToggleValue ? 'bg-coop-600' : 'bg-slate-200',
                ]"
              >
                <span
                  :class="[
                    'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out',
                    demoToggleValue ? 'translate-x-5' : 'translate-x-0',
                  ]"
                />
              </button>
              <span class="text-xs text-slate-600">
                {{ demoToggleValue ? 'Alertas de revisão ativos' : 'Alertas desativados' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TOM DE VOZ TAB -->
    <div v-show="activeTab === 'voice'" class="space-y-8">
      <div class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-xs space-y-6">
        <div>
          <h2 class="text-xl font-bold text-slate-900">Diretrizes de Tom de Voz & UX Copywriting</h2>
          <p class="text-sm text-slate-500">
            A redação deve ser acolhedora, transparente, direta e orientada ao aprendizado cooperativo.
          </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Como dizemos -->
          <div class="p-6 rounded-2xl bg-emerald-50/60 border border-emerald-200 space-y-3">
            <div class="flex items-center space-x-2 text-emerald-800 font-bold text-sm">
              <CheckCircle2 class="w-4 h-4 text-emerald-600" />
              <span>Como Nós Dizemos (Recomendado)</span>
            </div>
            <ul class="space-y-2 text-xs text-emerald-950">
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Bem-vindo à nossa cooperativa! Vamos iniciar sua jornada?"</strong> (Acolhimento e espírito coletivo).</span>
              </li>
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Consultar Tutor IA"</strong> ou <strong>"Gerar Quiz de Fixação"</strong> (Verbo ativo direto no infinitivo).</span>
              </li>
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Não foi possível salvar agora. Verifique a conexão e tente novamente."</strong> (Erro com orientação clara).</span>
              </li>
            </ul>
          </div>

          <!-- Como evitamos -->
          <div class="p-6 rounded-2xl bg-rose-50/60 border border-rose-200 space-y-3">
            <div class="flex items-center space-x-2 text-rose-800 font-bold text-sm">
              <XCircle class="w-4 h-4 text-rose-600" />
              <span>Como Evitamos (Não Recomendado)</span>
            </div>
            <ul class="space-y-2 text-xs text-rose-950">
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Autentique-se no sistema para prosseguir."</strong> (Frio, mecânico e impessoal).</span>
              </li>
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Clique aqui"</strong> ou <strong>"Submeter"</strong> (Vago, genérico e sem contexto de benefício).</span>
              </li>
              <li class="flex items-start gap-1.5">
                <span class="font-bold">·</span>
                <span><strong>"Erro fatal 500 no endpoint."</strong> (Jargão técnico opaco que gera insegurança no colaborador).</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Transição suave */
</style>
