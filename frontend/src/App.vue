<script lang="ts" setup>
import LayoutWrapper from './views/LayoutWrapper.vue';
import Toaster from '@/components/Utility/Toaster.vue';
import ModalWrapper from '@/views/ModalWrapper.vue';

import { useUIStore } from '@/stores/ui.ts';
</script>

<!-- Literally pointless-->
<template>
  <LayoutWrapper></LayoutWrapper>
  <Toaster />
  <ModalWrapper />
</template>

<style scoped></style>
