import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { clearAccessToken, getAccessToken, setAccessToken } from '@/lib/access-token'
import { ApiError, apiRequest } from '@/lib/api-client'

describe('apiRequest', () => {
  beforeEach(() => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    clearAccessToken()
  })

  afterEach(() => {
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('serializes JSON and preserves custom headers', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ accessToken: 'access' }), {
        headers: { 'Content-Type': 'application/json' },
        status: 200,
      }),
    )

    await apiRequest('/login', {
      authenticated: false,
      headers: { 'X-Request-Id': 'request-id' },
      json: { username: 'user' },
      method: 'POST',
    })

    expect(fetchMock).toHaveBeenCalledOnce()
    const [url, request] = fetchMock.mock.calls[0]
    const headers = new Headers(request?.headers)

    expect(url.toString()).toBe('http://localhost:8080/login')
    expect(request?.body).toBe(JSON.stringify({ username: 'user' }))
    expect(headers.get('Content-Type')).toBe('application/json')
    expect(headers.get('X-Request-Id')).toBe('request-id')
  })

  it('returns JSON and supports responses without content', async () => {
    vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(
        new Response(JSON.stringify({ name: 'Playlist' }), {
          headers: { 'Content-Type': 'application/json' },
          status: 200,
        }),
      )
      .mockResolvedValueOnce(new Response(null, { status: 204 }))

    await expect(apiRequest('/playlist')).resolves.toEqual({ name: 'Playlist' })
    await expect(apiRequest('/playlist/item', { method: 'DELETE' })).resolves.toBeUndefined()
  })

  it('sends the access token and clears it after an authenticated 401', async () => {
    setAccessToken('access-token')
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ message: 'Unauthorized' }), {
        headers: { 'Content-Type': 'application/json' },
        status: 401,
      }),
    )

    await expect(apiRequest('/playlist')).rejects.toMatchObject({
      body: { message: 'Unauthorized' },
      status: 401,
    })

    const headers = new Headers(fetchMock.mock.calls[0][1]?.headers)

    expect(headers.get('Authorization')).toBe('Bearer access-token')
    expect(getAccessToken()).toBeNull()
  })

  it('exposes HTTP failures as ApiError', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response('Unavailable', { status: 503 }),
    )

    const request = apiRequest('/playlist')

    await expect(request).rejects.toBeInstanceOf(ApiError)
    await expect(request).rejects.toMatchObject({
      body: 'Unavailable',
      status: 503,
    })
  })

  it('fails clearly without an API URL', async () => {
    vi.stubEnv('VITE_API_URL', '')

    await expect(apiRequest('/playlist')).rejects.toThrow(
      'VITE_API_URL is not configured',
    )
  })
})
