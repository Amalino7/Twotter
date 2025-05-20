<template>
  <div
    v-if="notification.show"
    class="fixed bottom-4 right-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white p-4 rounded shadow-lg"
  >
    {{ notification.message }}
    <div class="mt-2 h-1 bg-gray-600 rounded-full">
      <div class="h-full bg-white rounded-full notification-progress"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

defineProps<{
  notification: { show: boolean; message: string }
}>()

onMounted(() => {
  const progressBar = document.querySelector('.notification-progress') as HTMLElement | null
  if (progressBar) {
    progressBar.style.transition = 'width 3s linear'
  }
})
</script>
