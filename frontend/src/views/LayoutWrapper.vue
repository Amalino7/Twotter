<script setup>
import { computed } from 'vue';
import { useWindowSize } from '@/composables/useWindowSize';
import Navigation from '../components/Navigation.vue';
import { useAuthStore } from '../stores/auth.js';

const auth = useAuthStore();

const { width } = useWindowSize();
const isMobile = computed(() => width.value < 768);
</script>

<template>
  <div class="flex flex-row h-screen w-screen" v-if="!isMobile">
    <Navigation :is-mobile="isMobile"></Navigation>
    <div class="flex-3 bg-gray-800 overflow-auto max-w-3xl border-l border-r border-gray-700">
      <RouterView></RouterView>
    </div>
    <div class="flex-1 bg-gray-900 border-l border-gray-700">
      <button @click="auth.logout()">Logout</button>
    </div>
  </div>
  <div class="h-screen w-screen flex flex-col" v-else>
    <div class="flex-1 bg-gray-800 overflow-auto border-b border-gray-700">
      <RouterView></RouterView>
    </div>
    <Navigation :is-mobile="isMobile"></Navigation>
  </div>
</template>
