<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
  Compass,
} from 'lucide-vue-next';
import OnboardingTutorialModal from '@/components/common/OnboardingTutorialModal.vue';

const router = useRouter();
const authStore = useAuthStore();
const aiTutorStore = useAiTutorStore();

const isProfileMenuOpen = ref(false);
const isTutorialOpen = ref(false);

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

onMounted(async () => {
  if (authStore.isAuthenticated) {
    await authStore.fetchCurrentUser();
  }
});
</script>

<template>
  <header class="sticky top-0 z-30 bg-white/95 backdrop-blur-md border-b border-slate-200/80 shadow-xs">
    <div class="w-full px-4 sm:px-6 lg:px-8 xl:px-12">
      <div class="flex items-center justify-between h-20">
        <!-- Start: Logo and Nav Grouped Together from Start -->
        <div class="flex items-center space-x-6 sm:space-x-8 lg:space-x-10 shrink-0">
          <!-- Logo / Brand -->
          <div class="flex items-center space-x-3 cursor-pointer select-none shrink-0" @click="navigateToHome">
            <div class="w-11 h-11 rounded-2xl bg-gradient-to-br from-brand-600 to-brand-800 flex items-center justify-center text-white shadow-md shadow-brand-500/20 ring-2 ring-brand-400/30 shrink-0">
              <svg class="w-6 h-6 text-brand-100" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div>
              <div class="flex items-center space-x-1.5 whitespace-nowrap">
                <span class="text-xl font-extrabold tracking-tight text-slate-900 font-sans">
                  coop<span class="text-brand-600">.onboarding</span>
                </span>
                <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-bold tracking-wide uppercase bg-brand-50 text-brand-700 border border-brand-200">
                  Cooperativo
                </span>
              </div>
              <p class="text-[11px] text-slate-500 hidden sm:block whitespace-nowrap">Plataforma de Capacitação & Integração Inteligente</p>
            </div>
          </div>

          <!-- Navigation items (aligned from start after brand) -->
          <nav class="hidden md:flex items-center space-x-2 lg:space-x-3 whitespace-nowrap">
            <router-link
              to="/"
              class="px-3.5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center space-x-2 text-slate-600 hover:text-brand-700 hover:bg-brand-50/80"
              active-class="!text-brand-700 !bg-brand-50 shadow-2xs font-bold"
            >
              <LayoutDashboard class="w-4 h-4" />
              <span>Painel Geral</span>
            </router-link>

            <router-link
              v-if="authStore.isGestor || authStore.isAdmin"
              to="/gestao"
              class="px-3.5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center space-x-2 text-slate-600 hover:text-brand-700 hover:bg-brand-50/80"
              active-class="!text-brand-700 !bg-brand-50 shadow-2xs font-bold"
            >
              <Users class="w-4 h-4" />
              <span>Gestão de Turma</span>
            </router-link>

            <router-link
              to="/trilhas"
              class="px-3.5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center space-x-2 text-slate-600 hover:text-brand-700 hover:bg-brand-50/80"
              active-class="!text-brand-700 !bg-brand-50 shadow-2xs font-bold"
            >
              <BookOpen class="w-4 h-4" />
              <span>{{ (authStore.isGestor || authStore.isAdmin) ? 'Catálogo de Trilhas' : 'Trilhas' }}</span>
            </router-link>

            <router-link
              to="/quizzes"
              class="px-3.5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center space-x-2 text-slate-600 hover:text-brand-700 hover:bg-brand-50/80"
              active-class="!text-brand-700 !bg-brand-50 shadow-2xs font-bold"
            >
              <HelpCircle class="w-4 h-4" />
              <span>{{ (authStore.isGestor || authStore.isAdmin) ? 'Banco de Quizzes' : 'Quizzes' }}</span>
            </router-link>

            <!-- Guia Rápido (Exclusivo para Colaboradores) -->
            <button
              v-if="authStore.isColaborador"
              @click="isTutorialOpen = true"
              class="px-3.5 py-2.5 rounded-xl text-sm font-semibold transition-all flex items-center space-x-2 text-indigo-700 bg-indigo-50/80 hover:bg-indigo-100 border border-indigo-200/60 cursor-pointer whitespace-nowrap shadow-2xs"
              title="Abrir o Guia Rápido da Plataforma"
            >
              <Compass class="w-4 h-4 text-indigo-600" />
              <span>Guia Rápido</span>
            </button>
          </nav>
        </div>

        <!-- Right Side: User Switcher (Tutor IA removed from topbar, fixed in bottom-right) -->
        <div class="flex items-center space-x-3 shrink-0">
          <!-- User Role & Switcher Menu -->
          <div class="relative">
            <button
              @click="isProfileMenuOpen = !isProfileMenuOpen"
              class="flex items-center space-x-2.5 p-1.5 sm:px-3 sm:py-1.5 rounded-xl border border-slate-200/80 hover:bg-slate-50 transition-colors whitespace-nowrap"
            >
              <img
                :src="authStore.currentUser.avatarUrl"
                :alt="authStore.currentUser.name"
                class="w-8 h-8 rounded-full object-cover border-2 border-brand-500/30 shrink-0"
              />
              <div class="text-left hidden lg:block">
                <div class="text-xs font-bold text-slate-800 leading-tight flex items-center space-x-1.5">
                  <span class="truncate max-w-[140px] xl:max-w-[180px]">{{ authStore.currentUser.name }}</span>
                </div>
                <div class="flex items-center space-x-1.5 mt-0.5">
                  <span
                    :class="[
                      'inline-block px-1.5 py-0.2 rounded text-[10px] font-semibold border shrink-0',
                      authStore.roleBadge.bg,
                    ]"
                  >
                    {{ authStore.roleBadge.label }}
                  </span>
                  <span class="text-[10px] text-slate-400 truncate max-w-[120px]">· {{ authStore.currentUser.department }}</span>
                </div>
              </div>
              <ChevronDown class="w-4 h-4 text-slate-400 shrink-0" />
            </button>

            <!-- Dropdown Menu do Usuário Autenticado -->
            <div
              v-if="isProfileMenuOpen"
              class="absolute right-0 mt-2 w-72 bg-white rounded-2xl shadow-xl border border-slate-200 py-2 z-50 animate-fade-in"
            >
              <div class="px-4 py-3 border-b border-slate-100 flex items-center space-x-3">
                <img
                  :src="authStore.currentUser.avatarUrl"
                  :alt="authStore.currentUser.name"
                  class="w-10 h-10 rounded-full object-cover border border-slate-200 shrink-0"
                />
                <div class="min-w-0 flex-1">
                  <p class="text-xs font-bold text-slate-900 truncate">{{ authStore.currentUser.name }}</p>
                  <p class="text-[11px] text-slate-500 truncate">{{ authStore.currentUser.email }}</p>
                  <span
                    :class="[
                      'inline-block mt-1 px-1.5 py-0.2 rounded text-[9px] font-bold border uppercase',
                      authStore.roleBadge.bg,
                    ]"
                  >
                    {{ authStore.roleBadge.label }}
                  </span>
                </div>
              </div>

              <!-- Links rápidos de Ação -->
              <div class="px-2 pt-2 space-y-1">
                <button
                  @click="navigateToProfile"
                  class="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors cursor-pointer"
                >
                  <User class="w-4 h-4 text-slate-500" />
                  <span>Meu Perfil</span>
                </button>
                <router-link
                  to="/suporte"
                  @click="isProfileMenuOpen = false"
                  class="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors cursor-pointer"
                >
                  <HelpCircle class="w-4 h-4 text-slate-500" />
                  <span>Suporte & Ajuda</span>
                </router-link>
                <button
                  @click="handleLogout"
                  class="w-full flex items-center space-x-2.5 px-3 py-2 rounded-xl text-xs font-semibold text-rose-600 hover:bg-rose-50 transition-colors cursor-pointer border-t border-slate-100 mt-1 pt-2"
                >
                  <LogOut class="w-4 h-4 text-rose-500" />
                  <span>Encerrar Sessão</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Guia Rápido Onboarding (Apenas para Colaborador) -->
    <OnboardingTutorialModal
      v-if="authStore.isColaborador"
      :is-open="isTutorialOpen"
      @close="isTutorialOpen = false"
    />
  </header>
</template>
