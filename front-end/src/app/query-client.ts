import { QueryClient } from '@tanstack/react-query'

import { ApiError } from '@/lib/api-client'

export function shouldRetryRequest(failureCount: number, error: Error) {
  if (error instanceof ApiError && error.status >= 400 && error.status < 500) {
    return false
  }

  return failureCount < 2
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: shouldRetryRequest,
    },
    mutations: {
      retry: false,
    },
  },
})
