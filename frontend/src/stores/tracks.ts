import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { Track, Module, Lesson } from '@/types';
import { api } from '@/api/client';

export const mockTracks: Track[] = [
  {
    id: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    title: 'Cultura & Governança Cooperativista',
    description: 'Compreenda a essência do cooperativismo de crédito, seus 7 princípios fundamentais e os mecanismos de tomada de decisão democrática.',
    targetDepartment: 'Institucional & Cooperados',
    estimatedHours: 8,
    isActive: true,
    slaDays: 14,
    modules: [
      {
        id: 'mod-101',
        trackId: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        title: 'Módulo 1: Fundamentos do Cooperativismo',
        description: 'Origem histórica, princípios de Rochdale e a distinção entre banco tradicional e sociedade cooperativa.',
        orderIndex: 1,
        lessons: [
          {
            id: 'les-101-1',
            moduleId: 'mod-101',
            title: 'Os 7 Princípios do Cooperativismo no Cotidiano',
            orderIndex: 1,
            estimatedMinutes: 25,
            completed: true,
            contentMarkdown: `# Os 7 Princípios do Cooperativismo no Cotidiano

Bem-vindo à sua jornada de integração! Como cooperativa de crédito, nossa razão de existir não é maximizar o lucro de acionistas anônimos, mas promover a **prosperidade econômica e social de nossos associados e da comunidade local**.

---

## 1. Adesão Voluntária e Livre
As cooperativas são organizações voluntárias, abertas a todas as pessoas aptas a utilizar os seus serviços e dispostas a assumir as responsabilidades como membros, sem discriminação social, racial, política, religiosa ou de gênero.

## 2. Gestão Democrática pelos Membros
> "Uma pessoa, um voto."

Diferente de sociedades anônimas onde o peso da decisão depende do número de cotas de capital, na nossa cooperativa **cada cooperado possui igual poder de voto** nas Assembleias Gerais (AGO e AGE).

## 3. Participação Econômica dos Membros
Os cooperados contribuem equitativamente para o capital da sua cooperativa. Os resultados financeiros positivos gerados no exercício são denominados **Sobras**, e retornam aos associados proporcionalmente à sua movimentação financeira.

## 4. Autonomia e Independência
As cooperativas são organizações autônomas de ajuda mútua, controladas pelos seus membros. Qualquer parceria institucional mantém essa premissa inviolável.

## 5. Educação, Formação e Informação
Investimento contínuo no desenvolvimento profissional dos colaboradores e na educação financeira dos cooperados. Este programa de onboarding é uma expressão viva desse princípio!

## 6. Intercooperação
O fortalecimento mútuo entre cooperativas de diferentes ramos (crédito, agropecuário, saúde, transporte) constrói uma rede econômica resiliente.

## 7. Interesse pela Comunidade
Parte dos resultados da cooperativa é destinada ao Fundo de Assistência Técnica, Educacional e Social (FATES) e a projetos sociais de desenvolvimento sustentável da nossa região.

---

### Checklist de Fixação
- [ ] Compreendi a diferença entre lucro (banco) e sobras (cooperativa).
- [ ] Sei onde consultar as datas da Assembleia Geral Ordinária (AGO).
- [ ] Reconheço o impacto das iniciativas do fundo social em nossa comunidade.`,
          },
          {
            id: 'les-101-2',
            moduleId: 'mod-101',
            title: 'Estrutura de Governança, Conselhos e Assembleias',
            orderIndex: 2,
            estimatedMinutes: 30,
            completed: false,
            contentMarkdown: `# Estrutura de Governança e Tomada de Decisão

A governança cooperativa é sustentada por três pilares essenciais: transparência, prestação de contas (accountability) e conformidade regulatória perante o Banco Central do Brasil (BACEN).

---

## Órgãos Estatutários

### 1. Assembleia Geral (Soberania)
É a instância máxima deliberativa. Realiza-se ordinariamente até o fim de abril para:
* Aprovação das contas e demonstrações financeiras;
* Destinação das sobras líquidas apuradas;
* Eleição dos membros do Conselho de Administração e Conselho Fiscal.

### 2. Conselho de Administração (Estratégia)
Eleito pelos cooperados, é responsável por fixar as diretrizes estratégicas de negócio, políticas de crédito, expansão de agências e supervisão da Diretoria Executiva.

### 3. Diretoria Executiva (Gestão)
Profissionais contratados com dedicação integral à operação diária da cooperativa, garantindo eficiência, cumprimento de metas operacionais e conformidade com as normas do BACEN.

### 4. Conselho Fiscal (Fiscalização Independente)
Composto por cooperados eleitos, com atuação autônoma, fiscalizando de forma assídua as operações contábeis, livros fiscais e conformidade dos atos de gestão.

---

> **Lembre-se:** Como colaborador, seu dever ético é fornecer informações fidedignas a todas as instâncias de governança, preservando a confiança depositada por mais de 50 mil cooperados associados.`,
          },
        ],
      },
      {
        id: 'mod-102',
        trackId: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        title: 'Módulo 2: Ética e Canal de Integridade',
        description: 'Conduta esperada, prevenção a conflito de interesses e uso do Canal de Denúncias.',
        orderIndex: 2,
        lessons: [
          {
            id: 'les-102-1',
            moduleId: 'mod-102',
            title: 'Código de Conduta e Prevenção de Conflitos',
            orderIndex: 1,
            estimatedMinutes: 20,
            completed: false,
            contentMarkdown: `# Código de Conduta e Ética Cooperativista

Nossa reputação é o nosso maior patrimônio. O Código de Conduta se aplica a todos os colaboradores, estagiários, executivos e conselheiros.

---

## Princípios Intransigíveis
1. **Confidencialidade Estrita:** Informações financeiras de cooperados jamais devem ser discutidas em locais públicos ou compartilhadas fora do sistema institucional.
2. **Conflito de Interesses:** Não é permitido conceder tratamento privilegiado a parentes ou parceiros comerciais em operações de crédito.
3. **Brindes e Presentes:** Proibido receber presentes que ultrapassem o valor simbólico de R$ 100,00 ou que possam gerar reciprocidade indevida.
4. **Canal de Denúncias Independente:** Qualquer conduta irregular ou assédio pode ser relatada de forma 100% anônima pelo canal 0800 e portal seguro, sem retaliações.`,
          },
        ],
      },
    ],
  },
  {
    id: 'b1ffcd88-8d1c-4fe9-aa7e-7cc8ce491b22',
    title: 'Segurança da Informação, LGPD & Compliance',
    description: 'Normas de sigilo bancário, Lei Geral de Proteção de Dados (LGPD) e diretrizes de Prevenção à Lavagem de Dinheiro (PLD-FT).',
    targetDepartment: 'Conformidade & Risco',
    estimatedHours: 12,
    isActive: true,
    slaDays: 7,
    modules: [
      {
        id: 'mod-201',
        trackId: 'b1ffcd88-8d1c-4fe9-aa7e-7cc8ce491b22',
        title: 'Módulo 1: LGPD e Sigilo Bancário',
        description: 'Tratamento de dados pessoais de cooperados segundo a Lei 13.709/2018 e LC 105/2001.',
        orderIndex: 1,
        lessons: [
          {
            id: 'les-201-1',
            moduleId: 'mod-201',
            title: 'Tratamento de Dados e Bases Legais na LGPD',
            orderIndex: 1,
            estimatedMinutes: 35,
            completed: false,
            contentMarkdown: `# LGPD no Ambiente Cooperativo de Crédito

A Lei nº 13.709/2018 (Lei Geral de Proteção de Dados) estabelece diretrizes rigorosas para coleta, processamento, armazenamento e descarte de dados pessoais sensíveis.

---

## Bases Legais Aplicadas ao Crédito
* **Execução de Contrato:** Coleta de dados necessária para abertura de conta corrente, concessão de empréstimos e emissão de cartões.
* **Cumprimento de Obrigação Legal/Regulatória:** Normativas do Banco Central (BACEN), Receita Federal e COAF (Lei nº 9.613/98).
* **Proteção do Crédito:** Consulta e envio de informações a órgãos de proteção ao crédito (SCR/Bacen, Serasa, Boa Vista).

---

## Boas Práticas Obrigatórias no Dia a Dia
- Nunca envie relatórios contendo CPF ou saldos por e-mails externos não criptografados.
- Ao se afastar da mesa de trabalho, bloqueie imediatamente a tela do computador (\`Windows + L\` ou \`Super + L\`).
- Descarte documentos físicos com dados de associados exclusivamente nas fragmentadoras seguras de papel.
- Comunique incidentes de segurança cibernética imediatamente ao nosso DPO (Data Protection Officer).`,
          },
          {
            id: 'les-201-2',
            moduleId: 'mod-201',
            title: 'Prevenção à Lavagem de Dinheiro e Financiamento ao Terrorismo (PLD-FT)',
            orderIndex: 2,
            estimatedMinutes: 30,
            completed: false,
            contentMarkdown: `# Prevenção à Lavagem de Dinheiro e Financiamento ao Terrorismo (PLD-FT)

Em conformidade com a Circular BACEN nº 3.978/2020 e a Lei nº 9.613/1998, as cooperativas de crédito desempenham papel crucial na blindagem do Sistema Financeiro Nacional.

---

## As Três Fases da Lavagem de Dinheiro
1. **Colocação (Placement):** Inserção do recurso ilícito no sistema financeiro através de fracionamento de depósitos em dinheiro em espécie.
2. **Ocultação (Layering):** Realização de múltiplas transações complexas para mascarar a origem e titularidade dos fundos.
3. **Integração (Integration):** Reintrodução do capital na economia formal sob a aparência de rendimentos legítimos (compra de imóveis, empresas de fachada).

---

## Procedimentos KYC (Know Your Customer)
Todo colaborador deve assegurar:
* Atualização cadastral periódica de cooperados;
* Identificação de Pessoas Expostas Politicamente (PEP);
* Comunicação automática ao COAF de transações em espécie iguais ou superiores a R$ 50.000,00;
* Comunicação de operações suspeitas que destoem do perfil econômico cadastrado.`,
          },
        ],
      },
    ],
  },
  {
    id: 'c2eedf77-7e2d-4ef0-998f-8dd9df502c33',
    title: 'Sistemas Operacionais & Core Bancário',
    description: 'Domínio das ferramentas operacionais, abertura de contas, cota capital e esteira de crédito cooperativo.',
    targetDepartment: 'Operações & Agências',
    estimatedHours: 16,
    isActive: true,
    slaDays: 21,
    modules: [
      {
        id: 'mod-301',
        trackId: 'c2eedf77-7e2d-4ef0-998f-8dd9df502c33',
        title: 'Módulo 1: Admissão e Cota Capital',
        description: 'Fluxo de admissão de novos cooperados e integralização do capital social.',
        orderIndex: 1,
        lessons: [
          {
            id: 'les-301-1',
            moduleId: 'mod-301',
            title: 'Admissão de Cooperado e Integralização da Cota Capital',
            orderIndex: 1,
            estimatedMinutes: 40,
            completed: false,
            contentMarkdown: `# Admissão de Cooperados e Integralização de Cota Capital

Diferente de um cliente bancário comum, quem ingressa em uma cooperativa de crédito se torna **dono** do empreendimento através da subscrição de sua cota capital.

---

## O Que é a Cota Capital?
A cota capital representa a fração de participação societária do associado no patrimônio líquido da cooperativa:
- Confere o direito de voto na Assembleia Geral;
- Remuneração anual por juros ao capital (limitados à taxa Selic);
- Pode ser resgatada nos casos de desligamento do cooperado, conforme prazos estatutários.

## Passo a Passo Operacional no Core Bancário
1. Coleta e validação digital de comprovantes de renda e residência;
2. Consulta de restritivos cadastrais e capacidade civil;
3. Definição do valor de integralização inicial (ex: R$ 50,00);
4. Assinatura eletrônica do Termo de Admissão e Estatuto Social;
5. Ativação da conta corrente vinculada e entrega dos canais digitais (App e Internet Banking).`,
          },
        ],
      },
    ],
  },
];

