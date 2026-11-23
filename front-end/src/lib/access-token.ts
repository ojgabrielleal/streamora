import type { UserRole } from '@/api/users/types'

let accessToken: string | null = null

export function getAccessToken() {
  return accessToken
}

export function setAccessToken(token: string) {
  accessToken = token
}

export function clearAccessToken() {
  accessToken = null
}

function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const parts = token.split('.')
    if (parts.length < 2) {
      return null
    }
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(json) as Record<string, unknown>
  } catch {
    return null
  }
}

export function getCurrentUserRole(): UserRole | null {
  if (!accessToken) {
    return null
  }
  const payload = decodeJwtPayload(accessToken)
  const role = payload?.role
  if (role === 'ADMIN' || role === 'STANDARD') {
    return role
  }
  return null
}

export function isCurrentUserAdmin(): boolean {
  return getCurrentUserRole() === 'ADMIN'
}
