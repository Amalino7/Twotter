import { createRouter, createWebHistory } from 'vue-router';
import Feed from '@/views/Feed.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('@/views/LoginPage.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: Feed,
    },
    {
      path: '/user/me',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/UserProfile.vue'),
    },
    {
      path: '/new',
      name: 'post',
      component: () => import('../views/NewPost.vue'),
    },
  ],
});

export default router;
