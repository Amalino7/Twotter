<template>
  <div
    v-if="ui.showToast"
    class="fixed bottom-4 right-4 md:right-1/3 bg-gradient-to-r from-purple-500 to-pink-500 text-white p-4 rounded shadow-lg w-64 z-10"
  >
    {{ ui.toastMessage }}
    <div class="mt-2 h-1 bg-white rounded-full overflow-hidden">
      <div
        :key="animationKey"
        :style="{ animationDuration: `${duration}ms` }"
        class="h-full bg-gray-300 rounded-full animate-progress"
      ></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useUIStore } from '@/stores/ui.ts';

const ui = useUIStore();
const animationKey = ref(0);
const duration = 3000; // milliseconds

let timeoutId: ReturnType<typeof setTimeout> | null = null;

watch(
  () => ui.toastMessage,
  (newVal) => {
    if (newVal) {
      ui.showToast = true;
      animationKey.value++;
      // Clear any existing timeout
      if (timeoutId) {
        clearTimeout(timeoutId);
      }

      // Set new timeout
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
    transform: translateX(0);
  }
  to {
    transform: translateX(100%);
  }
}

.animate-progress {
  animation-name: progress;
  animation-timing-function: linear;
  animation-fill-mode: forwards;
  animation-direction: normal;
  transform: translateX(-100%);
}
</style>
