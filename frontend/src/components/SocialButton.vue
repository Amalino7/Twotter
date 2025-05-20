<script setup lang="ts">
import { ref } from 'vue'
import LikeIcon from './icons/LikeIcon.vue'
import { onMounted } from 'vue'
const isHearted = ref(false)
const isAnimating = ref(false)
const justUnhearted = ref(false)

const count = defineModel<number>()

function HeartButton() {
  if (isHearted.value) {
    isHearted.value = false
    count.value--
    justUnhearted.value = true
    setTimeout(() => {
      justUnhearted.value = false
    }, 1000)
  } else {
    isHearted.value = true
    count.value++
  }
  isAnimating.value = true
}
</script>

<template>
  <button
    @click="HeartButton"
    :class="[
      'flex items-center transition-all duration-200',
      isHearted
        ? 'text-pink-400'
        : justUnhearted
          ? 'text-gray-400'
          : 'text-gray-400 hover:text-pink-400',
    ]"
  >
    <LikeIcon
      :class="['transition-transform duration-300 ease-out', isAnimating ? 'animate-pop' : '']"
      @animationend="isAnimating = false"
    />
    <span class="ml-1">{{ count }}</span>
  </button>
</template>

<style scoped>
@keyframes pop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.4);
  }
  100% {
    transform: scale(1);
  }
}

.animate-pop {
  animation: pop 0.3s ease-out;
}

.notification-progress {
  width: 100%;
}

.notification-show .notification-progress {
  width: 0%;
}
</style>
