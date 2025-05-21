<script lang="ts" setup>
import { ref } from 'vue'
import LikeIcon from './icons/LikeIcon.vue'

const isHearted = ref(false)
const isAnimating = ref(false)
const justUnhearted = ref(false)

const count = defineModel<number>({required:true})

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
    :class="[
      'flex items-center transition-all duration-200',
      isHearted
        ? 'text-pink-400'
        : justUnhearted
          ? 'text-gray-400'
          : 'text-gray-400 hover:text-pink-400',
    ]"
    @click="HeartButton"
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
</style>
