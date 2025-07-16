<script lang="ts" setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useFusionAuth } from '@fusionauth/vue-sdk';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

onMounted(async () => {
  console.log('route', route, authStore.accessToken, authStore.authenticated);
});
</script>

<template>
  <div class="flex items-center justify-center min-h-screen">
    <p class="text-lg">Logging you in, please wait...</p>
  </div>
</template>
