<template>
  <nav class="flex flex-col flex-1" v-if="!isMobile">
    <router-link to="/home" class="nav-item">🏠 Home</router-link>
    <router-link to="/new" class="nav-item">➕ Post</router-link>
    <router-link :to="`/user/${username}`" class="nav-item">👤 Profile</router-link>
  </nav>
  <nav class="flex flex-row justify-around" v-if="isMobile">
    <router-link to="/home" class="nav-item">🏠 Home</router-link>
    <router-link to="/new" class="nav-item">➕ Post</router-link>
    <router-link :to="`/user/${username}`" class="nav-item">👤 Profile</router-link>
  </nav>
  <!-- <nav class="bg-green-200 p-l flex justify-around items-center">
    <a class="h-10">Bich</a>
    <a class="h-10">Home</a>
  </nav> -->
</template>
<script lang="ts" setup>
const username = 'me'
const props = defineProps({
  isMobile: { Boolean, required: true },
})
</script>

<style scoped>
nav {
  /* width: 100%; */
  font-size: 12px;
  text-align: center;
  /* margin-top: 2rem; */
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  flex: 1;
  /* padding: 0 1rem; */
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}
</style>
