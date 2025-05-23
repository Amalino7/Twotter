<template>
  <button @click="displayModal" class="text-gray-400 hover:text-blue-400 flex items-center">
    <ShareIcon />
  </button>
</template>

<script setup lang="ts">
import ShareIcon from '../icons/ShareIcon.vue';
import { useUIStore } from '@/stores/ui.ts';

const { url } = defineProps({ url: { type: String, required: true } });
const ui = useUIStore();
const displayModal = () => {
  console.log('displayModal');
  ui.openModal({ url: url });
};
</script>

<style></style>
