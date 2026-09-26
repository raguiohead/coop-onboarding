<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue';
import AiTutorDrawer from '@/components/ai/AiTutorDrawer.vue';
import { useAiTutorStore } from '@/stores/aiTutor';
import { Sparkles, HelpCircle } from 'lucide-vue-next';

const aiTutorStore = useAiTutorStore();
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans selection:bg-brand-500 selection:text-white">
    <!-- Header -->
    <AppHeader />

    <!-- Main Content Container -->
    <div class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 pt-8">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- Corporate Footer -->
    <footer class="mt-auto border-t border-slate-200/80 bg-white py-6 text-xs text-slate-500">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex items-center space-x-2">
          <div class="w-6 h-6 rounded-lg bg-brand-600 flex items-center justify-center text-white text-[10px] font-bold">
            COOP
          </div>
          <span>&copy; 2026 Cooperativa de Crédito Integrada. Todos os direitos reservados.</span>
        </div>
        <div class="flex flex-wrap items-center gap-4 sm:gap-6">
          <router-link to="/codigo-conduta" class="hover:text-brand-600 transition-colors">Código de Conduta</router-link>
          <router-link to="/seguranca-privacidade" class="hover:text-brand-600 transition-colors">Segurança & Privacidade</router-link>
          <router-link to="/suporte" class="hover:text-brand-600 transition-colors">Suporte ao Colaborador</router-link>
        </div>
      </div>
    </footer>

    <!-- Floating AI Tutor Trigger (Fixed Bottom-Right) -->
    <div class="fixed bottom-6 right-6 z-30">
      <button
        @click="aiTutorStore.openDrawer"
        class="group relative flex items-center space-x-2.5 px-4 py-3 rounded-2xl bg-gradient-to-r from-ai-500 to-indigo-600 hover:from-ai-600 hover:to-indigo-700 text-white shadow-xl shadow-ai-500/30 hover:scale-105 active:scale-95 transition-all duration-300 ring-4 ring-white"
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
    <AiTutorDrawer />
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
