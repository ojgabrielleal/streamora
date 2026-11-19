import { afterEach, describe, expect, it, vi } from 'vitest'

import { logout } from '@/api/auth/auth-api'

describe('auth API', () => {
  afterEach(() => {
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('logs out with the refresh cookie and without bearer authentication', async () => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    const fetchMock = vi
      .spyOn(globalThis, 'fetch')
      .mockResolvedValue(new Response(null, { status: 204 }))

    await logout()

    const [url, request] = fetchMock.mock.calls[0]
    const headers = new Headers(request?.headers)

    expect(url.toString()).toBe('http://localhost:8080/auth/logout')
    expect(request?.credentials).toBe('include')
    expect(request?.method).toBe('POST')
    expect(headers.has('Authorization')).toBe(false)
  })
})
