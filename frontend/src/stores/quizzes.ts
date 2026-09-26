import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useAuthStore } from './auth';

export interface Question {
  id: number;
  text: string;
  options: string[];
  correctIndex: number;
  explanation: string;
}

export interface QuizItem {
  id: string;
  title: string;
  category: string;
  difficulty: 'Iniciante' | 'Intermediário' | 'Avançado';
  questionsCount: number;
  estimatedMinutes: number;
  questions: Question[];
}

export interface UserQuizProgress {
  status: 'PENDING' | 'COMPLETED';
  score?: number;
  completedAt?: string;
  answers?: Record<number, number>;
}

export const initialQuizzes: QuizItem[] = [
  {
    id: 'quiz-01',
    title: 'Princípios e Governança do Modelo Cooperativo',
    category: 'Cultura Cooperativa',
    difficulty: 'Iniciante',
    questionsCount: 3,
    estimatedMinutes: 5,
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
];

// Progresso inicial limpo: todos os quizzes iniciam pendentes para todos os colaboradores
const initialUserQuizzesMap: Record<string, Record<string, UserQuizProgress>> = {};

export const useQuizStore = defineStore('quizzes', () => {
  const authStore = useAuthStore();

  // Lista mestre de quizzes disponíveis (salva no localStorage para permitir adição com IA)
  const savedMasterQuizzes = localStorage.getItem('coop_master_quizzes');
  const quizzes = ref<QuizItem[]>(savedMasterQuizzes ? JSON.parse(savedMasterQuizzes) : initialQuizzes);

  // Mapa de progresso dos usuários: userId -> quizId -> UserQuizProgress
  const userProgress = ref<Record<string, Record<string, UserQuizProgress>>>({});

  // Inicializa mapa de progresso
  function initUserProgress(userId: string) {
    if (!userProgress.value[userId]) {
      const saved = localStorage.getItem(`coop_quiz_progress_${userId}`);
      if (saved) {
        userProgress.value[userId] = JSON.parse(saved);
      } else if (initialUserQuizzesMap[userId]) {
        userProgress.value[userId] = { ...initialUserQuizzesMap[userId] };
        localStorage.setItem(`coop_quiz_progress_${userId}`, JSON.stringify(userProgress.value[userId]));
      } else {
        userProgress.value[userId] = {};
      }
    }
  }

  // Quizzes enriquecidos para o usuário ativo corrente
  const quizzesForCurrentUser = computed(() => {
    const currentUserId = authStore.currentUser.id;
    initUserProgress(currentUserId);
    const userMap = userProgress.value[currentUserId] || {};

    return quizzes.value.map((q) => {
      const p = userMap[q.id];
      return {
        ...q,
        status: (p?.status || 'PENDING') as 'PENDING' | 'COMPLETED',
        score: p?.score,
        completedAt: p?.completedAt,
        answers: p?.answers,
      };
    });
  });

  const completedQuizzesCount = computed(() => {
    return quizzesForCurrentUser.value.filter((q) => q.status === 'COMPLETED').length;
  });

  const averageQuizScore = computed(() => {
    const completed = quizzesForCurrentUser.value.filter((q) => q.status === 'COMPLETED' && q.score !== undefined);
    if (completed.length === 0) return 0;
    const sum = completed.reduce((acc, curr) => acc + (curr.score || 0), 0);
    return Math.round(sum / completed.length);
  });

  function saveQuizAttempt(quizId: string, score: number, answers: Record<number, number>) {
    const userId = authStore.currentUser.id;
    initUserProgress(userId);

    userProgress.value[userId][quizId] = {
      status: 'COMPLETED',
      score,
      completedAt: new Date().toLocaleDateString('pt-BR'),
      answers,
    };

    localStorage.setItem(`coop_quiz_progress_${userId}`, JSON.stringify(userProgress.value[userId]));
  }

  function resetQuizAttempt(quizId: string) {
    const userId = authStore.currentUser.id;
    initUserProgress(userId);

    if (userProgress.value[userId][quizId]) {
      userProgress.value[userId][quizId] = {
        status: 'PENDING',
        score: undefined,
        answers: {},
      };
      localStorage.setItem(`coop_quiz_progress_${userId}`, JSON.stringify(userProgress.value[userId]));
    }
  }

  function addAiGeneratedQuiz(newQuiz: QuizItem) {
    quizzes.value.push(newQuiz);
    localStorage.setItem('coop_master_quizzes', JSON.stringify(quizzes.value));
  }

  function resetAllQuizzes() {
    userProgress.value = {};
    Object.keys(localStorage).forEach((k) => {
      if (k.startsWith('coop_quiz_progress_')) {
        localStorage.removeItem(k);
      }
    });
  }

  return {
    quizzes,
    quizzesForCurrentUser,
    completedQuizzesCount,
    averageQuizScore,
    saveQuizAttempt,
    resetQuizAttempt,
    resetAllQuizzes,
    addAiGeneratedQuiz,
    initUserProgress,
  };
});
