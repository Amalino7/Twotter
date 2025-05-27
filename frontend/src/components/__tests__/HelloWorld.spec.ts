import { render, screen } from '@testing-library/vue';
import { expect, test } from 'vitest';
import Post from '@/components/PostComponents/Post.vue';
import { createPinia, setActivePinia } from 'pinia';

setActivePinia(createPinia());
test('renders properly', () => {
  render(Post, {
    props: {
      username: 'Tom',
      userHandle: 'josh',
      text: 'this is #fire',
      timestamp: new Date(),
      imageUrl: '',
      url: '',
    },
  });
  expect(screen.getByText('this is')).toBeTruthy();
  expect(screen.getByText('#fire')).toBeTruthy();
  expect(screen.getByText('just now')).toBeTruthy();
});
