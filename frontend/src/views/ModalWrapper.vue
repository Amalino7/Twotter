<script setup lang="ts">
import { useUIStore } from '@/stores/ui.ts';
import ShareModal from '@/components/PostComponents/ShareModal.vue';

const ui = useUIStore();
</script>

<template>
  <ShareModal />
</template>

<style scoped></style>