export const useTrackStore = defineStore('tracks', () => {
  const tracks = ref<Track[]>(mockTracks);
  const isLoading = ref<boolean>(false);
  const activeTrackId = ref<string | null>(mockTracks[0].id);
  const activeLessonId = ref<string | null>(mockTracks[0].modules[0].lessons[0].id);

  // Initialize completed lessons from localStorage or mock defaults
  const savedCompletions = localStorage.getItem('coop_completed_lessons');
  const completedLessonIds = ref<Set<string>>(
    savedCompletions ? new Set(JSON.parse(savedCompletions)) : new Set(['les-101-1'])
  );

  const activeTrack = computed(() => {
    return tracks.value.find((t) => t.id === activeTrackId.value) || tracks.value[0];
  });

  const activeLesson = computed(() => {
    if (!activeTrack.value) return undefined;
    for (const mod of activeTrack.value.modules) {
      const match = mod.lessons.find((l) => l.id === activeLessonId.value);
      if (match) return match;
    }
    return activeTrack.value.modules[0]?.lessons[0];
  });

  const totalLessonsCount = computed(() => {
    let count = 0;
    for (const t of tracks.value) {
      for (const m of t.modules) {
        count += m.lessons.length;
      }
    }
    return count;
  });

  const completedCount = computed(() => completedLessonIds.value.size);

  const overallProgressPercent = computed(() => {
    if (totalLessonsCount.value === 0) return 0;
    return Math.round((completedCount.value / totalLessonsCount.value) * 100);
  });

  function getTrackProgressPercent(trackId: string): number {
    const t = tracks.value.find((track) => track.id === trackId);
    if (!t) return 0;
    let total = 0;
    let completed = 0;
    for (const m of t.modules) {
      for (const l of m.lessons) {
        total++;
        if (completedLessonIds.value.has(l.id)) {
          completed++;
        }
      }
    }
    if (total === 0) return 0;
    return Math.round((completed / total) * 100);
  }

  function isLessonCompleted(lessonId: string): boolean {
    return completedLessonIds.value.has(lessonId);
  }

  function toggleLessonCompletion(lessonId: string) {
    if (completedLessonIds.value.has(lessonId)) {
      completedLessonIds.value.delete(lessonId);
    } else {
      completedLessonIds.value.add(lessonId);
    }
    localStorage.setItem('coop_completed_lessons', JSON.stringify(Array.from(completedLessonIds.value)));
  }

  function markLessonComplete(lessonId: string) {
    completedLessonIds.value.add(lessonId);
    localStorage.setItem('coop_completed_lessons', JSON.stringify(Array.from(completedLessonIds.value)));
  }

  function selectLesson(trackId: string, lessonId: string) {
    activeTrackId.value = trackId;
    activeLessonId.value = lessonId;
  }

  async function fetchTracks() {
    isLoading.value = true;
    try {
      const serverTracks = await api.getTracks();
      if (serverTracks && serverTracks.length > 0) {
        // Merge server tracks with client modules/lessons if server returns minimal metadata
        tracks.value = serverTracks.map((st) => {
          const matchedMock = mockTracks.find((mt) => mt.id === st.id || mt.title.toLowerCase() === st.title.toLowerCase());
          return {
            ...st,
            slaDays: st.slaDays || matchedMock?.slaDays || 14,
            modules: (st.modules && st.modules.length > 0) ? st.modules : (matchedMock?.modules || []),
          };
        });
      }
    } catch {
      // Backend may be offline or not returning tracks yet; gracefully keep comprehensive mock data
    } finally {
      isLoading.value = false;
    }
  }

  return {
    tracks,
    isLoading,
    activeTrackId,
    activeLessonId,
    activeTrack,
    activeLesson,
    completedLessonIds,
    totalLessonsCount,
    completedCount,
    overallProgressPercent,
    getTrackProgressPercent,
    isLessonCompleted,
    toggleLessonCompletion,
    markLessonComplete,
    selectLesson,
    fetchTracks,
  };
});
