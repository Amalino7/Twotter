<template>
  <div class="min-h-screen bg-gray-900 text-white p-6">
    <div class="max-w-2xl mx-auto">
      <h2 class="text-2xl font-bold mb-4">What's happening?</h2>

      <div class="relative">
        <textarea
          v-model="newTwott"
          class="w-full h-48 bg-gray-800 text-white p-3 rounded-lg resize-none border border-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
          placeholder="Write your twott..."
          @input="updateCharCount"
        ></textarea>
        <button
          @click="showEmojiPicker = !showEmojiPicker"
          class="absolute right-3 bottom-3 text-xl hover:scale-110 transition-transform"
        >
          😃
        </button>
        <div v-if="showEmojiPicker" class="absolute -bottom-50 right-16 z-10">
          <Picker
            @select="showEmoji"
            set="twitter"
            :data="emojiIndex"
            theme="dark"
            :emoji-size="18"
            :show-preview="false"
          />
        </div>
      </div>
      <!-- Media upload -->
      <div class="mt-4">
        <label class="block mb-1 text-sm font-semibold">Add media</label>
        <input
          ref="mediaInput"
          type="file"
          multiple
          accept="image/*,video/*"
          @change="handleMediaUpload"
          class="block w-full text-sm text-gray-300 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-600 file:text-white hover:file:bg-blue-700"
        />
        <div v-if="mediaPreviews.length" class="mt-3 grid grid-cols-2 gap-3">
          <div
            v-for="(media, index) in mediaPreviews"
            :key="index"
            class="bg-gray-800 p-2 rounded-lg"
          >
            <img
              v-if="media.type.startsWith('image/')"
              :src="media.url"
              class="rounded-lg max-h-48 object-cover"
              alt="uploaded picture"
            />
            <video v-else controls class="rounded-lg max-h-48 w-full">
              <source :src="media.url" :type="media.type" />
              Your browser does not support the video tag.
            </video>
          </div>
        </div>
      </div>

      <div class="flex items-center justify-between mt-4 text-sm">
        <span :class="charCountColor">{{ charCount }}/280</span>
        <button
          :disabled="!canPost"
          @click="submitTwott"
          class="bg-blue-600 hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed px-6 py-2 rounded-xl transition-all shadow"
        >
          Twott
        </button>
      </div>

      <transition name="fade">
        <p
          v-if="postStatus"
          class="mt-3 text-sm flex items-center gap-2"
          :class="{
            'text-green-400': postStatus === 'Twott posted!',
            'text-red-400': postStatus === 'Something went wrong.',
          }"
        >
          <span v-if="postStatus === 'Twott posted!'">✅</span>
          <span v-else>⚠️</span>
          {{ postStatus }}
        </p>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import data from 'emoji-mart-vue-fast/data/twitter.json';
import { EmojiIndex, Picker } from 'emoji-mart-vue-fast/src';
import 'emoji-mart-vue-fast/css/emoji-mart.css';
import { useAuthStore, apiURL } from '@/stores/auth';

const authStore = useAuthStore();
const emojiIndex = new EmojiIndex(data);
const newTwott = ref('');
const postStatus = ref('');
const charCount = ref(0);
const mediaFiles = ref([]);
const mediaPreviews = ref([]);
const mediaInput = ref(null);
const showEmojiPicker = ref(false);

const updateCharCount = () => {
  charCount.value = newTwott.value.length;
};

const showEmoji = (emoji) => {
  newTwott.value += emoji.native;
  updateCharCount();
  showEmojiPicker.value = false;
};

const charCountColor = computed(() => {
  return charCount.value > 280 ? 'text-red-500' : 'text-gray-400';
});

const canPost = computed(() => {
  return charCount.value > 0 && charCount.value <= 280;
});

const handleMediaUpload = (event) => {
  mediaFiles.value = Array.from(event.target.files);
  mediaPreviews.value = mediaFiles.value.map((file) => ({
    url: URL.createObjectURL(file),
    type: file.type,
  }));
};

const submitTwott = async () => {
  if (!authStore.isAuthenticated || !authStore.user) {
    postStatus.value = 'You must be logged in to post.';
    setTimeout(() => (postStatus.value = ''), 3000);
    return;
  }

  try {
    const response = await fetch(`${apiURL}posts`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${authStore.accessToken}`,
      },
      body: JSON.stringify({
        content: newTwott.value,
        userId: authStore.user.id,
      }),
    });

    if (response.status !== 201) {
      throw new Error('Failed to post twott.');
    }

    newTwott.value = '';
    charCount.value = 0;
    mediaFiles.value = [];
    mediaPreviews.value = [];
    if (mediaInput.value) mediaInput.value.value = null;
    postStatus.value = 'Twott posted!';
  } catch (error) {
    console.error(error);
    postStatus.value = 'Something went wrong.';
  } finally {
    setTimeout(() => (postStatus.value = ''), 3000);
  }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>