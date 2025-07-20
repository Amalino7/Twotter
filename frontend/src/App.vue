<script lang="ts" setup>
import LayoutWrapper from './views/LayoutWrapper.vue';
import Toaster from '@/components/Utility/Toaster.vue';
import ModalWrapper from '@/views/ModalWrapper.vue';

import { useUIStore } from '@/stores/ui.ts';

const ui = useUIStore();
</script>

<template>
  <LayoutWrapper></LayoutWrapper>
  <button @click="ui.triggerToast('Hahhash fdfgfgddfgfgd', 'info')">dsd</button>
  <Toaster />
  <ModalWrapper />
</template>

<style scoped></style>
