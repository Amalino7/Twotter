<script setup lang="ts">
// This could be an environment variable
import { apiURL } from '@/stores/auth.ts';

const backendLoginUrl = `${apiURL}login`;
</script>

<template>
  <div class="flex flex-col items-center justify-center">
    <h1 class="text-2xl font-bold mb-4">Welcome</h1>
    <p class="mb-6">Please log in to continue.</p>
    <a
      :href="backendLoginUrl"
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
    >
      Login with OAuth
    </a>
  </div>
</template>
