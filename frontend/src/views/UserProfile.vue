<template>
  <div class="min-h-screen bg-gray-900 text-white">
    <div class="max-w-4xl mx-auto p-4">
      <!-- User Info -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center space-x-4">
          <img
            src="https://via.placeholder.com/80"
            alt="User avatar"
            class="w-20 h-20 rounded-full border-2 border-gray-700"
          />
          <div>
            <transition name="fade" mode="out-in">
              <div v-if="isEditing" key="edit">
                <input
                  v-model="editableUsername"
                  class="bg-gray-800 text-white p-1 rounded mb-1 block transition-all duration-700"
                />
                <input
                  v-model="editableFullName"
                  class="bg-gray-800 text-white p-1 rounded block transition-all duration-700"
                />
              </div>
              <div v-else key="view">
                <h1 class="text-2xl font-bold">@{{ username }}</h1>
                <p class="text-gray-400">{{ fullName }}</p>
              </div>
            </transition>
          </div>
        </div>
        <div>
          <template v-if="isOwnProfile">
            <button
              v-if="!isEditing"
              @click="isEditing = true"
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
              <span v-else-if="editStatus === 'Edit canceled'"> ❌ </span>
              <span v-else-if="editStatus === 'Save failed'"> ⚠️ </span>
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
          <div :key="isEditing">
            <div v-if="isEditing">
              <textarea
                v-model="editableBio"
                rows="3"
                class="w-full bg-gray-800 text-white p-2 rounded transition-all duration-700"
              ></textarea>
            </div>
            <p v-else class="mb-2">{{ bio }}</p>
          </div>
        </transition>
        <div class="flex space-x-4 text-sm text-gray-400">
          <span><strong class="text-white">123</strong> Following</span>
          <span><strong class="text-white">456</strong> Followers</span>
        </div>
      </div>

      <!-- Tweets -->
      <!--      <div class="space-y-4">-->
      <!--        <div v-for="tweet in tweets" :key="tweet.id" class="bg-gray-800 p-4 rounded-xl shadow">-->
      <!--          <p>{{ tweet.content }}</p>-->
      <!--          <div class="text-sm text-gray-500 mt-2">{{ tweet.timestamp }}</div>-->
      <!--        </div>-->
      <!--      </div>-->

      <Feed></Feed>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Feed from '@/views/Feed.vue'

const isOwnProfile = ref(false)
const isFollowing = ref(false)
const isEditing = ref(false)

const username = ref('username')
const fullName = ref('Full Name')
const bio = ref('This is a sample user bio. It can include interests, occupation, and more.')

const editableUsername = ref(username.value)
const editableFullName = ref(fullName.value)
const editableBio = ref(bio.value)
const editStatus = ref('')

// const tweets = [
//   {
//     id: 1,
//     content: "Just setting up my twttr clone! #vue #tailwind",
//     timestamp: "May 22, 2025",
//   },
//   {
//     id: 2,
//     content: "Loving the dark mode on this new app ✨",
//     timestamp: "May 21, 2025",
//   },
// ];

const toggleFollow = () => {
  isFollowing.value = !isFollowing.value
}

const simulateSaveRequest = () => {
  return new Promise((resolve, reject) => {
    const success = Math.random() > 0.2
    setTimeout(() => (success ? resolve() : reject()), 800)
  })
}

const saveProfile = async () => {
  try {
    await simulateSaveRequest()
    username.value = editableUsername.value
    fullName.value = editableFullName.value
    bio.value = editableBio.value
    isEditing.value = false
    editStatus.value = 'Saved successfully'
  } catch {
    editStatus.value = 'Save failed'
  } finally {
    setTimeout(() => (editStatus.value = ''), 1500)
  }
}

const cancelEdit = () => {
  editableUsername.value = username.value
  editableFullName.value = fullName.value
  editableBio.value = bio.value
  isEditing.value = false
  editStatus.value = 'Edit canceled'
  setTimeout(() => (editStatus.value = ''), 1500)
}
</script>

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
