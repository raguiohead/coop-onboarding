<script setup lang="ts">
import { ref } from 'vue';
import {
  Sparkles,
  BookOpen,
  Award,
  Users,
  CheckCircle2,
  ArrowRight,
  ArrowLeft,
  X,
  Compass,
  Clock,
  ShieldCheck,
} from 'lucide-vue-next';

defineProps<{
  isOpen: boolean;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const currentStep = ref(0);

const tourSteps = [
  {
    step: 1,
    badge: 'Passo 1 de 4 · Boas-Vindas',
    icon: Compass,
    iconBg: 'bg-brand-50 text-brand-700 border-brand-200',
    title: 'Bem-vindo ao Onboarding Cooperativo!',
    subtitle: 'Sua jornada de integração em uma instituição financeira que coloca as pessoas no centro.',
    description:
      'Diferente de bancos tradicionais que buscam lucros para acionistas, nós somos uma cooperativa de crédito regida pelo princípio "uma pessoa, um voto". Aqui, o seu desenvolvimento e o atendimento aos cooperados andam juntos.',
    highlights: [
      'Meta de integração: Concluir sua jornada em até 14 dias (SLA corporativo).',
      'Aprendizado progressivo e adaptado ao seu departamento.',
      'Acompanhamento contínuo da liderança e tutoria assistida por IA.',
    ],
  },
  {
    step: 2,
    badge: 'Passo 2 de 4 · Aprendizagem',
    icon: BookOpen,
    iconBg: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    title: 'Navegação pelas Trilhas & Lições',
    subtitle: 'Módulos modulares com conteúdos normativos, éticos e operacionais.',
    description:
      'Acesse a aba "Trilhas" para explorar sua grade de formação. Cada módulo é composto por lições estruturadas com estimativa de tempo e leituras didáticas.',
    highlights: [
      'Leitura fluida: Lições formatadas com tópicos, checklists e dicas práticas.',
      'Checklist de conclusão: Marque cada lição como concluída para registrar seu avanço.',
      'Controle de SLA: Veja quantos dias restam para finalizar cada trilha.',
    ],
  },
  {
    step: 3,
    badge: 'Passo 3 de 4 · Fixação',
    icon: Award,
    iconBg: 'bg-indigo-50 text-indigo-700 border-indigo-200',
    title: 'Quizzes & Simulados Normativos',
    subtitle: 'Avaliações interativas para fixação com gabarito fundamentado.',
    description:
      'Na aba "Quizzes", você encontra testes rápidos de múltipla escolha para validar seu entendimento dos 7 princípios cooperativos, prevenção à lavagem de dinheiro (PLD) e sigilo bancário.',
    highlights: [
      'Gabarito pedagógico: Cada questão explica a resposta correta e a norma do BACEN.',
      'Refaça quando quiser: Reforce conceitos sem pressão punitiva.',
      'Certificação: Atingir média acima de 70% desbloqueia seu Certificado Institucional.',
    ],
  },
  {
    step: 4,
    badge: 'Passo 4 de 4 · Assistência 24/7',
    icon: Sparkles,
    iconBg: 'bg-purple-50 text-purple-700 border-purple-200',
    title: 'Tutor Virtual de IA Sempre Ativo',
    subtitle: 'Dúvidas em tempo real com privacidade 100% on-premises.',
    description:
      'Em qualquer tela da plataforma, você verá o botão roxo flutuante do Tutor IA no canto inferior direito. Ele utiliza inteligência artificial local treinada nos manuais e políticas internas da cooperativa.',
    highlights: [
      'Respostas embasadas: Cita trechos dos manuais internos e normas vigentes.',
      'Segurança total: Nenhum dado financeiro ou pessoal sai dos servidores locais.',
      'Geração de quizzes: Gestores podem gerar novos simulados customizados com 1 clique.',
    ],
  },
];

function nextStep() {
  if (currentStep.value < tourSteps.length - 1) {
    currentStep.value++;
  } else {
    finishTour();
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
}

function finishTour() {
  currentStep.value = 0;
  emit('close');
}
</script>

<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/75 backdrop-blur-sm animate-fade-in"
    >
      <div class="bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-2xl w-full overflow-hidden flex flex-col max-h-[85vh] my-auto">
        <!-- Header -->
      <div class="px-6 sm:px-8 py-5 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
        <div class="flex items-center space-x-2.5">
          <div class="w-8 h-8 rounded-xl bg-brand-50 text-brand-700 flex items-center justify-center font-bold">
            <Compass class="w-4 h-4" />
          </div>
          <div>
            <h3 class="text-sm font-bold text-slate-900">Guia Rápido de Onboarding</h3>
            <p class="text-[11px] text-slate-500">Apresentação da plataforma e seus recursos</p>
          </div>
        </div>

        <button
          @click="finishTour"
          class="text-slate-400 hover:text-slate-600 p-1.5 rounded-lg hover:bg-slate-200/50 transition-colors cursor-pointer"
          title="Fechar tutorial"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Step Content -->
      <div class="p-6 sm:p-8 overflow-y-auto space-y-6 flex-1">
        <!-- Step Indicator Pills -->
        <div class="flex items-center justify-between gap-2">
          <div
            v-for="(st, idx) in tourSteps"
            :key="st.step"
            class="flex-1 h-1.5 rounded-full transition-all"
            :class="idx <= currentStep ? 'bg-brand-600' : 'bg-slate-200'"
          ></div>
        </div>

        <!-- Current Step Details -->
        <div class="space-y-4">
          <div class="flex items-center space-x-3">
            <div
              :class="['w-12 h-12 rounded-2xl flex items-center justify-center border shadow-xs shrink-0', tourSteps[currentStep].iconBg]"
            >
              <component :is="tourSteps[currentStep].icon" class="w-6 h-6" />
            </div>
            <div>
              <span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-slate-100 text-slate-600 border border-slate-200">
                {{ tourSteps[currentStep].badge }}
              </span>
              <h2 class="text-xl sm:text-2xl font-extrabold text-slate-900 tracking-tight font-sans mt-0.5">
                {{ tourSteps[currentStep].title }}
              </h2>
            </div>
          </div>

          <p class="text-xs sm:text-sm font-semibold text-brand-800">
            {{ tourSteps[currentStep].subtitle }}
          </p>

          <p class="text-xs sm:text-sm text-slate-600 leading-relaxed">
            {{ tourSteps[currentStep].description }}
          </p>

          <!-- Highlights List -->
          <div class="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-2.5">
            <p class="text-xs font-bold text-slate-700 uppercase tracking-wide">Destaques Principais:</p>
            <ul class="space-y-2 text-xs text-slate-600">
              <li
                v-for="(hl, hIdx) in tourSteps[currentStep].highlights"
                :key="hIdx"
                class="flex items-start space-x-2"
              >
                <CheckCircle2 class="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                <span>{{ hl }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Footer Buttons -->
      <div class="px-6 sm:px-8 py-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/50">
        <button
          v-if="currentStep > 0"
          @click="prevStep"
          class="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl text-xs font-bold text-slate-600 hover:text-slate-900 hover:bg-slate-200/50 transition-colors cursor-pointer"
        >
          <ArrowLeft class="w-4 h-4" />
          <span>Voltar</span>
        </button>
        <button
          v-else
          @click="finishTour"
          class="text-xs font-semibold text-slate-400 hover:text-slate-600 transition-colors cursor-pointer"
        >
          Pular Tutorial
        </button>

        <div class="flex items-center space-x-2">
          <button
            @click="nextStep"
            class="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-md shadow-brand-600/20 hover:scale-[1.02] active:scale-[0.98] transition-all cursor-pointer"
          >
            <span>{{ currentStep < tourSteps.length - 1 ? 'Próximo Passo' : 'Entendi, Começar Agora!' }}</span>
            <ArrowRight class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</Teleport>
</template>
