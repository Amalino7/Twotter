<script setup lang="ts">
import Post from '../components/Post.vue'

const posts = [
  { text: 'hksfdgfgdhsfjdg', user: 'trash', url: 'https://picsum.photos/2000/1000' },
  { text: 'shgksdkg', user: 'meaningful' },
  { text: 'sfhfkg', user: 'random' },
  {
    text: 'gifghksdfgfdsfskhgfgsdkgdsfghdksfgkdfsgdfksdsgghdksghdskdsfdsgfsddksghkdsj',
    user: 'fdsfgjhsfdgjshgfdksdfhsfkhsfsdhshfjkgjsfdgkhfgskda',
  },
]

function randomDate(start: Date, end: Date) {
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
}
console.log(randomDate(new Date(2012, 0, 1), new Date()))
</script>

<template>
  <main class="flex flex-col m-14 items-center">
    <Post
      v-for="post in posts"
      :text="post.text"
      :username="post.user"
      :user-handle="'@' + post.user"
      :timestamp="randomDate(new Date(2012, 0, 1), new Date())"
      :image-url="post.url"
      url="https://www.youtube.com/"
    >
    </Post>
  </main>
</template>
