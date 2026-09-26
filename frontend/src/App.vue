<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/layout/AppHeader.vue';
import AiTutorDrawer from '@/components/ai/AiTutorDrawer.vue';
import { useAiTutorStore } from '@/stores/aiTutor';
import { Sparkles, HelpCircle } from 'lucide-vue-next';

const route = useRoute();
const aiTutorStore = useAiTutorStore();

const isLoginPage = computed(() => route.path === '/login');
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans selection:bg-brand-500 selection:text-white">
    <!-- Header (Oculto na tela de login) -->
    <AppHeader v-if="!isLoginPage" />

    <!-- Main Content Container (Largura expandida com margens reduzidas) -->
    <div :class="['flex-1 w-full mx-auto', isLoginPage ? 'p-0 max-w-none' : 'max-w-[1780px] px-3 sm:px-5 lg:px-8 xl:px-10 pt-6 pb-12']">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- Corporate Footer (Oculto na tela de login) -->
    <footer v-if="!isLoginPage" class="mt-auto border-t border-slate-200/80 bg-white py-6 text-xs text-slate-500">
      <div class="max-w-[1780px] mx-auto px-3 sm:px-5 lg:px-8 xl:px-10 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex items-center space-x-2.5">
          <div class="w-6 h-6 rounded-lg bg-gradient-to-br from-brand-600 to-brand-800 flex items-center justify-center text-white shadow-xs shrink-0">
            <svg class="w-3.5 h-3.5 text-brand-100" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <span class="font-bold text-slate-800 tracking-tight">coop<span class="text-brand-600">.onboarding</span></span>
          <span class="text-slate-300">·</span>
          <span>&copy; 2026 Cooperativa de Crédito Integrada. Todos os direitos reservados.</span>
        </div>
        <div class="flex flex-wrap items-center gap-4 sm:gap-6">
          <router-link to="/codigo-conduta" class="hover:text-brand-600 transition-colors">Código de Conduta</router-link>
          <router-link to="/seguranca-privacidade" class="hover:text-brand-600 transition-colors">Segurança & Privacidade</router-link>
          <router-link to="/suporte" class="hover:text-brand-600 transition-colors">Suporte ao Colaborador</router-link>
        </div>
      </div>
    </footer>

    <!-- Floating AI Tutor Trigger (Fixed Bottom-Right no canto inferior) -->
    <div
      v-if="!isLoginPage"
      class="fixed bottom-6 right-6 z-30"
    >
      <button
        @click="aiTutorStore.openDrawer"
        class="group relative flex items-center space-x-2.5 px-4 py-3 rounded-2xl bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-xl shadow-ai-500/35 hover:scale-105 active:scale-95 transition-all duration-300 border border-white/20 cursor-pointer"
        title="Dúvidas sobre o conteúdo? Fale com o Tutor Virtual de IA"
      >
        <span class="relative flex h-2.5 w-2.5">
          <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-white opacity-75"></span>
          <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-emerald-400"></span>
        </span>
        <Sparkles class="w-5 h-5 text-indigo-100 group-hover:rotate-12 transition-transform duration-300" />
        <span class="text-xs font-bold tracking-wide">Tutor IA</span>
      </button>
    </div>

    <!-- AI Tutor Drawer -->
    <AiTutorDrawer v-if="!isLoginPage" />
  </div>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
