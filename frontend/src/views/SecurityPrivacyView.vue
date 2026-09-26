<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  ShieldCheck,
  Lock,
  KeyRound,
  FileCheck2,
  AlertTriangle,
  EyeOff,
  UserCheck,
  Mail,
  ChevronRight,
  ExternalLink,
  Cpu,
  BadgeAlert,
} from 'lucide-vue-next';

const router = useRouter();
const activeTab = ref<'lgpd' | 'sigilo' | 'cyber' | 'pld'>('lgpd');
</script>

<template>
  <div class="space-y-10 pb-16 animate-fade-in max-w-5xl mx-auto">
    <!-- Breadcrumbs & Hero -->
    <div class="space-y-4">
      <nav class="flex items-center space-x-2 text-xs text-slate-500">
        <router-link to="/" class="hover:text-brand-700 transition-colors">Início</router-link>
        <ChevronRight class="w-3.5 h-3.5" />
        <span class="text-slate-900 font-semibold">Segurança & Privacidade</span>
      </nav>

      <div class="bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950 rounded-3xl p-8 sm:p-10 text-white relative overflow-hidden shadow-xl border border-slate-800">
        <div class="absolute -right-10 -bottom-10 w-80 h-80 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="relative z-10 max-w-2xl space-y-4">
          <div class="inline-flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
            <Lock class="w-3.5 h-3.5" />
            <span>Proteção de Dados & Sigilo Bancário</span>
          </div>
          <h1 class="text-2xl sm:text-4xl font-extrabold tracking-tight font-sans">
            Segurança da Informação, Privacidade & Conformidade
          </h1>
          <p class="text-sm sm:text-base text-slate-300 leading-relaxed">
            Padrões regulatórios do Banco Central do Brasil (BCB), Lei Geral de Proteção de Dados (LGPD) e diretrizes de sigilo bancário aplicados à nossa cooperativa de crédito.
          </p>
        </div>
      </div>
    </div>

    <!-- Navigation Tabs for Deep Dive -->
    <div class="flex items-center space-x-2 border-b border-slate-200 overflow-x-auto pb-px">
      <button
        @click="activeTab = 'lgpd'"
        :class="[
          'px-4 py-2.5 text-xs sm:text-sm font-bold rounded-t-xl transition-all border-b-2 -mb-px flex items-center space-x-2 whitespace-nowrap cursor-pointer',
          activeTab === 'lgpd'
            ? 'border-brand-600 text-brand-700 bg-white shadow-xs'
            : 'border-transparent text-slate-500 hover:text-slate-900',
        ]"
      >
        <FileCheck2 class="w-4 h-4" />
        <span>LGPD (Lei 13.709)</span>
      </button>

      <button
        @click="activeTab = 'sigilo'"
        :class="[
          'px-4 py-2.5 text-xs sm:text-sm font-bold rounded-t-xl transition-all border-b-2 -mb-px flex items-center space-x-2 whitespace-nowrap cursor-pointer',
          activeTab === 'sigilo'
            ? 'border-brand-600 text-brand-700 bg-white shadow-xs'
            : 'border-transparent text-slate-500 hover:text-slate-900',
        ]"
      >
        <EyeOff class="w-4 h-4" />
        <span>Sigilo Bancário (LC 105)</span>
      </button>

      <button
        @click="activeTab = 'cyber'"
        :class="[
          'px-4 py-2.5 text-xs sm:text-sm font-bold rounded-t-xl transition-all border-b-2 -mb-px flex items-center space-x-2 whitespace-nowrap cursor-pointer',
          activeTab === 'cyber'
            ? 'border-brand-600 text-brand-700 bg-white shadow-xs'
            : 'border-transparent text-slate-500 hover:text-slate-900',
        ]"
      >
        <KeyRound class="w-4 h-4" />
        <span>Cibersegurança (CMN 4.893)</span>
      </button>

      <button
        @click="activeTab = 'pld'"
        :class="[
          'px-4 py-2.5 text-xs sm:text-sm font-bold rounded-t-xl transition-all border-b-2 -mb-px flex items-center space-x-2 whitespace-nowrap cursor-pointer',
          activeTab === 'pld'
            ? 'border-brand-600 text-brand-700 bg-white shadow-xs'
            : 'border-transparent text-slate-500 hover:text-slate-900',
        ]"
      >
        <BadgeAlert class="w-4 h-4" />
        <span>PLD/FT & COAF</span>
      </button>
    </div>

    <!-- TAB 1: LGPD -->
    <section v-if="activeTab === 'lgpd'" class="space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-slate-200/80 shadow-xs space-y-4">
        <div class="flex items-center space-x-3 text-brand-700">
          <div class="w-10 h-10 rounded-xl bg-brand-50 flex items-center justify-center">
            <FileCheck2 class="w-5 h-5" />
          </div>
          <div>
            <h2 class="text-lg font-bold text-slate-900">LGPD no Cooperativismo de Crédito</h2>
            <p class="text-xs text-slate-500">Conformidade estrita com a Lei Federal nº 13.709/2018</p>
          </div>
        </div>

        <p class="text-sm text-slate-600 leading-relaxed">
          O cooperado confia a nós não apenas seus recursos financeiros, mas seus dados cadastrais, familiares e patrimoniais. O tratamento de dados pessoais no ecossistema cooperativo é fundamentado primordialmente no <strong>cumprimento de obrigação legal ou regulatória</strong> (Banco Central e COAF), na <strong>execução de contratos</strong> (empréstimos, cotas, contas correntes) e no <strong>legítimo interesse</strong>.
        </p>

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
          <div class="p-4 rounded-2xl bg-slate-50 border border-slate-200/60">
            <h4 class="text-xs font-bold text-slate-800 uppercase tracking-wide">Finalidade Específica</h4>
            <p class="text-xs text-slate-500 mt-1">Coletamos apenas dados estritamente necessários para a prestação dos serviços financeiros homologados.</p>
          </div>
          <div class="p-4 rounded-2xl bg-slate-50 border border-slate-200/60">
            <h4 class="text-xs font-bold text-slate-800 uppercase tracking-wide">Direito dos Titulares</h4>
            <p class="text-xs text-slate-500 mt-1">Cooperados podem requisitar confirmação, acesso, correção ou eliminação de dados via Portal de Privacidade.</p>
          </div>
          <div class="p-4 rounded-2xl bg-slate-50 border border-slate-200/60">
            <h4 class="text-xs font-bold text-slate-800 uppercase tracking-wide">Descarte Seguro</h4>
            <p class="text-xs text-slate-500 mt-1">Expirado o prazo prescricional regulatório do BACEN, registros são anonimizados ou descartados de forma rastreável.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- TAB 2: SIGILO BANCÁRIO -->
    <section v-if="activeTab === 'sigilo'" class="space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-slate-200/80 shadow-xs space-y-4">
        <div class="flex items-center space-x-3 text-brand-700">
          <div class="w-10 h-10 rounded-xl bg-brand-50 flex items-center justify-center">
            <EyeOff class="w-5 h-5" />
          </div>
          <div>
            <h2 class="text-lg font-bold text-slate-900">Dever de Sigilo Bancário & Cooperativo</h2>
            <p class="text-xs text-slate-500">Regulado pela Lei Complementar nº 105/2001</p>
          </div>
        </div>

        <p class="text-sm text-slate-600 leading-relaxed">
          Todas as informações sobre saldos, movimentações financeiras, investimentos, limites de crédito e garantias de nossos associados estão protegidas por sigilo bancário inquebrantável. É terminantemente proibido:
        </p>

        <ul class="space-y-2 text-xs sm:text-sm text-slate-700">
          <li class="flex items-start space-x-2">
            <AlertTriangle class="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
            <span>Consultar extratos ou dados de familiares, amigos ou pessoas públicas sem justificativa de negócio formal e atribuída.</span>
          </li>
          <li class="flex items-start space-x-2">
            <AlertTriangle class="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
            <span>Comentar operações financeiras de cooperados fora do ambiente profissional ou em redes sociais.</span>
          </li>
          <li class="flex items-start space-x-2">
            <AlertTriangle class="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
            <span>Compartilhar relatórios com terceiros sem ordem judicial ou autorização formal de órgãos competentes.</span>
          </li>
        </ul>

        <div class="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-xs leading-relaxed">
          <strong>Atenção:</strong> A quebra indevida de sigilo bancário constitui crime tipificado com pena de reclusão de 1 a 4 anos e multa, além de demissão por justa causa.
        </div>
      </div>
    </section>

    <!-- TAB 3: CIBERSEGURANÇA -->
    <section v-if="activeTab === 'cyber'" class="space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-slate-200/80 shadow-xs space-y-4">
        <div class="flex items-center space-x-3 text-indigo-700">
          <div class="w-10 h-10 rounded-xl bg-indigo-50 flex items-center justify-center">
            <KeyRound class="w-5 h-5" />
          </div>
          <div>
            <h2 class="text-lg font-bold text-slate-900">Segurança Cibernética & Resolução CMN 4.893</h2>
            <p class="text-xs text-slate-500">Mecanismos de Defesa Ativa e Boas Práticas Digitais</p>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="p-5 rounded-2xl bg-slate-50 border border-slate-200/70 space-y-2">
            <div class="flex items-center space-x-2 text-indigo-600 font-bold text-xs uppercase">
              <Lock class="w-4 h-4" />
              <span>Autenticação Forte & MFA</span>
            </div>
            <p class="text-xs text-slate-600">
              O acesso aos sistemas da cooperativa (Keycloak SSO) exige senhas complexas e verificação em duas etapas. Nunca compartilhe sua credencial.
            </p>
          </div>

          <div class="p-5 rounded-2xl bg-slate-50 border border-slate-200/70 space-y-2">
            <div class="flex items-center space-x-2 text-indigo-600 font-bold text-xs uppercase">
              <Cpu class="w-4 h-4" />
              <span>Mesa Limpa & Tela Bloqueada</span>
            </div>
            <p class="text-xs text-slate-600">
              Ao se ausentar da estação de trabalho por qualquer instante, pressione <kbd class="px-1 py-0.5 bg-white border rounded text-[10px]">Win + L</kbd>. Não deixe documentos físicos impressos sobre as mesas.
            </p>
          </div>

          <div class="p-5 rounded-2xl bg-slate-50 border border-slate-200/70 space-y-2">
            <div class="flex items-center space-x-2 text-indigo-600 font-bold text-xs uppercase">
              <AlertTriangle class="w-4 h-4" />
              <span>Alerta de Phishing</span>
            </div>
            <p class="text-xs text-slate-600">
              Desconfie de links suspeitos, arquivos executáveis anexos ou solicitações urgentes de dados bancários por e-mail ou WhatsApp. Notifique imediatamente o SOC/SegInfo.
            </p>
          </div>

          <div class="p-5 rounded-2xl bg-slate-50 border border-slate-200/70 space-y-2">
            <div class="flex items-center space-x-2 text-indigo-600 font-bold text-xs uppercase">
              <UserCheck class="w-4 h-4" />
              <span>Princípio do Privilégio Mínimo (RBAC)</span>
            </div>
            <p class="text-xs text-slate-600">
              Cada perfil tem acesso estritamente aos módulos necessários para a sua função operacional. Solicitações de acessos adicionais passam por aprovação da gerência.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- TAB 4: PLD/FT -->
    <section v-if="activeTab === 'pld'" class="space-y-6">
      <div class="bg-white p-8 rounded-3xl border border-slate-200/80 shadow-xs space-y-4">
        <div class="flex items-center space-x-3 text-amber-700">
          <div class="w-10 h-10 rounded-xl bg-amber-50 flex items-center justify-center">
            <BadgeAlert class="w-5 h-5" />
          </div>
          <div>
            <h2 class="text-lg font-bold text-slate-900">Prevenção à Lavagem de Dinheiro (PLD/FT)</h2>
            <p class="text-xs text-slate-500">Circular BCB nº 3.978 e Lei nº 9.613/1998</p>
          </div>
        </div>

        <p class="text-sm text-slate-600 leading-relaxed">
          Nossa cooperativa possui rígidos controles para evitar que seu ecossistema financeiro seja utilizado para práticas ilegais, como ocultação de bens ou financiamento ao terrorismo:
        </p>

        <div class="space-y-3">
          <div class="p-4 rounded-2xl bg-amber-50/50 border border-amber-200/60">
            <h4 class="text-xs font-bold text-amber-900">Conheça seu Cliente / Cooperado (KYC)</h4>
            <p class="text-xs text-slate-600 mt-1">Identificação e qualificação cadastral contínua dos associados, conferindo compatibilidade entre renda declarada e movimentação financeira.</p>
          </div>

          <div class="p-4 rounded-2xl bg-amber-50/50 border border-amber-200/60">
            <h4 class="text-xs font-bold text-amber-900">Comunicação de Operações Atípicas ao COAF</h4>
            <p class="text-xs text-slate-600 mt-1">Depósitos em espécie fracionados ou operações desproporcionais sem fundamento econômico são reportados de maneira sigilosa e sem ciência do cooperado.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Encarregado de Dados (DPO) -->
    <div class="bg-slate-900 text-white rounded-3xl p-8 shadow-xl flex flex-col md:flex-row items-center justify-between gap-6 border border-slate-800">
      <div class="space-y-2">
        <span class="text-[10px] font-bold uppercase tracking-widest text-brand-400">Canal Oficial de Privacidade</span>
        <h3 class="text-lg font-bold">Encarregado pelo Tratamento de Dados Pessoais (DPO)</h3>
        <p class="text-xs text-slate-300 max-w-xl">
          Para dúvidas sobre o tratamento de dados pessoais ou exercer os direitos previstos na LGPD, entre em contato direto com a equipe do DPO.
        </p>
      </div>

      <div class="shrink-0 flex items-center gap-3">
        <a
          href="mailto:dpo@coop.local"
          class="inline-flex items-center space-x-2 px-5 py-3 rounded-2xl text-xs sm:text-sm font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-md transition-colors"
        >
          <Mail class="w-4 h-4" />
          <span>dpo@coop.local</span>
        </a>
      </div>
    </div>
  </div>
</template>
