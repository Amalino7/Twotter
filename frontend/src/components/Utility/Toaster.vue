<template>
  <div
    v-if="ui.showToast"
    @mouseenter="pauseTimer"
    @mouseleave="resumeTimer"
    @click.right.prevent="dismissToast"
    :class="[
      'fixed bottom-4 right-4 bg-gray-900  p-4 rounded-lg shadow-lg z-10 border-2 select-none',
      'max-w-xl',
      toastBgClass,
    ]"
  >
    {{ ui.toastMessage }}
    <div class="mt-4 h-1 bg-white rounded-full overflow-hidden">
      <div
        :key="animationKey"
        :style="{
          animationDuration: `${duration}ms`,
          animationPlayState: isPaused ? 'paused' : 'running',
        }"
        :class="['h-full  rounded-full animate-progress', progressColor]"
      ></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { useUIStore } from '@/stores/ui';

const ui = useUIStore();
const animationKey = ref(0);
const duration = 3000; // milliseconds
const isPaused = ref(false);
let remainingTime = duration;
let startTime: number | null = null;
const progressColor = computed(() => {
  switch (ui.toastType) {
    case 'success':
      return 'bg-cyan-600';
    case 'error':
      return 'bg-red-600';
    case 'warning':
      return 'bg-yellow-600';
    case 'info':
    default:
      return 'bg-cyan-600';
  }
});
const toastBgClass = computed(() => {
  switch (ui.toastType) {
    case 'success':
      return 'text-cyan-600 border-cyan-600';
    case 'error':
      return 'text-red-600 border-red-600';
    case 'warning':
      return 'text-yellow-600 border-yellow-600';
    case 'info':
    default:
      return 'text-cyan-600 border-cyan-600';
  }
});

let timeoutId: ReturnType<typeof setTimeout> | null = null;

const pauseTimer = () => {
  if (timeoutId) {
    clearTimeout(timeoutId);
    isPaused.value = true;
    if (startTime) {
      remainingTime -= Date.now() - startTime;
    }
  }
};

const resumeTimer = () => {
  isPaused.value = false;
  startTime = Date.now();
  if (remainingTime > 0) {
    timeoutId = setTimeout(() => {
      ui.showToast = false;
      ui.toastMessage = '';
      timeoutId = null;
    }, remainingTime);
  }
};

const dismissToast = () => {
  if (timeoutId) {
    clearTimeout(timeoutId);
  }
  ui.showToast = false;
  ui.toastMessage = '';
  timeoutId = null;
  isPaused.value = false;
};

watch(
  () => ui.toastMessage,
  (newVal) => {
    if (newVal) {
      ui.showToast = true;
      animationKey.value++;
      remainingTime = duration;
      startTime = Date.now();

      if (timeoutId) {
        clearTimeout(timeoutId);
      }

      timeoutId = setTimeout(() => {
        ui.showToast = false;
        ui.toastMessage = '';
        timeoutId = null;
      }, duration);
    }
  },
);
</script>

<style scoped>
@keyframes progress {
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0);
  }
}

.animate-progress {
  animation-name: progress;
  animation-timing-function: linear;
  animation-fill-mode: forwards;
  animation-direction: normal;
}
</style>
