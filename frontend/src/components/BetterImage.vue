<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
  const props = defineProps({imageUrl:{type:String,required: true} })
  const imageDimensions = ref<{ width: number; height: number }>({width: 0, height:0})
  const imageLoaded = ref(false);
  function onImageLoad() {
    imageLoaded.value = true;
  }

const placeholderStyle = computed(() => {
  const { width, height } = imageDimensions.value;

  if (width && height) {
    return {
      aspectRatio: `${width} / ${height}`, // sets correct ratio
      width: '100%',
      maxHeight: '24rem',
      backgroundColor: '#e5e7eb', // tailwind gray-200
      borderRadius: '0.5rem',     // tailwind rounded-lg
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    };
  }

  return {
    height: '24rem',
    width: '100%',
    backgroundColor: '#e5e7eb',
    borderRadius: '0.5rem',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  };
});

function  getImageDimensions() {
  const img = new Image();
  img.src = props.imageUrl;
    imageDimensions.value.width = img.width;
    imageDimensions.value.height =img.height
}
  onMounted(() => {getImageDimensions()})
</script>

<template>
  <div>
    <div v-if="!imageLoaded" :style="placeholderStyle" class="text-gray-900">
      <p>Loading image...</p>
    </div>
    <div class="rounded-lg overflow-clip relative shadow-sm shadow-gray-800"
    style="">
      <img :src="imageUrl" alt="background tweet" class="absolute w-full h-auto max-h-96 object-cover blur-xl sepia-30" />
      <img
        v-show="imageLoaded"
        :src="imageUrl"
        alt="Tweet Image"
        class="w-full h-auto max-h-96 object-contain relative rounded-lg"
        @load="onImageLoad"
      />
    </div>

  </div>
</template>


<style scoped>

</style>