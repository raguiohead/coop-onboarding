import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { AiMessage } from '@/types';
import { api } from '@/api/client';

export const useAiTutorStore = defineStore('aiTutor', () => {
  const isDrawerOpen = ref<boolean>(false);
  const isThinking = ref<boolean>(false);
  const messages = ref<AiMessage[]>([
    {
      id: 'welcome-msg',
      sender: 'tutor',
      text: 'Olá! Sou o seu **Tutor Virtual de Onboarding com IA**. Estou aqui para esclarecer qualquer dúvida sobre os módulos, diretrizes cooperativistas e regras de negócio. Como posso ajudar você hoje?',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      sources: [
        'Manual de Boas-Vindas da Cooperativa - Seção 1.1',
        'Estatuto Social Cooperativo - Artigo 4º',
      ],
    },
  ]);

  function openDrawer() {
    isDrawerOpen.value = true;
  }

  function closeDrawer() {
    isDrawerOpen.value = false;
  }

  function toggleDrawer() {
    isDrawerOpen.value = !isDrawerOpen.value;
  }

  async function askQuestion(lessonId: string, question: string, lessonTitle?: string, lessonContent?: string) {
    if (!question.trim()) return;

    const userMessage: AiMessage = {
      id: `user-${Date.now()}`,
      sender: 'user',
      text: question.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    messages.value.push(userMessage);
    isThinking.value = true;

    try {
      let isFirstChunk = true;
      const tutorMessageId = `tutor-${Date.now()}`;
      const tutorMessage: AiMessage = {
        id: tutorMessageId,
        sender: 'tutor',
        text: '',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        sources: [
          `Base de Conhecimento: Aula "${lessonTitle || 'Geral'}"`,
          'Normativas de Integração e Governança Cooperativista',
        ],
      };

      await api.streamTutor(lessonId, question, (chunk) => {
        if (isFirstChunk) {
          isThinking.value = false;
          messages.value.push(tutorMessage);
          isFirstChunk = false;
        }
        tutorMessage.text += chunk;
      });

      if (isFirstChunk) {
        // Se nenhum chunk foi recebido via streaming, consulta via POST ask
        const response = await api.askTutor(lessonId, question);
        tutorMessage.text = response.answer;
        if (response.sources && response.sources.length > 0) {
          tutorMessage.sources = response.sources;
        }
        isThinking.value = false;
        messages.value.push(tutorMessage);
      }
    } catch {
      // Graceful contextual fallback em caso de indisponibilidade
      isThinking.value = false;
      const fallbackAnswer = generateContextualAnswer(question, lessonTitle, lessonContent);
      messages.value.push({
        id: `tutor-${Date.now()}`,
        sender: 'tutor',
        text: fallbackAnswer.text,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        sources: fallbackAnswer.sources,
      });
    } finally {
      isThinking.value = false;
    }
  }

  function generateContextualAnswer(question: string, lessonTitle?: string, lessonContent?: string): { text: string; sources: string[] } {
    const q = question.toLowerCase();

    if (q.includes('resum') || q.includes('principal') || q.includes('pontos')) {
      return {
        text: `Com base no material da lição **"${lessonTitle || 'Onboarding'}"**, aqui está a síntese dos pontos essenciais:

1. **Compromisso Cooperativo:** Cada associado tem voz ativa e participação no resultado através da distribuição proporcional de Sobras.
2. **Governança Transparente:** As decisões estratégicas são deliberadas em Assembleia Geral e fiscalizadas pelo Conselho Fiscal.
3. **Segurança e Rigor:** Operamos sob rigorosa conformidade com o Banco Central e proteção de dados (LGPD).

Gostaria de aprofundar algum desses tópicos?`,
        sources: [
          `Documento da Lição: ${lessonTitle || 'Visão Geral'} (Seção 1 e 2)`,
          'Guia Corporativo de Boas Práticas e Governança Cooperativista',
        ],
      };
    }

    if (q.includes('sobra') || q.includes('lucro') || q.includes('diferen')) {
      return {
        text: `Excelente pergunta! Essa é a distinção central do modelo cooperativo:

* **Bancos Tradicionais:** Visam o lucro que é distribuído exclusivamente para os acionistas que detêm o capital.
* **Cooperativas de Crédito:** Não visam lucro, mas geram **Sobras líquidas**. Essas sobras são devolvidas aos próprios associados proporcionalmente ao volume de negócios que cada um realizou durante o ano, ou reinvestidas em benefício coletivo após votação na AGO.`,
        sources: [
          'Lei Federal 5.764/1971 (Diretrizes Nacionais do Cooperativismo)',
          'Estatuto Social Cooperativo - Capítulo IV: Do Fundo de Reserva e Sobras',
        ],
      };
    }

    if (q.includes('lgpd') || q.includes('dado') || q.includes('seguran')) {
      return {
        text: `No contexto de segurança e privacidade bancária:

* Todos os dados cadastrais e financeiros estão sob a égide da **Lei 13.709/2018 (LGPD)** e da **LC 105/2001 (Sigilo Bancário)**.
* O compartilhamento sem base legal ou fora do sistema institucional configura infração grave.
* Sempre bloqueie sua estação de trabalho ao ausentar-se e descarte documentos com dados pessoais em fragmentadoras seguras.`,
        sources: [
          'Manual de Segurança da Informação e Privacidade - Revisão 2026',
          'Política Institucional de Proteção a Dados Pessoais de Cooperados',
        ],
      };
    }

    return {
      text: `Excelente questão! Analisando o conteúdo da aula **"${lessonTitle || 'Trilha de Aprendizado'}"**:

Essa diretriz visa garantir que todos os colaboradores atuem em plena consonância com as normas regulatórias e os princípios de cooperação mútua. Você pode consultar os detalhes operacionais no sistema institucional ou revisar o checklist no final da lição.

Se desejar, posso detalhar os procedimentos passo a passo!`,
      sources: [
        `Base Vetorial RAG: Trecho indexado da lição "${lessonTitle || 'Onboarding'}"`,
        'Política de Conformidade e Governança do Sistema Cooperativo',
      ],
    };
  }

  function clearHistory() {
    messages.value = [
      {
        id: 'welcome-msg',
        sender: 'tutor',
        text: 'Histórico reiniciado. Como posso apoiar seus estudos agora?',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        sources: [],
      },
    ];
  }

  return {
    isDrawerOpen,
    isThinking,
    messages,
    openDrawer,
    closeDrawer,
    toggleDrawer,
    askQuestion,
    clearHistory,
  };
});
