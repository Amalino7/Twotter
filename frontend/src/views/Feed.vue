<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Post from '../components/PostComponents/Post.vue';
import { apiURL, useAuthStore } from '@/stores/auth';
import { PostResponse } from '@/types/dtos.ts';

const posts = ref<PostResponse[]>([]);
const authStore = useAuthStore();

/**
 * Fetches the user's post feed from the API.
 */
async function fetchPosts() {
  if (!authStore.accessToken) {
    console.error('Authentication token not found.');
    return;
  }

  try {
    const response = await fetch(`${apiURL}users/${authStore.user?.id}/feed`, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      if (response.status === 401) {
        console.error('Unauthorized: User ID does not match or token is invalid.');
        // Handle unauthorized access, e.g., by logging the user out
        authStore.logout();
      } else {
        throw new Error(`Failed to fetch posts with status: ${response.status}`);
      }
      return;
    }

    posts.value = await response.json();
  } catch (error) {
    console.error('An error occurred while fetching the post feed:', error);
  }
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
        :user-id="post.userId"
        :text="post.content"
        :username="post.userDisplayName"
        :user-handle="post.userHandle"
        :timestamp="new Date(post.createdAt)"
        :image-url="post.imageUrl"
        :likes-count="post.likesCount"
        :comments-count="post.commentsCount"
        :reposts-count="post.repostsCount"
        :has-liked="post.hasLiked"
      >
      </Post>
    </div>
    <div v-else>
      <p>Loading posts or no posts to display.</p>
    </div>
  </main>
</template>
