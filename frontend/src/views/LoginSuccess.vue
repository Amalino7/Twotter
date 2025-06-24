<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

onMounted(() => {
  const accessToken = route.query.access_token as string | null;

  if (accessToken) {
    authStore.setAccessToken(accessToken);
    console.log('Successfully stored access token.');
    router.replace({ name: 'home' });
  } else {
    console.error('Login failed: No access token found in the redirect URL.');
    router.replace({ name: 'Login' });
  }
});
</script>

<template>
  <div class="flex items-center justify-center min-h-screen">
    <p class="text-lg">Logging you in, please wait...</p>
  </div>
</template>
