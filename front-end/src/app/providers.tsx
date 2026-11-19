import { QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider } from '@tanstack/react-router'

import { queryClient } from '@/app/query-client'
import { SessionBootstrap } from '@/app/session-bootstrap'
import { router } from '@/router'

export function AppProviders() {
  return (
    <QueryClientProvider client={queryClient}>
      <SessionBootstrap>
        <RouterProvider router={router} />
      </SessionBootstrap>
    </QueryClientProvider>
  )
}
