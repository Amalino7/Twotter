<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import Post from '../components/PostComponents/Post.vue';
import { useAuthStore } from '@/stores/auth';
import { type PostResponse } from '@/types/dtos.ts';
import { api } from '@/utils/api.ts';

const posts = ref<PostResponse[]>([]);
const authStore = useAuthStore();

/**
 * Fetches the user's post feed from the API.
 */
async function fetchPosts() {
  posts.value = await api.get<PostResponse[]>('/feed');
}

// Fetch the posts when the component is mounted
onMounted(fetchPosts);
</script>

<template>
  <main class="flex flex-col m-6 items-center">
    <div v-if="posts.length > 0">
      <Post
        v-for="post in posts"
        :key="post.id"
        :comments-count="post.commentsCount"
        :has-liked="post.hasLiked"
        :image-url="post.imageUrl"
        :likes-count="post.likesCount"
        :reposts-count="post.repostsCount"
        :text="post.content"
        :timestamp="new Date(post.createdAt)"
        :user-handle="post.userHandle"
        :user-id="post.userId"
        :username="post.userDisplayName"
      >
      </Post>
    </div>
    <div v-else>
      <p>Loading posts or no posts to display.</p>
    </div>
  </main>
</template>
