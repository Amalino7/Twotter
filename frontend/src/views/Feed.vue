<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Post from '../components/PostComponents/Post.vue';
import { useAuthStore } from '@/stores/auth'; // Adjust the path to your pinia store

// Define the structure of a post object based on your API response
interface PostResponse {
  text: string;
  user: {
    username: string; // Assuming the user object has a username
  };
  url?: string;
  createdAt: string; // Or Date, depending on your API
}

const posts = ref<PostResponse[]>([]);
const authStore = useAuthStore();

/**
 * Fetches the user's post feed from the API.
 */
async function fetchPosts() {
  // In a real app, you'd get the user ID from the store or route params
  const userId = '123';

  if (!authStore.accessToken) {
    console.error('Authentication token not found.');
    // You might want to redirect to a login page here
    return;
  }

  try {
    const response = await fetch(`/api/users/${userId}/feed`, { // Assuming a proxy is set up for /api
      headers: {
        'Authorization': `Bearer ${authStore.accessToken}`,
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

function randomDate(start: Date, end: Date) {
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
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
        :text="post.text"
        :username="post.user.username"
        :user-handle="'@' + post.user.username"
        :timestamp="new Date(post.createdAt)"
        :image-url="post.url"
        url="https://www.youtube.com/"
      >
      </Post>
    </div>
    <div v-else>
      <p>Loading posts or no posts to display.</p>
    </div>
  </main>
</template>