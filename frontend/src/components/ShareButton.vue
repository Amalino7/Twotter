<template>
  <div>
    <button @click="showModal = true" class="text-gray-400 hover:text-blue-400 flex items-center">
      <ShareIcon />
    </button>

    <div
      v-if="showModal"
      class="fixed inset-0 bg-gray-600/40 backdrop-blur-sm flex justify-center items-center"
    >
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg w-96">
        <p class="text-white mb-4">Share this link:</p>
        <input
          :value="url"
          readonly
          @click="($event.target as HTMLInputElement).select()"
          class="w-full p-2 mb-4 bg-gray-700 text-white rounded border border-gray-600"
        />
        <div class="flex justify-between">
          <button
            @click="copyLink"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
          >
            Copy
          </button>
          <button
            @click="shareIfSupported"
            class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
          >
            Use Native Share
          </button>
          <button
            @click="showModal = false"
            class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
          >
            Close
          </button>
        </div>
      </div>
    </div>
    <Notification :notification="notification" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ShareIcon from './icons/ShareIcon.vue'
import Notification from './Notification.vue'

const showModal = ref(false)
const { url } = defineProps({ url: {type: String, required: true} ,})
const notification = ref({ show: false, message: '' })

function showNotification(message: string) {
  notification.value.message = message
}

async function copyLink() {
  try {
    await navigator.clipboard.writeText(url)
    showNotification('Copied successfully!')
  } catch {
    showNotification('Failed to copy.')
  }
}

async function shareIfSupported() {
  if (navigator.share) {
    try {
      await navigator.share({
        title: document.title,
        url: url,
      })
      showNotification('Shared successfully!')
    } catch (err) {
      console.log(err)
      showNotification('Share failed.')
    }
  } else {
    showNotification('Native share not supported.')
  }
}
</script>

<style></style>
