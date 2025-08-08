<script setup lang="ts">
import ShareButton from './ShareButton.vue';
import RetweetIcon from '../icons/RetweetIcon.vue';
import CommentIcon from '../icons/CommentIcon.vue';
import convertToTwitterDate from '@/utils/twitterDate.js';
import SocialButton from './SocialButton.vue';
import BetterImage from '@/components/BetterImage.vue';
import TextFormat from '@/components/Utility/TextFormat.vue';
import { api } from '@/utils/api.ts';

const props = defineProps([
  'postId',
  'userId',
  'username',
  'userHandle',
  'text',
  'timestamp',
  'imageUrl',
  'url',
  'likesCount',
  'hasLiked',
  'commentsCount',
  'repostsCount',
]);

const twitterDate = convertToTwitterDate(props.timestamp);

function postLiked(arg: boolean) {
  if (arg) {
    api.post(`/posts/${props.postId}/likes`, {});
  } else {
    console.log('unliked');
    api.delete(`/posts/${props.postId}/likes`);
  }
}
</script>

<template>
  <div
    class="w-full max-w-2xl bg-gray-700 rounded-xl shadow-md overflow-hidden m-4 text-white border border-gray-600"
  >
    <div class="p-4">
      <div class="flex items-center">
        <img alt="User Avatar" class="h-10 w-10 rounded-full" src="https://placehold.co/400" />
        <div class="ml-3">
          <router-link :to="`/user/${userId}`" class="hover:underline">
            <p class="text-sm font-medium text-white truncate">{{ username }}</p>
          </router-link>
          <div class="flex items-center">
            <p class="text-sm text-gray-400 truncate">@{{ userHandle }}</p>
            <p class="text-sm text-gray-400 ml-2 truncate">{{ twitterDate }}</p>
          </div>
        </div>
      </div>
      <div class="mt-4">
        <!--        <TextFormat :content="`<script>alert('You got pwned')</script> Sbeve I am #cool`"></TextFormat>-->
        <!--        <TextFormat :text=" ` <p> <p/> Check out this website: www.example.com and also this one: https://www.another-example.com`"  />-->
        <p class="text-gray-300 break-words">
          <TextFormat :text="text"></TextFormat>
        </p>
        <div v-if="imageUrl" class="mt-4">
          <BetterImage :image-url="imageUrl" />
        </div>
      </div>
      <div class="mt-4 flex justify-between">
        <button class="text-gray-400 hover:text-blue-400 flex items-center">
          <CommentIcon />
          <span class="ml-1">{{ commentsCount }}</span>
        </button>
        <button class="text-gray-400 hover:text-green-400 flex items-center">
          <RetweetIcon />
          <span class="ml-1">{{ repostsCount }}</span>
        </button>

        <SocialButton @liked="postLiked" :has-liked="hasLiked" :model-value="likesCount" />
        <ShareButton :url="url"></ShareButton>
      </div>
    </div>
  </div>
</template>
