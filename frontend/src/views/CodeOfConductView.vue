<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  ShieldCheck,
  Scale,
  HeartHandshake,
  Users,
  Eye,
  AlertOctagon,
  FileText,
  PhoneCall,
  CheckCircle2,
  ChevronRight,
  BookOpen,
  Lock,
} from 'lucide-vue-next';

const router = useRouter();
const acceptedAcknowledgement = ref(false);
const showAcknowledgeToast = ref(false);

function handleAcknowledge() {
  acceptedAcknowledgement.value = true;
  showAcknowledgeToast.value = true;
  setTimeout(() => {
    showAcknowledgeToast.value = false;
  }, 4000);
}
</script>

<template>
  <div class="space-y-10 pb-16 animate-fade-in max-w-5xl mx-auto">
    <!-- Breadcrumb & Header Hero -->
    <div class="space-y-4">
      <nav class="flex items-center space-x-2 text-xs text-slate-500">
        <router-link to="/" class="hover:text-brand-700 transition-colors">Início</router-link>
        <ChevronRight class="w-3.5 h-3.5" />
        <span class="text-slate-900 font-semibold">Código de Conduta Ética</span>
      </nav>

      <div class="bg-gradient-to-br from-slate-900 via-brand-950 to-slate-900 rounded-3xl p-8 sm:p-10 text-white relative overflow-hidden shadow-xl border border-slate-800">
        <div class="absolute -right-10 -bottom-10 w-80 h-80 bg-brand-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="relative z-10 max-w-2xl space-y-4">
          <div class="inline-flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold bg-brand-500/20 text-brand-300 border border-brand-500/30">
            <Scale class="w-3.5 h-3.5" />
            <span>Governança & Integridade Cooperativa</span>
          </div>
          <h1 class="text-2xl sm:text-4xl font-extrabold tracking-tight font-sans">
            Código de Conduta Ética & Diretrizes Institucionais
          </h1>
          <p class="text-sm sm:text-base text-slate-300 leading-relaxed">
            Nossos princípios norteadores para a construção de um ambiente de trabalho justo, inclusivo e comprometido com o bem-estar dos nossos cooperados, colaboradores e comunidades.
          </p>
        </div>
      </div>
    </div>

    <!-- Toast de confirmação -->
    <transition enter-active-class="transition duration-300 ease-out" enter-from-class="transform translate-y-2 opacity-0" enter-to-class="transform translate-y-0 opacity-100" leave-active-class="transition duration-200 ease-in" leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="showAcknowledgeToast" class="p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 flex items-center space-x-3 shadow-md">
        <CheckCircle2 class="w-5 h-5 text-emerald-600 shrink-0" />
        <p class="text-xs sm:text-sm font-semibold">
          Termo de Ciência registrado com sucesso! Seu compromisso ético foi anexado ao seu prontuário de onboarding.
        </p>
      </div>
    </transition>

    <!-- 1. Mensagem da Liderança -->
    <section class="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-xs space-y-4">
      <div class="flex items-center space-x-3 text-brand-800">
        <div class="w-10 h-10 rounded-xl bg-brand-50 flex items-center justify-center text-brand-700">
          <HeartHandshake class="w-5 h-5" />
        </div>
        <h2 class="text-lg sm:text-xl font-bold text-slate-900">1. Mensagem do Conselho de Administração</h2>
      </div>
      <p class="text-sm text-slate-600 leading-relaxed">
        Como cooperativa de crédito, a confiança não é apenas um valor abstrato — é a base de nossa existência societária. Cada ação tomada por nossos colaboradores impacta diretamente a vida econômica de milhares de famílias associadas. Este Código de Conduta expressa os padrões inegociáveis de retidão, transparência e respeito humano que sustentam nosso modelo cooperativista há décadas.
      </p>
    </section>

    <!-- 2. Pilares Fundamentais (Cards em Grid) -->
    <section class="space-y-6">
      <h2 class="text-lg sm:text-xl font-bold text-slate-900 flex items-center space-x-2">
        <ShieldCheck class="w-5 h-5 text-brand-600" />
        <span>2. Pilares Éticos Fundamentais</span>
      </h2>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs space-y-3">
          <div class="w-8 h-8 rounded-lg bg-teal-50 text-teal-700 flex items-center justify-center">
            <Users class="w-4 h-4" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Respeito & Diversidade Plena</h3>
          <p class="text-xs sm:text-sm text-slate-600 leading-relaxed">
            Repudiamos qualquer forma de discriminação por motivo de raça, gênero, orientação sexual, crença religiosa, deficiência ou convicção política. Promovemos ativamente um ambiente seguro, acolhedor e inclusivo.
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs space-y-3">
          <div class="w-8 h-8 rounded-lg bg-indigo-50 text-indigo-700 flex items-center justify-center">
            <Eye class="w-4 h-4" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Transparência nas Relações</h3>
          <p class="text-xs sm:text-sm text-slate-600 leading-relaxed">
            Comunicação clara, honesta e tempestiva com os cooperados. Nenhum produto ou serviço de crédito deve ser oferecido sem a devida elucidação sobre taxas, encargos e condições de contratação.
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs space-y-3">
          <div class="w-8 h-8 rounded-lg bg-amber-50 text-amber-700 flex items-center justify-center">
            <Scale class="w-4 h-4" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Prevenção ao Conflito de Interesses</h3>
          <p class="text-xs sm:text-sm text-slate-600 leading-relaxed">
            É vedado utilizar a função, cargo ou informações privilegiadas em benefício próprio, de parentes ou terceiros. Decisões de concessão de crédito seguem critérios técnicos e colegiados.
          </p>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-slate-200/80 shadow-xs space-y-3">
          <div class="w-8 h-8 rounded-lg bg-rose-50 text-rose-700 flex items-center justify-center">
            <AlertOctagon class="w-4 h-4" />
          </div>
          <h3 class="text-base font-bold text-slate-900">Tolerância Zero a Assédio</h3>
          <p class="text-xs sm:text-sm text-slate-600 leading-relaxed">
            Não toleramos nenhuma modalidade de assédio moral ou sexual. Comportamentos abusivos, intimidações, humilhações ou pressões que violem a dignidade humana serão objeto de apuração rigorosa.
          </p>
        </div>
      </div>
    </section>

    <!-- 3. Práticas do Cotidiano -->
    <section class="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-xs space-y-6">
      <h2 class="text-lg sm:text-xl font-bold text-slate-900 flex items-center space-x-2">
        <FileText class="w-5 h-5 text-brand-600" />
        <span>3. Diretrizes Operacionais do Dia a Dia</span>
      </h2>

      <div class="space-y-4">
        <div class="p-4 rounded-2xl bg-slate-50 border border-slate-100 flex items-start space-x-3">
          <CheckCircle2 class="w-5 h-5 text-brand-600 shrink-0 mt-0.5" />
          <div>
            <h4 class="text-xs sm:text-sm font-bold text-slate-900">Política de Presentes e Hospitalidades</h4>
            <p class="text-xs text-slate-600 mt-0.5">
              Colaboradores não podem receber presentes de fornecedores ou cooperados cujo valor individual exceda R$ 100,00 ou que possam suscitar favorecimento indevido. Brindes institucionais de baixo valor são permitidos.
            </p>
          </div>
        </div>

        <div class="p-4 rounded-2xl bg-slate-50 border border-slate-100 flex items-start space-x-3">
          <CheckCircle2 class="w-5 h-5 text-brand-600 shrink-0 mt-0.5" />
          <div>
            <h4 class="text-xs sm:text-sm font-bold text-slate-900">Uso Consciente dos Recursos Cooperativos</h4>
            <p class="text-xs text-slate-600 mt-0.5">
              Equipamentos de TI, sistemas corporativos e recursos financeiros da instituição destinam-se exclusivamente às atividades operacionais e educacionais homologadas.
            </p>
          </div>
        </div>

        <div class="p-4 rounded-2xl bg-slate-50 border border-slate-100 flex items-start space-x-3">
          <CheckCircle2 class="w-5 h-5 text-brand-600 shrink-0 mt-0.5" />
          <div>
            <h4 class="text-xs sm:text-sm font-bold text-slate-900">Compromisso Socioambiental (FATES)</h4>
            <p class="text-xs text-slate-600 mt-0.5">
              Participação ativa em iniciativas comunitárias apoiadas pelo Fundo de Assistência Técnica, Educacional e Social da cooperativa.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 4. Canal de Linha Ética e Denúncia -->
    <section class="bg-gradient-to-br from-amber-500/10 via-amber-50 to-orange-50 rounded-3xl p-8 border border-amber-200/80 shadow-xs flex flex-col md:flex-row items-center justify-between gap-6">
      <div class="space-y-2 max-w-xl">
        <div class="flex items-center space-x-2 text-amber-800">
          <Lock class="w-5 h-5" />
          <h3 class="text-base sm:text-lg font-bold">Canal Confidencial de Linha Ética</h3>
        </div>
        <p class="text-xs sm:text-sm text-slate-700 leading-relaxed">
          Presenciou alguma conduta em desacordo com este Código? O Canal de Linha Ética é 100% anônimo, independente e assegura proteção integral contra qualquer retaliação.
        </p>
      </div>

      <div class="shrink-0 flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
        <a
          href="tel:08007770000"
          class="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-5 py-3 rounded-2xl text-xs sm:text-sm font-bold bg-amber-600 hover:bg-amber-700 text-white shadow-md transition-colors"
        >
          <PhoneCall class="w-4 h-4" />
          <span>0800 777 0000 (24h)</span>
        </a>
        <router-link
          to="/suporte"
          class="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-5 py-3 rounded-2xl text-xs sm:text-sm font-bold bg-white text-slate-800 border border-slate-200 hover:bg-slate-50 transition-colors"
        >
          <span>Abrir Relato Online</span>
        </router-link>
      </div>
    </section>

    <!-- Termo de Ciência Interativo -->
    <div class="bg-white rounded-3xl p-8 border border-slate-200/80 shadow-xs text-center space-y-4">
      <h3 class="text-base font-bold text-slate-900">Termo de Leitura e Compromisso Ético</h3>
      <p class="text-xs sm:text-sm text-slate-500 max-w-xl mx-auto">
        Ao confirmar, você atesta que leu, compreendeu e se compromete a cumprir as diretrizes estabelecidas no Código de Conduta Ética da Cooperativa.
      </p>

      <div class="pt-2">
        <button
          v-if="!acceptedAcknowledgement"
          @click="handleAcknowledge"
          class="inline-flex items-center space-x-2 px-6 py-3 rounded-2xl text-sm font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-lg shadow-brand-600/20 hover:scale-[1.02] active:scale-[0.98] transition-all cursor-pointer"
        >
          <CheckCircle2 class="w-4 h-4" />
          <span>Confirmar Leitura e Adesão aos Princípios</span>
        </button>

        <div v-else class="inline-flex items-center space-x-2 px-6 py-3 rounded-2xl text-sm font-bold bg-emerald-100 text-emerald-800 border border-emerald-300">
          <CheckCircle2 class="w-5 h-5 text-emerald-600" />
          <span>Adesão Confirmada e Registrada em sua Trilha</span>
        </div>
      </div>
    </div>
  </div>
</template>
