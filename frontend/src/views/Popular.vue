<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { PostResponse } from '@/types/dtos.ts';
import { api } from '@/utils/api.ts';
import Post from '@/components/PostComponents/Post.vue';

const posts = ref<PostResponse[]>([]);

async function fetchPosts() {
  posts.value = (await api.get<{ items: [PostResponse] }>('/posts/popular')).items;
}

onMounted(fetchPosts);
</script>

<template>
  <div v-if="posts.length > 0">
    <main class="flex flex-col m-6 items-center flex-1">
      <Post
        v-for="post in posts"
        :key="post.id"
        :post-id="post.id"
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
    </main>
  </div>
  <div v-else>
    <p>Loading posts or no posts to display.</p>
  </div>
</template>
