<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(
  defineProps<{
    value: number;
    max?: number;
    showLabel?: boolean;
    size?: 'sm' | 'md' | 'lg';
    variant?: 'brand' | 'coop' | 'ai' | 'success';
  }>(),
  {
    max: 100,
    showLabel: false,
    size: 'md',
    variant: 'coop',
  }
);

const percentage = computed(() => {
  const p = Math.round((props.value / props.max) * 100);
  return Math.min(Math.max(p, 0), 100);
});

const heightClass = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'h-1.5';
    case 'lg':
      return 'h-3.5';
    default:
      return 'h-2.5';
  }
});

const barGradient = computed(() => {
  switch (props.variant) {
    case 'ai':
      return 'bg-gradient-to-r from-ai-500 to-indigo-600';
    case 'success':
      return 'bg-gradient-to-r from-emerald-500 to-teal-500';
    case 'brand':
      return 'bg-gradient-to-r from-brand-500 to-brand-700';
    case 'coop':
    default:
      return 'bg-gradient-to-r from-coop-500 to-coop-700';
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="showLabel" class="flex justify-between items-center mb-1 text-xs font-semibold text-slate-600">
      <span>Progresso</span>
      <span class="text-slate-900">{{ percentage }}%</span>
    </div>
    <div :class="['w-full bg-slate-200/80 rounded-full overflow-hidden', heightClass]">
      <div
        :class="['h-full rounded-full transition-all duration-500 ease-out shadow-sm', barGradient]"
        :style="{ width: `${percentage}%` }"
        role="progressbar"
        :aria-valuenow="percentage"
        aria-valuemin="0"
        aria-valuemax="100"
      ></div>
    </div>
  </div>
</template>
