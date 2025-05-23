<template>
  <template v-for="(segment, index) in parsedContent">
    <router-link v-if="segment.type === 'hashtag'" :to="`/hashtag/${segment.value}/`">
      #{{ segment.value }}
    </router-link>
    <template v-else>
      {{ segment.value }}
    </template>
  </template>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps(['text']);
const parsedContent = computed(() => {
  const words = props.text.split(/(\s+)/);
  return words.map((word) => {
    if (word.startsWith('#')) {
      return { type: 'hashtag', value: word.slice(1) };
    } else {
      return { type: 'text', value: word };
    }
  });
});
</script>
