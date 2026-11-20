import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import {
  createDirectory,
  deleteEntry,
  getFileTree,
  getFtpConnection,
  moveEntry,
  renameEntry,
} from '@/api/files/file-api'
import { clearAccessToken, setAccessToken } from '@/lib/access-token'

describe('file API', () => {
  beforeEach(() => {
    vi.stubEnv('VITE_API_URL', 'http://localhost:8080')
    setAccessToken('access-token')
  })

  afterEach(() => {
    clearAccessToken()
    vi.unstubAllEnvs()
    vi.restoreAllMocks()
  })

  it('gets the FTP connection and shared file tree', async () => {
    const connection = { host: 'ftp.example.com', port: 21, username: 'radio' }
    const tree = [{ children: [], format: 'mp3', lastModified: '2026-11-20T12:00:00Z', name: 'audio.mp3', size: 5, type: 'FILE' }]
    const fetchMock = vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(jsonResponse(connection))
      .mockResolvedValueOnce(jsonResponse(tree))

    const returnedConnection = await getFtpConnection()
    const returnedTree = await getFileTree()

    expect(fetchMock.mock.calls[0][0].toString()).toBe('http://localhost:8080/storage/connection')
    expect(fetchMock.mock.calls[1][0].toString()).toBe('http://localhost:8080/storage')
    expect(returnedConnection).toEqual(connection)
    expect(returnedTree).toEqual(tree)
  })

  it('sends the expected storage mutations', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(null, { status: 204 }))

    await createDirectory('programas/jornal')
    await renameEntry('audio.mp3', 'abertura.mp3', false)
    await moveEntry('jornal', 'programas', true)
    await deleteEntry('antigos', true)

    expectRequest(fetchMock.mock.calls[0], '/storage/directories', 'POST', { path: 'programas/jornal' })
    expectRequest(fetchMock.mock.calls[1], '/storage/files/rename', 'PUT', { path: 'audio.mp3', newName: 'abertura.mp3' })
    expectRequest(fetchMock.mock.calls[2], '/storage/directories/move', 'PUT', { path: 'jornal', targetDirectory: 'programas' })
    expect(fetchMock.mock.calls[3][0].toString()).toBe('http://localhost:8080/storage/directories?path=antigos')
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
