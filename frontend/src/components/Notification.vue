<template>
  <div
    v-if="notification.show"
    class="fixed bottom-4 right-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white p-4 rounded shadow-lg w-64"
  >
    {{ notification.message }}
    <div class="mt-2 h-1 bg-white rounded-full overflow-hidden">
      <div
        class="h-full bg-gray-300 rounded-full animate-progress"
        :style="{ animationDuration: `${duration}ms` }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref } from 'vue'

const props = defineProps<{
  notification: { show: boolean; message: string }
}>()

const duration = 3000 // milliseconds

// watch(
//   () => props.notification.show,
//   (newVal) => {
//     if (newVal) {
//       setTimeout(() => {
//         props.notification.show = false
//       }, duration)
//     }
//   },
// )
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
