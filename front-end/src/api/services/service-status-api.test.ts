import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { getServiceStatuses } from '@/api/services/service-status-api'
import { clearAccessToken, setAccessToken } from '@/lib/access-token'

describe('service status API', () => {
  beforeEach(() => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    setAccessToken('access-token')
  })

  afterEach(() => {
    clearAccessToken()
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('gets both service statuses with bearer authentication', async () => {
    const serviceStatuses = { icecast: 'ONLINE', liquidsoap: 'OFFLINE' } as const
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(serviceStatuses), {
        headers: { 'Content-Type': 'application/json' },
        status: 200,
      }),
    )

    const returnedStatuses = await getServiceStatuses()

    const [url, request] = fetchMock.mock.calls[0]
    const headers = new Headers(request?.headers)
    expect(url.toString()).toBe('http://localhost:8080/services/status')
    expect(headers.get('Authorization')).toBe('Bearer access-token')
    expect(returnedStatuses).toEqual(serviceStatuses)
  })
})
