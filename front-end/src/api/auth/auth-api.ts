import type { AuthRequest, AuthTokens } from '@/api/auth/auth-types'
import { apiRequest } from '@/lib/api-client'

export function login(credentials: AuthRequest) {
  return apiRequest<AuthTokens>('/login', {
    authenticated: false,
    json: credentials,
    method: 'POST',
  })
}
