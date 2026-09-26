<script setup lang="ts">
withDefaults(
  defineProps<{
    type?: 'card' | 'text' | 'line';
    lines?: number;
  }>(),
  {
    type: 'card',
    lines: 3,
  }
);
</script>

<template>
  <div v-if="type === 'card'" class="animate-pulse bg-white border border-slate-200/80 rounded-2xl p-6 shadow-sm">
    <div class="h-4 bg-slate-200 rounded w-1/4 mb-4"></div>
    <div class="h-6 bg-slate-200 rounded w-3/4 mb-3"></div>
    <div class="h-4 bg-slate-200 rounded w-full mb-2"></div>
    <div class="h-4 bg-slate-200 rounded w-2/3 mb-6"></div>
    <div class="h-2.5 bg-slate-200 rounded-full w-full mb-4"></div>
    <div class="flex justify-between items-center">
      <div class="h-4 bg-slate-200 rounded w-20"></div>
      <div class="h-9 bg-slate-200 rounded-lg w-28"></div>
    </div>
  </div>

  <div v-else-if="type === 'text'" class="animate-pulse space-y-3">
    <div
      v-for="i in lines"
      :key="i"
      class="h-4 bg-slate-200 rounded"
      :style="{ width: i === lines ? '60%' : '100%' }"
    ></div>
  </div>

  <div v-else class="animate-pulse h-4 bg-slate-200 rounded w-full"></div>
</template>
