import { ref, onMounted, onUnmounted } from 'vue'

export function useWindowSize() {
  const width = ref(window.innerWidth)

  const updateWidth = () => {
    width.value = window.innerWidth
  }

  onMounted(() => window.addEventListener('resize', updateWidth))
  onUnmounted(() => window.removeEventListener('resize', updateWidth))

  return { width }
}
