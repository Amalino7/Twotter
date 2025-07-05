<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { apiURL, useAuthStore, type User } from '@/stores/auth';
import Post from '@/components/PostComponents/Post.vue';
import LoginPage from '@/views/LoginPage.vue';
import type { PostResponse } from '@/types/dtos.ts';
import { useRoute } from 'vue-router';

const authStore = useAuthStore();
const userPosts = ref<PostResponse[]>([]);

const route = useRoute();
const isFollowing = ref(false);
const isEditing = ref(false);
const editStatus = ref('');
const profileUser = ref<User | null>(null);

const editableDisplayName = ref('');
const editableUsername = ref('');
const editableBio = ref('');

const isOwnProfile = computed(() => {
  return route.params.handle === 'me' || route.params.handle === authStore.user?.id;
});

/**
 * Fetches the user's own posts from the API.
 */

onMounted(async () => {
  if (!isOwnProfile.value) {
    await fetchUserProfile(route.params.handle as string);
    if (profileUser.value) {
      await fetchUserPosts(profileUser.value.id);
    }
  } else {
    profileUser.value = authStore.user;
    if (profileUser.value) {
      await fetchUserPosts(profileUser.value.id);
    }
  }
});

const startEdit = () => {
  if (!profileUser.value) return;
  editableDisplayName.value = profileUser.value.displayName;
  editableUsername.value = profileUser.value.name;
  editableBio.value = profileUser.value.bio;
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
  editStatus.value = 'Edit canceled';
  setTimeout(() => (editStatus.value = ''), 1500);
};

const saveProfile = async () => {
  if (!profileUser.value) return;

  const updatedUser: User = {
    ...profileUser.value,
    displayName: editableDisplayName.value,
    name: editableUsername.value,
    bio: editableBio.value,
  };
  authStore.user = updatedUser;

  isEditing.value = false;
  editStatus.value = 'Saved successfully';
  setTimeout(() => (editStatus.value = ''), 1500);

  // TODO put logic
};

const toggleFollow = () => {
  isFollowing.value = !isFollowing.value;
};

async function fetchUserProfile(handle: string) {
  if (!authStore.accessToken) {
    console.error('Authentication token not found.');
    return;
  }

  try {
    const response = await fetch(`${apiURL}users/${handle}`, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch user profile with status: ${response.status}`);
    }

    profileUser.value = await response.json();
  } catch (error) {
    console.error('An error occurred while fetching user profile:', error);
  }
}

async function fetchUserPosts(userId: string) {
  if (!authStore.accessToken) {
    console.error('Authentication token not found.');
    return;
  }

  try {
    const response = await fetch(`${apiURL}users/${userId}/posts`, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch user posts with status: ${response.status}`);
    }

    userPosts.value = await response.json();
  } catch (error) {
    console.error('An error occurred while fetching user posts:', error);
  }
}

onMounted(async () => {
  if (route.params.handle) {
    // Viewing someone else's profile
    await fetchUserProfile(route.params.handle as string);
    if (profileUser.value) {
      await fetchUserPosts(profileUser.value.id);
    }
  } else if (profileUser.value) {
    // Viewing own profile
    profileUser.value = profileUser.value;
    await fetchUserPosts(profileUser.value.id);
  }
});
</script>

<template>
  <div v-if="profileUser" class="min-h-screen bg-gray-900 text-white">
    <div class="max-w-4xl mx-auto p-4">
      <!-- User Info -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center space-x-4">
          <img
            src="https://placehold.co/80"
            alt="User avatar"
            class="w-20 h-20 rounded-full border-2 border-gray-700"
          />
          <div>
            <transition name="fade" mode="out-in">
              <div v-if="isEditing" key="edit">
                <input
                  v-model="editableDisplayName"
                  class="bg-gray-800 text-white p-1 rounded mb-1 block transition-all duration-700"
                />
                <input
                  v-model="editableUsername"
                  class="bg-gray-800 text-white p-1 rounded block transition-all duration-700"
                />
              </div>
              <div v-else key="view">
                <h1 class="text-2xl font-bold">{{ profileUser.displayName }}</h1>
                <p class="text-gray-400">@{{ profileUser.name }}</p>
              </div>
            </transition>
          </div>
        </div>
        <div>
          <template v-if="isOwnProfile">
            <button
              v-if="!isEditing"
              @click="startEdit"
              class="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-xl shadow"
            >
              Edit Profile
            </button>
            <div v-else class="space-x-2">
              <button
                @click="saveProfile"
                class="bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded-xl shadow"
              >
                Save
              </button>
              <button
                @click="cancelEdit"
                class="bg-gray-600 hover:bg-gray-700 text-white py-2 px-4 rounded-xl shadow"
              >
                Cancel
              </button>
            </div>
            <p
              v-if="editStatus"
              class="mt-2 text-sm flex items-center gap-1"
              :class="{
                'text-green-400': editStatus === 'Saved successfully',
                'text-red-400': editStatus === 'Edit canceled' || editStatus === 'Save failed',
              }"
            >
              <span v-if="editStatus === 'Saved successfully'"> ✅ </span>
              <span v-else> ❌ </span>
              {{ editStatus }}
            </p>
          </template>
          <button
            v-else
            @click="toggleFollow"
            class="follow-button py-2 px-4 shadow"
            :class="{
              'bg-blue-600 hover:bg-blue-700 rounded-xl': !isFollowing,
              'bg-red-600 hover:bg-red-700 rounded-full': isFollowing,
            }"
          >
            {{ isFollowing ? 'Unfollow' : 'Follow' }}
          </button>
        </div>
      </div>

      <!-- Bio and Stats -->
      <div class="mb-6">
        <transition name="fade" mode="out-in">
          <div>
            <div v-if="isEditing">
              <textarea
                v-model="editableBio"
                rows="3"
                class="w-full bg-gray-800 text-white p-2 rounded"
              ></textarea>
            </div>
            <p v-else class="mb-2">{{ profileUser?.bio }}</p>
          </div>
        </transition>
        <div class="flex space-x-4 text-sm text-gray-400">
          <span><strong class="text-white">123</strong> Following</span>
          <span><strong class="text-white">456</strong> Followers</span>
        </div>
      </div>

      <!-- User's Posts -->
      <div class="space-y-4">
        <h2 class="text-xl font-bold border-b border-gray-700 pb-2">
          {{ isOwnProfile ? 'My Posts' : `${profileUser?.displayName}'s Posts` }}
        </h2>
        <div v-if="userPosts.length > 0">
          <Post
            v-for="post in userPosts"
            :key="post.id"
            :text="post.content"
            :username="post.userDisplayName"
            :user-handle="post.userHandle"
            :timestamp="new Date(post.createdAt)"
            :image-url="post.imageUrl"
            :likes-count="post.likesCount"
            :comments-count="post.commentsCount"
            :reposts-count="post.repostsCount"
            :has-liked="post.hasLiked"
          />
        </div>
        <div v-else>
          <p>No posts to display yet.</p>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="flex justify-center items-center min-h-screen text-white">
    <LoginPage></LoginPage>
  </div>
</template>

<style scoped>
.follow-button {
  transition: all 0.3s ease-in-out;
  transform-origin: center;
  transition-property: background-color, transform, border-radius, padding;
}

.follow-button:hover {
  transform: scale(1.05);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.6s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
