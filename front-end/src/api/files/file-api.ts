import type { FtpConnection, FtpFileEntry } from '@/api/files/file-types'
import { apiRequest } from '@/lib/api-client'

export function getFtpConnection() {
  return apiRequest<FtpConnection>('/storage/connection')
}

export function getFileTree() {
  return apiRequest<FtpFileEntry[]>('/storage')
}

export function createDirectory(path: string) {
  return apiRequest<void>('/storage/directories', {
    method: 'POST',
    json: { path },
  })
}

export function renameEntry(path: string, newName: string, directory: boolean) {
  const entryType = directory ? 'directories' : 'files'

  return apiRequest<void>(`/storage/${entryType}/rename`, {
    method: 'PUT',
    json: { path, newName },
  })
}

export function moveEntry(
  path: string,
  targetDirectory: string,
  directory: boolean,
) {
  const entryType = directory ? 'directories' : 'files'

  return apiRequest<void>(`/storage/${entryType}/move`, {
    method: 'PUT',
    json: { path, targetDirectory },
  })
}

export function deleteEntry(path: string, directory: boolean) {
  const entryType = directory ? 'directories' : 'files'
  const query = new URLSearchParams({ path })

  return apiRequest<void>(`/storage/${entryType}?${query}`, {
    method: 'DELETE',
  })
}
