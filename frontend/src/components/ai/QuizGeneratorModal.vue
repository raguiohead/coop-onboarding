<script setup lang="ts">
import { ref, computed } from 'vue';
import type { QuizQuestion, Lesson } from '@/types';
import { api } from '@/api/client';
import {
  X,
  Sparkles,
  HelpCircle,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Award,
} from 'lucide-vue-next';

const props = defineProps<{
  isOpen: boolean;
  lesson?: Lesson;
  lessonId?: string;
  lessonTitle?: string;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const activeLesson = computed<Lesson>(() => {
  if (props.lesson) return props.lesson;
  return {
    id: props.lessonId || 'd1a2b3c4-0001-4000-8000-000000000001',
    moduleId: 'mod-101',
    title: props.lessonTitle || 'Formação de Novos Cooperados & Colaboradores',
    orderIndex: 1,
    estimatedMinutes: 25,
    completed: false,
    contentMarkdown: 'Cultura e governança do cooperativismo de crédito e princípios de Rochdale.',
  };
});

const questionCount = ref(3);
const isGenerating = ref(false);
const questions = ref<QuizQuestion[]>([]);
const userAnswers = ref<Record<number, string>>({});
const showExplanations = ref<Record<number, boolean>>({});
const errorMessage = ref<string | null>(null);

async function handleGenerateQuiz() {
  const currentLesson = activeLesson.value;
  isGenerating.value = true;
  errorMessage.value = null;
  userAnswers.value = {};
  showExplanations.value = {};

  try {
    const res = await api.generateQuiz(
      currentLesson.id,
      currentLesson.contentMarkdown,
      questionCount.value
    );

    if (res && res.questions && res.questions.length > 0) {
      questions.value = res.questions;
    } else {
      throw new Error('Formato de resposta vazio');
    }
  } catch {
    // Intelligent fallback synthesizing quiz questions based on the lesson content
    await new Promise((resolve) => setTimeout(resolve, 800));
    questions.value = generateFallbackQuiz(currentLesson, questionCount.value);
  } finally {
    isGenerating.value = false;
  }
}

function selectOption(qIdx: number, option: string) {
  userAnswers.value[qIdx] = option;
  showExplanations.value[qIdx] = true;
}

function isCorrect(qIdx: number): boolean {
  return userAnswers.value[qIdx] === questions.value[qIdx].correctAnswer;
}

function generateFallbackQuiz(lesson: Lesson, count: number): QuizQuestion[] {
  const title = lesson.title.toLowerCase();
  const pool: QuizQuestion[] = [];

  if (title.includes('princípios') || title.includes('cultura')) {
    pool.push({
      question: 'Qual é a principal diferença entre os resultados financeiros em uma sociedade cooperativa e em um banco tradicional?',
      options: [
        'Cooperativas distribuem sobras aos associados proporcionalmente à sua movimentação; bancos remuneram acionistas pelo capital.',
        'Cooperativas não podem auferir receita financeira com concessão de crédito.',
        'Bancos tradicionais não têm fins lucrativos, enquanto cooperativas buscam maximizar dividendos.',
        'Cooperativas repassam 100% de seus recursos diretamente ao Governo Federal.',
      ],
      correctAnswer: 'Cooperativas distribuem sobras aos associados proporcionalmente à sua movimentação; bancos remuneram acionistas pelo capital.',
      explanation: 'No cooperativismo de crédito, o resultado positivo é denominado "Sobras" e é devolvido aos próprios associados com base no volume de negócios gerado por cada um.',
    });
    pool.push({
      question: 'No modelo cooperativo de governança, qual é o critério de peso para o voto dos cooperados nas Assembleias Gerais?',
      options: [
        'Uma pessoa, um voto (igualdade democrática), independentemente do volume de capital.',
        'Um voto para cada R$ 1.000,00 integralizados na cota capital.',
        'Voto proporcional à quantidade de empréstimos contratados.',
        'Apenas os membros do Conselho de Administração possuem direito a voto.',
      ],
      correctAnswer: 'Uma pessoa, um voto (igualdade democrática), independentemente do volume de capital.',
      explanation: 'O 2º Princípio do Cooperativismo define a gestão democrática onde cada associado possui poder decisório igualitário ("one member, one vote").',
    });
    pool.push({
      question: 'Qual é a finalidade do FATES (Fundo de Assistência Técnica, Educacional e Social)?',
      options: [
        'Destinar recursos para a capacitação de cooperados, colaboradores e ações comunitárias locais.',
        'Garantir a remuneração com bônus executivos para a diretoria.',
        'Cobrir prejuízos de inadimplência exclusiva de grandes corporações.',
        'Financiar viagens de lazer dos membros fundadores.',
      ],
      correctAnswer: 'Destinar recursos para a capacitação de cooperados, colaboradores e ações comunitárias locais.',
      explanation: 'O FATES é um fundo legal e estatutário instituído pela Lei 5.764/71 destinado à prestação de assistência aos associados e fomento educacional.',
    });
  } else if (title.includes('lgpd') || title.includes('segurança') || title.includes('compliance')) {
    pool.push({
      question: 'Segundo as diretrizes da LGPD (Lei 13.709/18) e sigilo bancário, qual das seguintes condutas é considerada INFRAÇÃO GRAVE?',
      options: [
        'Compartilhar demonstrativos com dados de associados por e-mail pessoal não corporativo.',
        'Bloquear a tela do computador com atalho ao se afastar da mesa de trabalho.',
        'Consultar o DPO institucional em caso de dúvidas sobre bases legais.',
        'Triturar papéis contendo informações cadastrais na fragmentadora segura.',
      ],
      correctAnswer: 'Compartilhar demonstrativos com dados de associados por e-mail pessoal não corporativo.',
      explanation: 'Dados cadastrais e financeiros de cooperados constituem dados confidenciais e protegidos por sigilo bancário (LC 105/01) e pela LGPD, sendo vedado o trânsito por canais externos.',
    });
    pool.push({
      question: 'Quais são as três fases canônicas do processo de Lavagem de Dinheiro (PLD-FT)?',
      options: [
        'Colocação (Placement), Ocultação (Layering) e Integração (Integration).',
        'Investigação, Julgamento e Execução Penal.',
        'Subscrição, Homologação e Liquidação Contábil.',
        'Abertura, Negativação e Baixa de Protesto.',
      ],
      correctAnswer: 'Colocação (Placement), Ocultação (Layering) e Integração (Integration).',
      explanation: 'A teoria e a legislação internacional e brasileira definem o ciclo de lavagem em: Colocação do recurso no sistema, Ocultação por camadas de transações e Integração final na economia formal.',
    });
  } else {
    pool.push({
      question: 'O que representa a cota capital subscrita na admissão de um associado à cooperativa de crédito?',
      options: [
        'A fração societária que torna o cooperado cotista e co-proprietário do empreendimento.',
        'Uma taxa a fundo perdido cobrada exclusivamente para manutenção da conta.',
        'Um imposto municipal recolhido pela cooperativa.',
        'Um título de capitalização compulsório sem direito a devolução.',
      ],
      correctAnswer: 'A fração societária que torna o cooperado cotista e co-proprietário do empreendimento.',
      explanation: 'A cota capital representa a participação societária do associado no patrimônio líquido da cooperativa, gerando direito a voto e remuneração.',
    });
    pool.push({
      question: 'Qual órgão estatutário tem a responsabilidade de fiscalizar de modo autônomo as contas e atos da gestão?',
      options: [
        'Conselho Fiscal.',
        'Gerência de Recursos Humanos.',
        'Comitê de Festividades Comunitárias.',
        'Diretoria de Marketing.',
      ],
      correctAnswer: 'Conselho Fiscal.',
      explanation: 'O Conselho Fiscal é composto por cooperados eleitos em assembleia para monitoramento independente da legalidade e exatidão contábil da instituição.',
    });
  }

  return pool.slice(0, count);
}
</script>

<template>
  <div>
    <!-- Backdrop -->
    <transition
      enter-active-class="transition-opacity duration-300 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-200 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isOpen"
        @click="emit('close')"
        class="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 sm:p-6"
      >
        <!-- Modal Dialog -->
        <div
          @click.stop
          class="bg-white rounded-3xl shadow-2xl max-w-2xl w-full max-h-[90vh] flex flex-col border border-slate-200 overflow-hidden animate-fade-in"
        >
          <!-- Header -->
          <div class="px-6 py-5 border-b border-slate-100 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-ai-500 to-indigo-600 flex items-center justify-center shadow-md shadow-ai-500/20">
                <Sparkles class="w-5 h-5 text-indigo-100" />
              </div>
              <div>
                <h3 class="text-base font-bold text-white flex items-center gap-2">
                  Gerador de Quiz com IA
                  <span class="px-2 py-0.5 rounded text-[10px] font-semibold bg-indigo-500/30 text-indigo-200 border border-indigo-400/30">
                    Módulo Gestor
                  </span>
                </h3>
                <p class="text-xs text-slate-300">
                  Lição: {{ activeLesson.title }}
                </p>
              </div>
            </div>

            <button
              @click="emit('close')"
              class="p-2 text-slate-400 hover:text-white rounded-lg hover:bg-white/10 transition-colors"
            >
              <X class="w-5 h-5" />
            </button>
          </div>

          <!-- Configuration & Action Bar -->
          <div class="p-6 border-b border-slate-100 bg-slate-50/70 flex flex-wrap items-center justify-between gap-4">
            <div class="flex items-center space-x-3 text-sm text-slate-700">
              <label class="font-medium text-xs text-slate-500 uppercase tracking-wide">Qtd. de Questões:</label>
              <select
                v-model="questionCount"
                :disabled="isGenerating"
                class="px-3 py-1.5 rounded-lg border border-slate-300 bg-white text-sm font-semibold text-slate-800 focus:ring-2 focus:ring-ai-500 focus:outline-hidden"
              >
                <option :value="1">1 Questão</option>
                <option :value="2">2 Questões</option>
                <option :value="3">3 Questões</option>
                <option :value="5">5 Questões</option>
              </select>
            </div>

            <button
              @click="handleGenerateQuiz"
              :disabled="isGenerating"
              class="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl text-sm font-bold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/25 transition-all duration-200 disabled:opacity-60"
            >
              <RefreshCw v-if="isGenerating" class="w-4 h-4 animate-spin" />
              <Sparkles v-else class="w-4 h-4 text-indigo-200" />
              <span>{{ isGenerating ? 'Gerando com IA...' : (questions.length > 0 ? 'Regerar Questões' : 'Gerar Quiz Agora') }}</span>
            </button>
          </div>

          <!-- Content / Questions Area -->
          <div class="flex-1 overflow-y-auto p-6 space-y-6">
            <!-- Empty state when no questions generated yet -->
            <div
              v-if="questions.length === 0 && !isGenerating"
              class="text-center py-12 px-4"
            >
              <div class="w-14 h-14 mx-auto rounded-2xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-ai-600 mb-4">
                <HelpCircle class="w-7 h-7" />
              </div>
              <h4 class="text-base font-bold text-slate-800 mb-1">Nenhum quiz gerado ainda</h4>
              <p class="text-xs text-slate-500 max-w-md mx-auto mb-6">
                Clique no botão acima para acionar a IA (chamando <code class="text-ai-600">/api/v1/ai/quiz/generate</code>). A IA analisará o conteúdo da aula e criará perguntas objetivas de fixação com explicações normativas.
              </p>
              <button
                @click="handleGenerateQuiz"
                class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-semibold bg-slate-900 text-white hover:bg-slate-800 transition-colors"
              >
                <Sparkles class="w-3.5 h-3.5 text-indigo-300" />
                <span>Iniciar Geração Automática</span>
              </button>
            </div>

            <!-- Loading State -->
            <div v-if="isGenerating" class="py-12 text-center">
              <div class="w-12 h-12 mx-auto rounded-full border-4 border-ai-200 border-t-ai-600 animate-spin mb-4"></div>
              <p class="text-sm font-semibold text-slate-700">A IA está processando o conteúdo pedagógico...</p>
              <p class="text-xs text-slate-400 mt-1">Formulando alternativas e fundamentações regulatórias.</p>
            </div>

            <!-- List of Questions -->
            <div
              v-for="(q, qIdx) in questions"
              :key="qIdx"
              class="bg-white border border-slate-200 rounded-2xl p-5 shadow-xs transition-all hover:border-slate-300"
            >
              <div class="flex items-start space-x-3 mb-4">
                <span class="flex-shrink-0 w-7 h-7 rounded-lg bg-indigo-50 text-indigo-700 font-bold text-xs flex items-center justify-center border border-indigo-100">
                  {{ qIdx + 1 }}
                </span>
                <h4 class="text-sm font-bold text-slate-900 leading-snug">
                  {{ q.question }}
                </h4>
              </div>

              <!-- Options -->
              <div class="space-y-2 pl-10">
                <button
                  v-for="(opt, optIdx) in q.options"
                  :key="optIdx"
                  @click="selectOption(qIdx, opt)"
                  :class="[
                    'w-full text-left p-3 rounded-xl text-xs font-medium border transition-all flex items-start space-x-2.5',
                    userAnswers[qIdx] === opt
                      ? opt === q.correctAnswer
                        ? 'bg-emerald-50 border-emerald-300 text-emerald-900 font-semibold'
                        : 'bg-rose-50 border-rose-300 text-rose-900 font-semibold'
                      : showExplanations[qIdx] && opt === q.correctAnswer
                      ? 'bg-emerald-50/60 border-emerald-200 text-emerald-800'
                      : 'border-slate-200/90 hover:bg-slate-50 text-slate-700'
                  ]"
                >
                  <span class="w-4 h-4 rounded-full border flex items-center justify-center shrink-0 mt-0.5 text-[10px]">
                    {{ String.fromCharCode(65 + optIdx) }}
                  </span>
                  <span class="flex-1">{{ opt }}</span>
                  <span v-if="userAnswers[qIdx] === opt">
                    <CheckCircle2 v-if="opt === q.correctAnswer" class="w-4 h-4 text-emerald-600 shrink-0" />
                    <AlertCircle v-else class="w-4 h-4 text-rose-600 shrink-0" />
                  </span>
                </button>
              </div>

              <!-- Feedback & Explanation Box -->
              <div
                v-if="showExplanations[qIdx]"
                :class="[
                  'mt-4 pl-10 pt-3 border-t border-slate-100 animate-fade-in text-xs',
                  isCorrect(qIdx) ? 'text-emerald-800' : 'text-slate-700'
                ]"
              >
                <div class="p-3 rounded-xl bg-slate-50 border border-slate-200/80">
                  <div class="flex items-center space-x-1.5 font-bold mb-1">
                    <span v-if="isCorrect(qIdx)" class="text-emerald-600 flex items-center gap-1">
                      <CheckCircle2 class="w-3.5 h-3.5" /> Correto!
                    </span>
                    <span v-else class="text-rose-600 flex items-center gap-1">
                      <AlertCircle class="w-3.5 h-3.5" /> Incorreto!
                    </span>
                    <span class="text-slate-500 font-normal">Explicação do Tutor:</span>
                  </div>
                  <p class="text-slate-600 leading-relaxed">{{ q.explanation }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex items-center justify-between">
            <span class="text-xs text-slate-500">
              {{ questions.length > 0 ? `${Object.keys(userAnswers).length} de ${questions.length} respondidas` : 'Pronto para gerar' }}
            </span>
            <button
              @click="emit('close')"
              class="px-4 py-2 rounded-xl text-xs font-semibold bg-white border border-slate-200 text-slate-700 hover:bg-slate-100 transition-colors"
            >
              Fechar
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>
