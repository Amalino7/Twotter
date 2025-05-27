<script setup lang="ts">
import { useUIStore } from '@/stores/ui.ts';

const ui = useUIStore();

async function copyLink() {
  try {
    if (!ui.modalProps.url) {
      throw new Error('URL is not available');
    }
    await navigator.clipboard.writeText(ui.modalProps.url);
    ui.triggerToast('Copied successfully!');
  } catch {
    ui.triggerToast('Failed to copy.');
  }
}

async function shareIfSupported() {
  if (navigator.share) {
    try {
      if (!ui.modalProps.url) {
        throw new Error('URL is not available');
      }
      await navigator.share({
        title: document.title,
        url: ui.modalProps.url,
      });
      ui.triggerToast('Shared successfully!');
    } catch (err) {
      console.log(err);
      ui.triggerToast('Share failed.');
    }
  } else {
    ui.triggerToast('Native share not supported.');
  }
}
</script>

<template>
  <div
    v-if="ui.showModal"
    class="fixed inset-0 bg-blue-400/40 backdrop-blur-sm flex justify-center items-center z-2"
  >
    <div class="bg-gray-800 p-6 rounded-lg shadow-lg w-96">
      <p class="text-white mb-4">Share this link:</p>
      <input
        :value="ui.modalProps.url"
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
          @click="ui.showModal = false"
          class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
