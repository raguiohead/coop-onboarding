<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useAiTutorStore } from '@/stores/aiTutor';
import {
  Sparkles,
  ChevronDown,
  User,
  ShieldCheck,
  Briefcase,
  BookOpen,
  LayoutDashboard,
  HelpCircle,
  Users,
  LogOut,
  LogIn,
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const aiTutorStore = useAiTutorStore();

const isProfileMenuOpen = ref(false);

async function selectProfile(profileId: string) {
  await authStore.switchProfile(profileId);
  isProfileMenuOpen.value = false;
}

function navigateToHome() {
  router.push('/');
}

function navigateToProfile() {
  isProfileMenuOpen.value = false;
  router.push('/perfil');
}

function handleLogout() {
  isProfileMenuOpen.value = false;
  authStore.logout();
  router.push('/login');
}
</script>

<template>
  <header class="sticky top-0 z-30 bg-white/90 backdrop-blur-md border-b border-slate-200/80 shadow-xs">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo / Brand -->
        <div class="flex items-center space-x-3 cursor-pointer select-none" @click="navigateToHome">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-brand-600 to-brand-800 flex items-center justify-center text-white shadow-md shadow-brand-500/20 ring-2 ring-brand-400/30">
            <svg class="w-6 h-6 text-brand-100" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div>
            <div class="flex items-center space-x-1.5">
              <span class="text-lg font-extrabold tracking-tight text-slate-900 font-sans">
                coop<span class="text-brand-600">.onboarding</span>
              </span>
              <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-bold tracking-wide uppercase bg-brand-50 text-brand-700 border border-brand-200">
                Cooperativo
              </span>
            </div>
            <p class="text-[11px] text-slate-500 hidden sm:block">Plataforma de Capacitação & Integração Inteligente</p>
          </div>
        </div>

        <!-- Navigation items -->
        <nav class="hidden md:flex items-center space-x-1">
          <router-link
            to="/"
            class="px-3 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-1.5 text-slate-600 hover:text-brand-700 hover:bg-brand-50"
            active-class="!text-brand-700 !bg-brand-50 font-semibold"
          >
            <LayoutDashboard class="w-4 h-4" />
            <span>Painel Geral</span>
          </router-link>

          <a
            href="#trilhas"
            @click.prevent="navigateToHome"
            class="px-3 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-1.5 text-slate-600 hover:text-brand-700 hover:bg-brand-50"
          >
            <BookOpen class="w-4 h-4" />
            <span>Trilhas</span>
          </a>

          <router-link
            to="/quizzes"
            class="px-3 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-1.5 text-slate-600 hover:text-brand-700 hover:bg-brand-50"
            active-class="!text-brand-700 !bg-brand-50 font-semibold"
          >
            <HelpCircle class="w-4 h-4" />
            <span>Quizzes</span>
          </router-link>

          <router-link
            v-if="authStore.isGestor || authStore.isAdmin"
            to="/gestao"
            class="px-3 py-2 rounded-lg text-sm font-medium transition-colors flex items-center space-x-1.5 text-slate-600 hover:text-brand-700 hover:bg-brand-50"
            active-class="!text-brand-700 !bg-brand-50 font-semibold"
          >
            <Users class="w-4 h-4" />
            <span>Gestão de Turma</span>
          </router-link>
        </nav>

        <!-- Right Side: Tutor AI Button + User Switcher -->
        <div class="flex items-center space-x-3">
          <!-- AI Tutor trigger button -->
          <button
            @click="aiTutorStore.openDrawer"
            class="group relative inline-flex items-center space-x-2 px-3.5 py-2 rounded-xl text-xs sm:text-sm font-semibold bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-md shadow-ai-500/20 transition-all duration-200 hover:scale-[1.02] active:scale-[0.98]"
            title="Abrir Tutor Virtual de IA"
          >
            <span class="relative flex h-2 w-2">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-white opacity-75"></span>
              <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-400"></span>
            </span>
            <Sparkles class="w-4 h-4 text-ai-100 group-hover:rotate-12 transition-transform duration-300" />
            <span class="hidden sm:inline">Tutor IA</span>
          </button>

          <!-- User Role & Switcher Menu -->
          <div class="relative">
            <button
              @click="isProfileMenuOpen = !isProfileMenuOpen"
              class="flex items-center space-x-2.5 p-1.5 sm:px-3 sm:py-1.5 rounded-xl border border-slate-200/80 hover:bg-slate-50 transition-colors"
            >
              <img
                :src="authStore.currentUser.avatarUrl"
                :alt="authStore.currentUser.name"
                class="w-8 h-8 rounded-full object-cover border-2 border-brand-500/30"
              />
              <div class="text-left hidden lg:block">
                <div class="text-xs font-bold text-slate-800 leading-tight flex items-center space-x-1.5">
                  <span>{{ authStore.currentUser.name }}</span>
                </div>
                <div class="flex items-center space-x-1.5 mt-0.5">
                  <span
                    :class="[
                      'inline-block px-1.5 py-0.2 rounded text-[10px] font-semibold border',
                      authStore.roleBadge.bg,
                    ]"
                  >
                    {{ authStore.roleBadge.label }}
                  </span>
                  <span class="text-[10px] text-slate-400">· {{ authStore.currentUser.department }}</span>
                </div>
              </div>
              <ChevronDown class="w-4 h-4 text-slate-400" />
            </button>

            <!-- Dropdown Menu for Role Switching -->
            <div
              v-if="isProfileMenuOpen"
              class="absolute right-0 mt-2 w-80 bg-white rounded-2xl shadow-xl border border-slate-200 py-2 z-50 animate-fade-in"
            >
              <div class="px-4 py-2 border-b border-slate-100">
                <div class="flex items-center justify-between">
                  <p class="text-xs font-bold text-slate-700 uppercase tracking-wider">Perfis Keycloak RBAC</p>
                  <span class="flex items-center gap-1 text-[11px] text-brand-600 font-semibold">
                    <ShieldCheck class="w-3.5 h-3.5" /> Ativo
                  </span>
                </div>
                <p class="text-[11px] text-slate-500 mt-0.5">Alterne instantaneamente para testar visões de Colaborador, Gestor e Admin.</p>
              </div>

              <!-- Lista de 5 Perfis Reais -->
              <div class="p-1 space-y-1 max-h-72 overflow-y-auto">
                <button
                  v-for="profile in authStore.mockProfiles"
                  :key="profile.id"
                  @click="selectProfile(profile.id)"
                  :class="[
                    'w-full flex items-center space-x-3 px-3 py-2 rounded-xl text-left transition-colors',
                    authStore.currentUser.id === profile.id
                      ? 'bg-brand-50/80 text-brand-900 border border-brand-200/60 font-medium'
                      : 'hover:bg-slate-50 text-slate-700',
                  ]"
                >
                  <img
                    :src="profile.avatarUrl"
                    class="w-8 h-8 rounded-full object-cover border border-slate-200"
                  />
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between">
                      <p class="text-xs font-semibold truncate">{{ profile.name }}</p>
                      <span
                        v-if="authStore.currentUser.id === profile.id"
                        class="text-[9px] font-bold text-brand-600 bg-brand-100/60 px-1 rounded"
                      >
                        ativo
                      </span>
                    </div>
                    <p class="text-[10px] text-slate-500 truncate">{{ profile.department }}</p>
                  </div>
                  <span
                    :class="[
                      'text-[9px] font-bold px-1.5 py-0.5 rounded border uppercase shrink-0',
                      profile.role === 'ADMIN'
                        ? 'bg-rose-50 text-rose-700 border-rose-200'
                        : profile.role === 'GESTOR'
                        ? 'bg-indigo-50 text-indigo-700 border-indigo-200'
                        : 'bg-teal-50 text-teal-700 border-teal-200',
                    ]"
                  >
                    {{ profile.role }}
                  </span>
                </button>
              </div>

              <!-- Links rápidos de Ação -->
              <div class="px-2 pt-2 border-t border-slate-100 space-y-1">
                <button
                  @click="navigateToProfile"
                  class="w-full flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
                >
                  <User class="w-3.5 h-3.5 text-slate-500" />
                  <span>Ver Meu Perfil Completo</span>
                </button>
                <button
                  @click="handleLogout"
                  class="w-full flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-semibold text-rose-600 hover:bg-rose-50 transition-colors"
                >
                  <LogOut class="w-3.5 h-3.5 text-rose-500" />
                  <span>Sair da Sessão (Ir para Login)</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>
