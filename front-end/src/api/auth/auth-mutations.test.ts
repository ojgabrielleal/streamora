import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { loginMutationOptions } from '@/api/auth/auth-mutations'
import { clearAccessToken, getAccessToken } from '@/lib/access-token'

describe('loginMutationOptions', () => {
  beforeEach(() => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    clearAccessToken()
  })

  afterEach(() => {
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('authenticates without a bearer token and keeps only the access token', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify({
          accessToken: 'access-token',
          refreshToken: 'refresh-token',
        }),
        {
          headers: { 'Content-Type': 'application/json' },
          status: 200,
        },
      ),
    )
    const options = loginMutationOptions()
    const credentials = { password: 'password', username: 'username' }
    const tokens = await options.mutationFn?.(credentials, {} as never)

    options.onSuccess?.(tokens!, credentials, undefined, {} as never)

    const headers = new Headers(fetchMock.mock.calls[0][1]?.headers)

    expect(headers.has('Authorization')).toBe(false)
    expect(getAccessToken()).toBe('access-token')
    expect(tokens).toEqual({
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
    })
  })
})
