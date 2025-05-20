<script setup>
import { computed } from 'vue'
import { useWindowSize } from '@/composables/useWindowSize'
import Navigation from '../components/Navigation.vue'

const { width } = useWindowSize()
const isMobile = computed(() => width.value < 768)
</script>

<template>
  <!-- <MobileLayout v-if="isMobile" />
    <DesktopLayout v-else /> -->
  <div class="flex flex-row h-screen v-screen" v-if="!isMobile">
    <Navigation :is-mobile="isMobile"></Navigation>
    <div class="flex-3 bg-green-600">
      <RouterView></RouterView>
    </div>
    <div class="flex-1 bg-cyan-500"></div>
  </div>
  <div class="h-screen v-screen flex flex-col" v-else="isMobile">
    <div class="flex-1 bg-cyan-500">
      <RouterView></RouterView>
    </div>
    <Navigation :is-mobile="isMobile"></Navigation>
  </div>
</template>
