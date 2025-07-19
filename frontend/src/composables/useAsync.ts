import { ApiError } from '@/utils/types.ts';

import { onUnmounted, ref } from 'vue'; // Import onUnmounted

interface UseAsyncOptions<Res, Args extends any[] = []> {
  /**
   * If true, call `execute()` immediately on composable setup.
   */
  immediate?: boolean;
  /**
   * Called after a successful request.
   */
  onSuccess?: (data: Res) => void;
  /**
   * Called if the request throws.
   */
  onError?: (error: ApiError) => void;
}

/**
 * A versatile async composable with:
 * - Typed result & error
 * - AbortController cancellation
 * - immediate vs. lazy execution
 * - Args support
 */
console.log('useAsync composable loaded');

export function useAsync<Res, Args extends any[] = []>(
  fn: (signal?: AbortSignal, ...args: Args) => Promise<Res>,
  options: UseAsyncOptions<Res, Args> = {},
) {
  const loading = ref(false);
  const error = ref<ApiError | null>(null);
  const data = ref<Res | null>(null);

  let controller: AbortController | null = null;

  const execute = async (...args: Args) => {
    controller?.abort();
    controller = new AbortController();
    const signal = controller.signal;

    loading.value = true;
    error.value = null;

    try {
      const result = await fn(signal, ...args);
      data.value = result;
      options.onSuccess?.(result);
    } catch (err: any) {
      if (err.name === 'AbortError') {
        console.warn('Request was aborted');
        return;
      }
      const apiErr = err as ApiError;
      error.value = apiErr;
      options.onError?.(apiErr);
    } finally {
      // TODO potential race conditions?
      loading.value = false;
    }
  };
  onUnmounted(() => {
    controller?.abort();
  });

  return { loading, error, data, execute };
}
