import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import {
  createPlaylist,
  deletePlaylist,
  getPlaylist,
  getPlaylists,
  setPlaylistTracks,
  updatePlaylist,
} from '@/api/playlists/playlist-api'
import type { PlaylistPayload } from '@/api/playlists/playlist-types'
import { clearAccessToken, setAccessToken } from '@/lib/access-token'

describe('playlist API', () => {
  beforeEach(() => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    setAccessToken('access-token')
  })

  afterEach(() => {
    clearAccessToken()
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('gets the library and playlist details', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(jsonResponse([]))
      .mockResolvedValueOnce(jsonResponse({ uuid: 'playlist-1', tracks: [] }))

    await getPlaylists()
    await getPlaylist('playlist-1')

    expect(fetchMock.mock.calls[0][0].toString()).toBe('http://localhost:8080/playlist')
    expect(fetchMock.mock.calls[1][0].toString()).toBe('http://localhost:8080/playlist/playlist-1')
  })

  it('sends existing mutation contracts without changing the backend', async () => {
    const payload: PlaylistPayload = {
      isBase: true,
      m3uFileName: null,
      name: 'Noites de jazz',
      weight: 2,
    }
    const fetchMock = vi.spyOn(globalThis, 'fetch')
      .mockImplementation(() => Promise.resolve(jsonResponse({ uuid: 'playlist-1' })))

    await createPlaylist(payload)
    await updatePlaylist('playlist-1', { ...payload, m3uFileName: 'playlist-1.m3u' })
    await setPlaylistTracks('playlist-1', ['jazz/blue.mp3'])
    await deletePlaylist('playlist-1')

    expectRequest(fetchMock.mock.calls[0], '/playlist', 'POST', payload)
    expectRequest(fetchMock.mock.calls[1], '/playlist/playlist-1', 'PUT', {
      ...payload,
      m3uFileName: 'playlist-1.m3u',
    })
    expectRequest(fetchMock.mock.calls[2], '/playlist/playlist-1/tracks', 'PUT', {
      paths: ['jazz/blue.mp3'],
    })
    expect(fetchMock.mock.calls[3][0].toString()).toBe('http://localhost:8080/playlist/playlist-1')
    expect(fetchMock.mock.calls[3][1]?.method).toBe('DELETE')
  })
})

function jsonResponse(value: unknown) {
  return new Response(JSON.stringify(value), {
    headers: { 'Content-Type': 'application/json' },
    status: 200,
  })
}

function expectRequest(
  [url, request]: [URL | RequestInfo, RequestInit?],
  path: string,
  method: string,
  body: unknown,
) {
  expect(url.toString()).toBe(`http://localhost:8080${path}`)
  expect(request?.method).toBe(method)
  expect(request?.body).toBe(JSON.stringify(body))
}
