import { ApiError } from '@/utils/types.ts';
import { onUnmounted, ref } from 'vue';

interface UseAsyncOptions<Res, Args extends any[]> {
  onSuccess?: (data: Res) => void;
  onError?: (error: ApiError) => void;
  /**
   * If true, will call execute(...args) immediately on mount.
   */
  immediate?: boolean;
  /**
   * Args to use for the immediate execution.
   */
  args?: Args;
}

export function useAsync<Res, Args extends any[] = []>(
  fn: (signal: AbortSignal, ...args: Args) => Promise<Res>,
  options: UseAsyncOptions<Res, Args> = {},
) {
  const loading = ref(false);
  const data = ref<Res | null>(null);
  const error = ref<ApiError | null>(null);

  let controller = new AbortController();
  let callId = 0;

  const abort = () => {
    controller.abort();
    controller = new AbortController();
  };

  const execute = async (...args: Args): Promise<Res | void> => {
    abort();
    const thisCall = ++callId;

    loading.value = true;
    error.value = null;

    try {
      const result = await fn(controller.signal, ...args);
      if (thisCall !== callId) return;
      data.value = result;
      options.onSuccess?.(result);
      return result;
    } catch (err: any) {
      if (err.name === 'AbortError') {
        console.warn('Request aborted');
        return;
      }
      const apiErr = err as ApiError;
      if (thisCall !== callId) return;
      error.value = apiErr;
      options.onError?.(apiErr);
      throw apiErr;
    } finally {
      if (thisCall === callId) loading.value = false;
    }
  };

  onUnmounted(abort);

  // auto‑execute if requested
  if (options.immediate) {
    // @ts-ignore
    execute(...(options.args ?? ([] as unknown)));
  }

  return { loading, data, error, execute, abort };
}
