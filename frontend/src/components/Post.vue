<script setup>
import ShareIcon from './icons/ShareIcon.vue'
import LikeIcon from './icons/LikeIcon.vue'
import RetweetIcon from './icons/RetweetIcon.vue'
import CommentIcon from './icons/CommentIcon.vue'
import convertToTwitterDate from '../utility/twitterDate'
const props = defineProps(['username', 'userHandle', 'text', 'timestamp'])

const twitterDate = convertToTwitterDate(props.timestamp)
</script>

<template>
  <div class="w-full max-w-xl bg-gray-800 rounded-xl shadow-md overflow-hidden m-4 text-white">
    <div class="p-4">
      <div class="flex items-center">
        <img class="h-10 w-10 rounded-full" src="https://placehold.co/400" alt="User Avatar" />
        <div class="ml-3">
          <p class="text-sm font-medium text-white truncate">{{ username }}</p>
          <div class="flex items-center">
            <p class="text-sm text-gray-400 truncate">{{ userHandle }}</p>
            <p class="text-sm text-gray-400 ml-2 truncate">{{ twitterDate }}</p>
          </div>
        </div>
      </div>
      <div class="mt-4">
        <p class="text-gray-300 break-words">{{ text }}</p>
      </div>
      <div class="mt-4 flex justify-between">
        <button
          @click="incrementCommentCount"
          class="text-gray-400 hover:text-blue-400 flex items-center"
        >
          <CommentIcon />
          <span class="ml-1">{{ commentCount }}</span>
        </button>
        <button
          @click="incrementRetweetCount"
          class="text-gray-400 hover:text-green-400 flex items-center"
        >
          <RetweetIcon />
          <span class="ml-1">{{ retweetCount }}</span>
        </button>
        <button
          @click="incrementLikeCount"
          class="text-gray-400 hover:text-pink-400 flex items-center"
        >
          <LikeIcon />
          <span class="ml-1">{{ likeCount }}</span>
        </button>
        <button
          @click="incrementShareCount"
          class="text-gray-400 hover:text-blue-400 flex items-center"
        >
          <ShareIcon />
          <span class="ml-1">{{ shareCount }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style>
/* You can add additional styles here if needed */
</style>
