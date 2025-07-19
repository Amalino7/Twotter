import './assets/main.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import { useAuthStore } from '@/stores/auth.ts';

const app = createApp(App);

app.use(createPinia());
app.use(router);

const auth = useAuthStore();
auth.init().then(() => app.mount('#app'));
