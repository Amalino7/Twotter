<template>
  <nav class="flex flex-col flex-1 left" v-if="!isMobile">
    <router-link key="/home" to="/home" class="">🏠 Home</router-link>
    <router-link key="/new" to="/new" class="">➕ Post</router-link>
    <router-link key="/user" :to="`/user/${username}`" class="">👤 Profile</router-link>
    <!-- <div class="h-4"></div> -->
    <hr class="divide-solid rounded-3xl border-b-4 w-1/3 ml-4 mt-2" />
  </nav>
  <nav class="flex flex-row justify-around" v-if="isMobile">
    <router-link to="/home" class="flex-1 text-center">🏠 Home</router-link>
    <router-link to="/new" class="flex-1 text-center">➕ Post</router-link>
    <router-link :to="`/user/${username}`" class="flex-1 text-center">👤 Profile</router-link>
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
  font-size: 18px;
  /* text-align: center; */
  /* margin-top: 2rem; */
}

nav a.router-link-exact-active {
  color: var(--color-text);
  /* order: calc(-infinity); */
  /* font-size: larger; */
  /* background: var(--color-background); */
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  /* flex: 1; */
  /* padding: 0 1rem; */
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}
</style>
