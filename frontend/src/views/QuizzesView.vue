<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useAiTutorStore } from '@/stores/aiTutor';
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
} from 'lucide-vue-next';

interface Question {
  id: number;
  text: string;
  options: string[];
  correctIndex: number;
  explanation: string;
}

interface QuizItem {
  id: string;
  title: string;
  category: string;
  difficulty: 'Iniciante' | 'Intermediário' | 'Avançado';
  questionsCount: number;
  estimatedMinutes: number;
  status: 'PENDING' | 'COMPLETED';
  score?: number;
  questions: Question[];
}

const authStore = useAuthStore();
const aiTutorStore = useAiTutorStore();

const isQuizModalOpen = ref(false);
const activeQuiz = ref<QuizItem | null>(null);
const currentQuestionIndex = ref(0);
const selectedAnswers = ref<Record<number, number>>({});
const showResults = ref(false);

const quizzesList = ref<QuizItem[]>([
  {
    id: 'quiz-01',
    title: 'Princípios e Governança do Modelo Cooperativo',
    category: 'Cultura Cooperativa',
    difficulty: 'Iniciante',
    questionsCount: 3,
    estimatedMinutes: 5,
    status: 'COMPLETED',
    score: 100,
    questions: [
      {
        id: 1,
        text: 'Qual a principal diferença entre a distribuição de sobras em uma cooperativa e o dividendo de um banco tradicional?',
        options: [
          'Em bancos tradicionais, os dividendos vão para os associados proporcionalmente ao uso.',
          'Em cooperativas, as sobras são distribuídas aos cooperados na proporção de suas operações, enquanto em bancos o lucro remunera o capital dos acionistas.',
          'Cooperativas não geram resultados positivos, operando sempre em déficit controlado.',
          'Bancos e cooperativas utilizam exatamente a mesma sistemática de rateio de lucros.',
        ],
        correctIndex: 1,
        explanation: 'Nas sociedades cooperativas não há busca pelo lucro em benefício de acionistas, mas a geração de Sobras que retornam aos cooperados proporcionalmente à movimentação efetuada.',
      },
      {
        id: 2,
        text: 'Qual o princípio fundamental de votação na Assembleia Geral da nossa cooperativa?',
        options: [
          'Um cooperado, um voto — independentemente da quantidade de cotas de capital.',
          'Voto ponderado pelo saldo da conta corrente.',
          'Apenas cooperados com mais de 10 anos de vínculo possuem direito a voto.',
          'Cada R$ 1.000,00 em cotas concede um voto adicional na assembleia.',
        ],
        correctIndex: 0,
        explanation: 'O 2º Princípio do Cooperativismo consagra a Gestão Democrática pelos membros: cada cooperado tem poder de voto igualitário nas assembleias.',
      },
      {
        id: 3,
        text: 'Qual órgão estatutário atua na fiscalização contínua e autônoma da legalidade e contas da cooperativa?',
        options: [
          'Conselho Fiscal, eleito diretamente pela Assembleia Geral.',
          'Gerência de Recursos Humanos.',
          'Comitê de Marketing e Comunicação.',
          'O banco central regional local.',
        ],
        correctIndex: 0,
        explanation: 'O Conselho Fiscal é órgão estatutário independente composto por membros eleitos pelos próprios cooperados para fiscalizar a administração e contas.',
      },
    ],
  },
  {
    id: 'quiz-02',
    title: 'Prevenção à Lavagem de Dinheiro (PLD) e Sigilo Bancário',
    category: 'Compliance & Riscos',
    difficulty: 'Intermediário',
    questionsCount: 3,
    estimatedMinutes: 6,
    status: 'PENDING',
    questions: [
      {
        id: 1,
        text: 'Quais são as três fases canônicas do processo de Lavagem de Dinheiro reconhecidas pela Lei 9.613/1998?',
        options: [
          'Investigação, Julgamento e Liquidação.',
          'Colocação (Placement), Ocultação (Layering) e Integração (Integration).',
          'Arrecadação, Tributação e Desoneração.',
          'Abordagem, Notificação e Repatriação.',
        ],
        correctIndex: 1,
        explanation: 'A teoria e a legislação internacional tipificam as fases em: Colocação do recurso ilícito no sistema, Ocultação por camadas complexas de transações e Integração à economia formal.',
      },
      {
        id: 2,
        text: 'Em relação ao Sigilo Bancário (Lei Complementar 105/2001), qual a conduta esperada do colaborador?',
        options: [
          'Consultar saldos e movimentações de parentes por curiosidade pessoal.',
          'Manter estrita confidencialidade das operações ativas e passivas dos cooperados, acessando informações estritamente por dever de ofício.',
          'Compartilhar extratos de cooperados em aplicativos de mensagens particulares para agilizar atendimentos.',
          'Divulgar dados cadastrais sempre que solicitado verbalmente por terceiros.',
        ],
        correctIndex: 1,
        explanation: 'O dever de sigilo bancário é indeclinável e o acesso a dados financeiros de cooperados é restrito à finalidade funcional estrita sob pena de demissão e sanção penal.',
      },
      {
        id: 3,
        text: 'Qual instituição federal deve ser comunicada em caso de operações atípicas sem causa econômica ou jurídica aparente?',
        options: [
          'COAF (Conselho de Controle de Atividades Financeiras).',
          'Secretaria Municipal de Finanças.',
          'Conselho Regional de Administração.',
          'Superintendência de Proteção ao Consumidor.',
        ],
        correctIndex: 0,
        explanation: 'As instituições do Sistema Financeiro Nacional são obrigadas por lei a comunicar ao COAF transações que suscitem fundadas suspeitas de lavagem de dinheiro.',
      },
    ],
  },
  {
    id: 'quiz-03',
    title: 'Produtos Cooperativos & Crédito Consciente',
    category: 'Negócios & Atendimento',
    difficulty: 'Iniciante',
    questionsCount: 2,
    estimatedMinutes: 4,
    status: 'PENDING',
    questions: [
      {
        id: 1,
        text: 'O que diferencia a concessão de crédito em uma cooperativa daquela praticada pelo sistema bancário tradicional?',
        options: [
          'O foco na sustentabilidade financeira do associado, taxas justas e retorno das sobras geradas.',
          'Cooperativas não cobram juros de nenhuma modalidade de financiamento.',
          'Crédito concedido sem nenhuma análise cadastral ou comprovação de renda.',
          'O crédito é restrito apenas aos diretores da instituição.',
        ],
        correctIndex: 0,
        explanation: 'A cooperativa visa apoiar o crescimento econômico sustentável do cooperado, praticando taxas justas e devolvendo parte dos juros pagos através das sobras anuais.',
      },
      {
        id: 2,
        text: 'O que é a Cota Capital do associado?',
        options: [
          'A participação societária do cooperado no patrimônio da instituição, constituindo sua copropriedade.',
          'Uma taxa mensal cobrada a fundo perdido pelo uso da agência.',
          'Um imposto retido na fonte pela Receita Federal.',
          'Uma garantia compulsória exigida exclusivamente em caso de inadimplência.',
        ],
        correctIndex: 0,
        explanation: 'A cota capital é o aporte inicial que transforma o cliente em associado e cotista coproprietário da cooperativa de crédito.',
      },
    ],
  },
]);

