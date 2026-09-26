<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore, mockProfiles, profileCredentials } from '@/stores/auth';
import {
  Lock,
  User,
  Sparkles,
  ShieldCheck,
  AlertCircle,
  ArrowRight,
  CheckCircle2,
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('colaborador');
const password = ref('colab123');
const isLoading = ref(false);
const errorMessage = ref('');

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMessage.value = 'Preencha o usuário e a senha.';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  const success = await authStore.login(username.value, password.value);
  isLoading.value = false;

  if (success) {
    router.push('/');
  } else {
    errorMessage.value = 'Credenciais inválidas ou Keycloak inacessível. Verifique usuário e senha.';
  }
}

async function quickLogin(userKey: string) {
  const cred = profileCredentials[userKey];
  if (!cred) return;

  username.value = cred.user;
  password.value = cred.pass;
  await handleLogin();
}
</script>

<template>
  <div class="min-h-screen flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-slate-50 via-slate-100 to-slate-200/50">
    <div class="sm:mx-auto sm:w-full sm:max-w-md text-center">
      <!-- Brand Logo -->
      <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br from-brand-600 to-brand-800 text-white shadow-lg shadow-brand-500/20 mb-4 ring-4 ring-brand-100">
        <svg class="w-8 h-8 text-brand-100" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
      </div>

      <h2 class="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight font-sans">
        Portal de Integração Cooperativa
      </h2>
      <p class="mt-2 text-xs sm:text-sm text-slate-600">
        Autenticação corporativa unificada via <strong class="text-brand-700">Keycloak IAM (OAuth2 / RBAC)</strong>
      </p>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-xl">
      <div class="bg-white py-8 px-6 shadow-xl shadow-slate-200/60 rounded-3xl border border-slate-200/80 sm:px-10">
        
        <!-- Error Alert -->
        <div
          v-if="errorMessage"
          class="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs sm:text-sm flex items-start space-x-2 animate-fade-in"
        >
          <AlertCircle class="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
          <span>{{ errorMessage }}</span>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label for="username" class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Usuário Keycloak
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <User class="w-4 h-4" />
              </div>
              <input
                id="username"
                v-model="username"
                type="text"
                required
                autocomplete="username"
                placeholder="Ex: colaborador, gestor, admin, carlos, fernanda"
                class="block w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm text-slate-900 placeholder:text-slate-400 bg-slate-50/50 focus:bg-white transition-colors"
              />
            </div>
          </div>

          <div>
            <label for="password" class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Senha
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <Lock class="w-4 h-4" />
              </div>
              <input
                id="password"
                v-model="password"
                type="password"
                required
                autocomplete="current-password"
                placeholder="Digite sua senha de acesso"
                class="block w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:outline-hidden focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm text-slate-900 placeholder:text-slate-400 bg-slate-50/50 focus:bg-white transition-colors"
              />
            </div>
          </div>

          <button
            type="submit"
            :disabled="isLoading"
            class="w-full flex justify-center items-center space-x-2 py-3 px-4 border border-transparent rounded-xl shadow-md text-sm font-bold text-white bg-gradient-to-r from-brand-600 to-brand-800 hover:from-brand-700 hover:to-brand-900 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-brand-500 disabled:opacity-60 transition-all cursor-pointer"
          >
            <span v-if="isLoading" class="w-4 h-4 rounded-full border-2 border-white border-t-transparent animate-spin"></span>
            <span v-else>Entrar na Plataforma</span>
            <ArrowRight v-if="!isLoading" class="w-4 h-4" />
          </button>
        </form>

        <!-- Divider -->
        <div class="relative my-8">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-slate-200"></div>
          </div>
          <div class="relative flex justify-center text-xs uppercase tracking-wider">
            <span class="bg-white px-3 text-slate-400 font-semibold flex items-center gap-1.5">
              <Sparkles class="w-3.5 h-3.5 text-brand-600" /> Acesso Rápido para Demonstração (1 Clique)
            </span>
          </div>
        </div>

        <!-- Quick Demo Profiles Grid -->
        <div class="space-y-2.5">
          <p class="text-xs text-slate-500 text-center mb-3">
            Selecione uma persona para autenticar instantaneamente no Keycloak com as permissões reais do perfil:
          </p>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
            <!-- Ana Carolina -->
            <button
              @click="quickLogin('colaborador')"
              type="button"
              class="flex items-center space-x-3 p-3 rounded-2xl border border-slate-200 hover:border-teal-300 hover:bg-teal-50/40 text-left transition-all group"
            >
              <img :src="mockProfiles[0].avatarUrl" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
              <div class="flex-1 min-w-0">
                <p class="text-xs font-bold text-slate-800 truncate group-hover:text-teal-900">{{ mockProfiles[0].name }}</p>
                <p class="text-[11px] text-slate-500 truncate">{{ mockProfiles[0].department }}</p>
                <span class="inline-block px-1.5 py-0.2 rounded text-[9px] font-bold bg-teal-100 text-teal-800 uppercase mt-0.5">
                  Colaborador
                </span>
              </div>
            </button>

            <!-- Carlos Souza -->
            <button
              @click="quickLogin('carlos')"
              type="button"
              class="flex items-center space-x-3 p-3 rounded-2xl border border-slate-200 hover:border-teal-300 hover:bg-teal-50/40 text-left transition-all group"
            >
              <img :src="mockProfiles[1].avatarUrl" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
              <div class="flex-1 min-w-0">
                <p class="text-xs font-bold text-slate-800 truncate group-hover:text-teal-900">{{ mockProfiles[1].name }}</p>
                <p class="text-[11px] text-slate-500 truncate">{{ mockProfiles[1].department }}</p>
                <span class="inline-block px-1.5 py-0.2 rounded text-[9px] font-bold bg-teal-100 text-teal-800 uppercase mt-0.5">
                  Colaborador TI
                </span>
              </div>
            </button>

            <!-- Roberto Mendes -->
            <button
              @click="quickLogin('gestor')"
              type="button"
              class="flex items-center space-x-3 p-3 rounded-2xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/40 text-left transition-all group"
            >
              <img :src="mockProfiles[2].avatarUrl" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
              <div class="flex-1 min-w-0">
                <p class="text-xs font-bold text-slate-800 truncate group-hover:text-indigo-900">{{ mockProfiles[2].name }}</p>
                <p class="text-[11px] text-slate-500 truncate">{{ mockProfiles[2].department }}</p>
                <span class="inline-block px-1.5 py-0.2 rounded text-[9px] font-bold bg-indigo-100 text-indigo-800 uppercase mt-0.5">
                  Gestor RH
                </span>
              </div>
            </button>

            <!-- Fernanda Lima -->
            <button
              @click="quickLogin('fernanda')"
              type="button"
              class="flex items-center space-x-3 p-3 rounded-2xl border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/40 text-left transition-all group"
            >
              <img :src="mockProfiles[3].avatarUrl" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
              <div class="flex-1 min-w-0">
                <p class="text-xs font-bold text-slate-800 truncate group-hover:text-indigo-900">{{ mockProfiles[3].name }}</p>
                <p class="text-[11px] text-slate-500 truncate">{{ mockProfiles[3].department }}</p>
                <span class="inline-block px-1.5 py-0.2 rounded text-[9px] font-bold bg-indigo-100 text-indigo-800 uppercase mt-0.5">
                  Gestora Crédito
                </span>
              </div>
            </button>
          </div>

          <!-- Mariana Duarte (Admin) Full Width -->
          <button
            @click="quickLogin('admin')"
            type="button"
            class="w-full flex items-center space-x-3 p-3 rounded-2xl border border-slate-200 hover:border-rose-300 hover:bg-rose-50/40 text-left transition-all group"
          >
            <img :src="mockProfiles[4].avatarUrl" class="w-9 h-9 rounded-full object-cover border border-slate-200" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between">
                <p class="text-xs font-bold text-slate-800 truncate group-hover:text-rose-900">{{ mockProfiles[4].name }}</p>
                <span class="inline-block px-1.5 py-0.2 rounded text-[9px] font-bold bg-rose-100 text-rose-800 uppercase">
                  Administrador Master
                </span>
              </div>
              <p class="text-[11px] text-slate-500 truncate">{{ mockProfiles[4].department }}</p>
            </div>
          </button>
        </div>

        <!-- Security footer note -->
        <div class="mt-8 pt-4 border-t border-slate-100 flex items-center justify-center space-x-2 text-[11px] text-slate-400">
          <ShieldCheck class="w-4 h-4 text-emerald-600" />
          <span>Servidor de Identidade Keycloak 25 · Realm <strong>coop-onboarding</strong></span>
        </div>

      </div>
    </div>
  </div>
</template>
