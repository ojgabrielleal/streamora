import type { AccessTokenResponse, AuthRequest } from '@/api/auth/auth-types'
import { apiRequest } from '@/lib/api-client'

export function login(credentials: AuthRequest) {
  return apiRequest<AccessTokenResponse>('/login', {
    authenticated: false,
    credentials: 'include',
    json: credentials,
    method: 'POST',
  })
}

export function logout() {
  return apiRequest<void>('/auth/logout', {
    authenticated: false,
    credentials: 'include',
    method: 'POST',
  })
}