function startQuiz(quiz: QuizItem) {
  activeQuiz.value = quiz;
  currentQuestionIndex.value = 0;
  selectedAnswers.value = {};
  showResults.value = false;
}

function selectAnswer(questionIdx: number, optionIdx: number) {
  if (showResults.value) return;
  selectedAnswers.value[questionIdx] = optionIdx;
}

function nextQuestion() {
  if (!activeQuiz.value) return;
  if (currentQuestionIndex.value < activeQuiz.value.questions.length - 1) {
    currentQuestionIndex.value++;
  } else {
    finishQuiz();
  }
}

function previousQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--;
  }
}

function finishQuiz() {
  if (!activeQuiz.value) return;
  let correctCount = 0;
  activeQuiz.value.questions.forEach((q, idx) => {
    if (selectedAnswers.value[idx] === q.correctIndex) {
      correctCount++;
    }
  });

  const percentage = Math.round((correctCount / activeQuiz.value.questions.length) * 100);
  activeQuiz.value.score = percentage;
  activeQuiz.value.status = 'COMPLETED';
  showResults.value = true;
}

function resetActiveQuiz() {
  if (activeQuiz.value) {
    activeQuiz.value.status = 'PENDING';
    activeQuiz.value.score = undefined;
    selectedAnswers.value = {};
    currentQuestionIndex.value = 0;
    showResults.value = false;
  }
}

