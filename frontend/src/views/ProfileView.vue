<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
  User,
  ShieldCheck,
  Award,
  Calendar,
  Mail,
  Building,
  KeyRound,
  LogOut,
  CheckCircle2,
  Lock,
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();

function handleLogout() {
  authStore.logout();
  router.push('/login');
}
</script>

<template>
  <div class="max-w-4xl mx-auto space-y-8 pb-16">
    <!-- Profile Card Header -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs overflow-hidden">
      <!-- Gradient Cover -->
      <div class="h-32 bg-gradient-to-r from-brand-700 via-brand-800 to-indigo-900"></div>

      <div class="px-6 sm:px-8 pb-8 pt-0 relative">
        <div class="flex flex-col sm:flex-row items-center sm:items-end justify-between gap-4 -mt-14 mb-6">
          <div class="flex flex-col sm:flex-row items-center sm:items-end space-y-3 sm:space-y-0 sm:space-x-5 text-center sm:text-left">
            <img
              :src="authStore.currentUser.avatarUrl"
              :alt="authStore.currentUser.name"
              class="w-24 h-24 rounded-3xl object-cover border-4 border-white shadow-lg shadow-slate-900/10"
            />
            <div>
              <div class="flex items-center space-x-2 justify-center sm:justify-start">
                <h1 class="text-xl sm:text-2xl font-extrabold text-slate-900 font-sans">
                  {{ authStore.currentUser.name }}
                </h1>
                <span :class="['px-2 py-0.5 rounded text-[10px] font-bold border uppercase', authStore.roleBadge.bg]">
                  {{ authStore.currentUser.role }}
                </span>
              </div>
              <p class="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                {{ authStore.currentUser.department }}
              </p>
            </div>
          </div>

          <button
            @click="handleLogout"
            class="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-bold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 transition-colors cursor-pointer"
          >
            <LogOut class="w-4 h-4" />
            <span>Encerrar Sessão</span>
          </button>
        </div>

        <!-- Information Grid -->
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-6 border-t border-slate-100 text-xs">
          <div class="flex items-center space-x-3 p-3 rounded-2xl bg-slate-50/70 border border-slate-100">
            <Mail class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 font-medium">E-mail Institucional</p>
              <p class="font-bold text-slate-800 truncate">{{ authStore.currentUser.email }}</p>
            </div>
          </div>

          <div class="flex items-center space-x-3 p-3 rounded-2xl bg-slate-50/70 border border-slate-100">
            <Building class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 font-medium">Lotação / Área</p>
              <p class="font-bold text-slate-800 truncate">{{ authStore.currentUser.department }}</p>
            </div>
          </div>

          <div class="flex items-center space-x-3 p-3 rounded-2xl bg-slate-50/70 border border-slate-100">
            <Calendar class="w-4 h-4 text-slate-400 shrink-0" />
            <div class="min-w-0">
              <p class="text-[10px] text-slate-400 font-medium">Data de Admissão</p>
              <p class="font-bold text-slate-800">{{ authStore.currentUser.joinDate }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Certifications & Achievements -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs p-6 sm:p-8">
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
        <div>
          <h2 class="text-base font-bold text-slate-900 flex items-center gap-2">
            <Award class="w-5 h-5 text-brand-600" />
            <span>Badges & Certificados de Formação</span>
          </h2>
          <p class="text-xs text-slate-500">Conquistas validadas durante o seu programa de integração.</p>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div class="p-4 rounded-2xl bg-teal-50/60 border border-teal-200 flex items-start space-x-3">
          <div class="w-10 h-10 rounded-xl bg-teal-600 text-white flex items-center justify-center shrink-0 shadow-xs">
            <CheckCircle2 class="w-5 h-5" />
          </div>
          <div>
            <h3 class="text-xs font-bold text-teal-950">Princípios do Cooperativismo</h3>
            <p class="text-[11px] text-teal-800 mt-0.5">Certificado emitido após conclusão do Módulo 1 e acerto de 100% no quiz de fixação.</p>
            <span class="inline-block mt-2 text-[10px] font-bold text-teal-700 bg-teal-100 px-2 py-0.5 rounded">
              Emitido em 26/09/2026
            </span>
          </div>
        </div>

        <div class="p-4 rounded-2xl bg-indigo-50/60 border border-indigo-200 flex items-start space-x-3">
          <div class="w-10 h-10 rounded-xl bg-indigo-600 text-white flex items-center justify-center shrink-0 shadow-xs">
            <ShieldCheck class="w-5 h-5" />
          </div>
          <div>
            <h3 class="text-xs font-bold text-indigo-950">Compliance & Sigilo Bancário</h3>
            <p class="text-[11px] text-indigo-800 mt-0.5">Treinamento obrigatório de conformidade regulatória com o Banco Central e LGPD.</p>
            <span class="inline-block mt-2 text-[10px] font-bold text-indigo-700 bg-indigo-100 px-2 py-0.5 rounded">
              Em andamento
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Security & Keycloak Session -->
    <div class="bg-white rounded-3xl border border-slate-200/80 shadow-xs p-6 sm:p-8">
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-6">
        <div>
          <h2 class="text-base font-bold text-slate-900 flex items-center gap-2">
            <KeyRound class="w-5 h-5 text-indigo-600" />
            <span>Sessão & Identidade Keycloak</span>
          </h2>
          <p class="text-xs text-slate-500">Parâmetros de autenticação OpenID Connect e RBAC.</p>
        </div>
        <span class="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 flex items-center gap-1">
          <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Conectado
        </span>
      </div>

      <div class="space-y-3 text-xs text-slate-600">
        <div class="flex justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
          <span class="font-semibold text-slate-700">Servidor IAM:</span>
          <span class="font-mono text-[11px] text-slate-900">http://localhost:8180</span>
        </div>
        <div class="flex justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
          <span class="font-semibold text-slate-700">Realm Ativo:</span>
          <span class="font-mono text-[11px] text-slate-900">coop-onboarding</span>
        </div>
        <div class="flex justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
          <span class="font-semibold text-slate-700">Client ID:</span>
          <span class="font-mono text-[11px] text-slate-900">coop-frontend (Public Client)</span>
        </div>
        <div class="flex justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
          <span class="font-semibold text-slate-700">Fluxo de Autorização:</span>
          <span class="font-mono text-[11px] text-slate-900">Direct Access Grants + Refresh Token</span>
        </div>
      </div>
    </div>
  </div>
</template>
