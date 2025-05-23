<script setup lang="ts">
import Post from '../components/Post.vue'

const posts = [
  { text: 'hksfdgfgdhsfjdg', user: 'trash', url: '' },
  { text: 'shgksdkg', user: 'meaningful', url: '' },
  {
    text: 'sfhfkg',
    user: 'random',
    url: 'https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwebneel.com%2Fdaily%2Fsites%2Fdefault%2Ffiles%2Fimages%2Fdaily%2F05-2018%2Fportrait-photography-by-dennis-drozhzhin.jpg&f=1&nofb=1&ipt=84910c2a70dda384d8ce5b2f2cccb404d5a749ca2201c490f1de3312bbfca066',
  },
  {
    text: 'gifghksdfgfdsfskhgfgsdkgdsfghdksfgkdfsgdfksdsgghdksghdskdsfdsgfsddksghkdsj',
    user: 'fdsfgjhsfdgjshgfdksdfhsfkhsfsdhshfjkgjsfdgkhfgskda',
    url: 'https://i.redd.it/1fnxl0er742f1.png',
  },
]

function randomDate(start: Date, end: Date) {
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
}
console.log(randomDate(new Date(2012, 0, 1), new Date()))
</script>

<template>
  <main class="flex flex-col m-6 items-center">
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