function closeQuizRunner() {
  activeQuiz.value = null;
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
          <span>Avaliações & Fixação Pedagógica</span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight font-sans">
          Central de Quizzes & Simulados
        </h1>
        <p class="text-xs sm:text-sm text-slate-300 mt-1 max-w-2xl">
          Teste seus conhecimentos nos pilares cooperativistas, diretrizes normativas e políticas de crédito com justificativas pedagógicas detalhadas.
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

    <!-- Metrics Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-teal-50 border border-teal-100 flex items-center justify-center text-teal-600">
          <CheckCircle2 class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Quizzes Concluídos</p>
          <p class="text-xl font-extrabold text-slate-900 mt-0.5">
            {{ quizzesList.filter(q => q.status === 'COMPLETED').length }} de {{ quizzesList.length }}
          </p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-100 flex items-center justify-center text-indigo-600">
          <TrendingUp class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Média Geral de Acertos</p>
          <p class="text-xl font-extrabold text-indigo-700 mt-0.5">100%</p>
        </div>
      </div>

      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex items-center space-x-4">
        <div class="w-12 h-12 rounded-xl bg-amber-50 border border-amber-100 flex items-center justify-center text-amber-600">
          <Clock class="w-6 h-6" />
        </div>
        <div>
          <p class="text-xs text-slate-500 font-medium">Pendentes na sua Trilha</p>
          <p class="text-xl font-extrabold text-amber-700 mt-0.5">
            {{ quizzesList.filter(q => q.status === 'PENDING').length }} avaliações
          </p>
        </div>
      </div>
    </div>

    <!-- Active Quiz Runner Modal / Inline View -->
    <div v-if="activeQuiz" class="bg-white rounded-3xl border border-indigo-100 shadow-xl p-6 sm:p-8 animate-fade-in">
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
        <div>
          <span class="text-[11px] font-bold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-100">
            {{ activeQuiz.category }} · Questão {{ currentQuestionIndex + 1 }} de {{ activeQuiz.questions.length }}
          </span>
          <h2 class="text-lg sm:text-xl font-bold text-slate-900 mt-1">
            {{ activeQuiz.title }}
          </h2>
        </div>
        <button
          @click="closeQuizRunner"
          class="text-xs text-slate-400 hover:text-slate-600 font-medium px-3 py-1.5 rounded-lg hover:bg-slate-100 transition-colors"
        >
          Voltar para Lista
        </button>
      </div>

      <!-- If quiz is in progress -->
      <div v-if="!showResults" class="space-y-6">
        <div class="p-4 sm:p-5 rounded-2xl bg-slate-50 border border-slate-200/80">
          <p class="text-sm sm:text-base font-semibold text-slate-900 leading-relaxed">
            {{ activeQuiz.questions[currentQuestionIndex].text }}
          </p>
        </div>

        <!-- Options list -->
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
                'w-6 h-6 rounded-lg text-xs font-bold flex items-center justify-center shrink-0 border mt-0.5',
                selectedAnswers[currentQuestionIndex] === optIdx
                  ? 'bg-indigo-600 text-white border-indigo-600'
                  : 'bg-slate-100 text-slate-600 border-slate-200'
              ]"
            >
              {{ String.fromCharCode(65 + optIdx) }}
            </span>
            <span class="flex-1">{{ option }}</span>
          </button>
        </div>

        <!-- Navigation Buttons -->
        <div class="flex items-center justify-between pt-4 border-t border-slate-100">
          <button
            @click="previousQuestion"
            :disabled="currentQuestionIndex === 0"
            class="px-4 py-2 rounded-xl text-xs font-bold border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-30 disabled:pointer-events-none transition-colors"
          >
            Questão Anterior
          </button>

          <button
            @click="nextQuestion"
            :disabled="selectedAnswers[currentQuestionIndex] === undefined"
            class="px-5 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-gradient-to-r from-brand-600 to-brand-800 hover:from-brand-700 hover:to-brand-900 text-white shadow-sm disabled:opacity-40 disabled:pointer-events-none transition-all cursor-pointer flex items-center space-x-1.5"
          >
            <span>{{ currentQuestionIndex === activeQuiz.questions.length - 1 ? 'Concluir Avaliação' : 'Próxima Questão' }}</span>
            <ArrowRight class="w-4 h-4" />
          </button>
        </div>
      </div>

      <!-- Quiz Completed / Results View -->
      <div v-else class="space-y-8 animate-fade-in">
        <!-- Results Score Header -->
        <div class="text-center p-8 rounded-3xl bg-gradient-to-br from-indigo-50 to-brand-50 border border-indigo-100">
          <div class="w-16 h-16 mx-auto rounded-2xl bg-white shadow-md flex items-center justify-center text-indigo-600 mb-3 border border-indigo-100">
            <Award class="w-9 h-9" />
          </div>
          <h3 class="text-xl sm:text-2xl font-extrabold text-slate-900">
            Avaliação Concluída com Sucesso!
          </h3>
          <p class="text-xs sm:text-sm text-slate-600 mt-1">
            Seu desempenho foi registrado no histórico do seu plano individual de capacitação.
          </p>

          <div class="inline-flex items-center space-x-2 mt-4 px-4 py-2 rounded-2xl bg-white border border-indigo-200 shadow-xs">
            <span class="text-xs font-bold text-slate-500 uppercase tracking-wider">Aproveitamento:</span>
            <span class="text-2xl font-black text-indigo-700">{{ activeQuiz.score }}%</span>
          </div>
        </div>

        <!-- Detailed Explanations / Gabarito -->
        <div class="space-y-4">
          <h4 class="text-sm font-bold text-slate-800 flex items-center gap-2">
            <BookOpen class="w-4 h-4 text-indigo-600" /> Gabarito Comentado & Justificativas Normativas
          </h4>

          <div
            v-for="(q, qIdx) in activeQuiz.questions"
            :key="qIdx"
            class="p-5 rounded-2xl border bg-white shadow-xs space-y-3"
            :class="selectedAnswers[qIdx] === q.correctIndex ? 'border-emerald-200 bg-emerald-50/20' : 'border-rose-200 bg-rose-50/20'"
          >
            <div class="flex items-start justify-between gap-2">
              <p class="text-xs sm:text-sm font-bold text-slate-900">
                {{ qIdx + 1 }}. {{ q.text }}
              </p>
              <span
                :class="[
                  'text-[10px] font-bold px-2 py-0.5 rounded uppercase shrink-0',
                  selectedAnswers[qIdx] === q.correctIndex
                    ? 'bg-emerald-100 text-emerald-800 border border-emerald-200'
                    : 'bg-rose-100 text-rose-800 border border-rose-200'
                ]"
              >
                {{ selectedAnswers[qIdx] === q.correctIndex ? 'Correto' : 'Incorreto' }}
              </span>
            </div>

            <!-- Option details -->
            <div class="text-xs space-y-1 text-slate-600 pl-2 border-l-2 border-slate-200">
              <p>
                <strong class="text-slate-800">Sua resposta:</strong>
                {{ q.options[selectedAnswers[qIdx]] }}
              </p>
              <p v-if="selectedAnswers[qIdx] !== q.correctIndex" class="text-emerald-700">
                <strong>Resposta correta:</strong> {{ q.options[q.correctIndex] }}
              </p>
            </div>

            <!-- Regulatory explanation -->
            <div class="p-3 rounded-xl bg-slate-50 border border-slate-200/80 text-[11.5px] text-slate-700 leading-relaxed">
              <p class="font-semibold text-slate-900 flex items-center gap-1.5 mb-1 text-[11px] uppercase tracking-wide">
                <Sparkles class="w-3 h-3 text-indigo-500" /> Justificativa Pedagógica:
              </p>
              {{ q.explanation }}
            </div>
          </div>
        </div>

        <div class="flex items-center justify-between pt-4 border-t border-slate-100">
          <button
            @click="resetActiveQuiz"
            class="px-4 py-2 rounded-xl text-xs font-semibold border border-slate-200 text-slate-600 hover:bg-slate-50 flex items-center space-x-1.5"
          >
            <RotateCcw class="w-3.5 h-3.5" />
            <span>Refazer Avaliação</span>
          </button>

          <button
            @click="closeQuizRunner"
            class="px-5 py-2.5 rounded-xl text-xs sm:text-sm font-bold bg-slate-900 hover:bg-slate-800 text-white shadow-xs transition-colors"
          >
            Finalizar & Voltar à Lista
          </button>
        </div>
      </div>
    </div>

    <!-- Quizzes Cards Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="quiz in quizzesList"
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
              :class="[
                'text-[10px] font-bold px-2 py-0.5 rounded border uppercase',
                quiz.status === 'COMPLETED'
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                  : 'bg-amber-50 text-amber-700 border-amber-200'
              ]"
            >
              {{ quiz.status === 'COMPLETED' ? 'Concluído' : 'Pendente' }}
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
          <div v-if="quiz.status === 'COMPLETED'" class="mb-4 p-3 rounded-xl bg-emerald-50/60 border border-emerald-100 flex items-center justify-between text-xs">
            <span class="text-emerald-800 font-semibold">Nota Registrada:</span>
            <span class="text-sm font-black text-emerald-700">{{ quiz.score }}%</span>
          </div>

          <button
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
