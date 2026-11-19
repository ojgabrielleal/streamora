import {
  createRootRoute,
  createRoute,
  createRouter,
  redirect,
} from '@tanstack/react-router'

import { HomePage } from '@/routes/index'
import { DashboardPage } from '@/routes/dashboard'
import { RootRoute } from '@/routes/root'
import { getAccessToken } from '@/lib/access-token'

const rootRoute = createRootRoute({
  component: RootRoute,
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  beforeLoad: redirectAuthenticatedUser,
  component: HomePage,
})

export function redirectAuthenticatedUser() {
  if (getAccessToken()) {
    throw redirect({ to: '/dashboard' })
  }
}

export function requireAccessToken() {
  if (!getAccessToken()) {
    throw redirect({ to: '/' })
  }
}

const dashboardRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/dashboard',
  beforeLoad: requireAccessToken,
  component: DashboardPage,
})

const routeTree = rootRoute.addChildren([indexRoute, dashboardRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
