import {
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router'

import { HomePage } from '@/routes/index'
import { RootRoute } from '@/routes/root'

const rootRoute = createRootRoute({
  component: RootRoute,
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: HomePage,
})

const routeTree = rootRoute.addChildren([indexRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
