import { render, screen } from '@testing-library/vue'
import HelloWorld from '../HelloWorld.vue'
import { expect, test } from 'vitest'

test('renders properly', () => {
  render(HelloWorld, { props: { msg: 'Hello Vitest' } })
  expect(screen.getByText('Hello Vitest')).toBeTruthy()
})
