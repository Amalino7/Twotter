<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white">
    <div class="w-full max-w-md p-8 space-y-6 bg-gray-800 rounded-2xl shadow-2xl">
      <div class="flex justify-between">
        <button
          :class="{ 'font-bold border-b-2 border-blue-400': isLogin, 'text-gray-400': !isLogin }"
          class="px-4 py-2"
          @click="isLogin = true"
        >
          Login
        </button>
        <button
          :class="{ 'font-bold border-b-2 border-blue-400': !isLogin, 'text-gray-400': isLogin }"
          class="px-4 py-2"
          @click="isLogin = false"
        >
          Register
        </button>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium">Email</label>
            <input
              v-model="form.email"
              type="email"
              class="mt-1 block w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 text-white"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium">Password</label>
            <input
              v-model="form.password"
              type="password"
              class="mt-1 block w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 text-white"
              required
            />
          </div>

          <div v-if="!isLogin">
            <label class="block text-sm font-medium">Confirm Password</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="mt-1 block w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg focus:ring-blue-500 focus:border-blue-500 text-white"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          class="w-full mt-6 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
        >
          {{ isLogin ? 'Login' : 'Register' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const isLogin = ref(true);
const form = reactive({
  email: '',
  password: '',
  confirmPassword: '',
});

const router = useRouter();

function handleSubmit() {
  console.log('bdfbsidfuhs');
  if (!isLogin.value && form.password !== form.confirmPassword) {
    alert("Passwords don't match. You want security, not a personality test.");
    return;
  }

  console.log(`${isLogin.value ? 'Logging in' : 'Registering'} with`, form);
  router.push('/home'); // Change to your app's route
}
</script>

<style scoped>
body {
  font-family: 'Inter', sans-serif;
  background-color: #111827;
  color: white;
}
</style>
