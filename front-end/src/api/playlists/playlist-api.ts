import type {
  Playlist,
  PlaylistDetails,
  PlaylistPayload,
} from '@/api/playlists/playlist-types'
import { apiRequest } from '@/lib/api-client'

export function getPlaylists() {
  return apiRequest<Playlist[]>('/playlist')
}

export function getPlaylist(uuid: string) {
  return apiRequest<PlaylistDetails>(`/playlist/${uuid}`)
}

export function createPlaylist(payload: PlaylistPayload) {
  return apiRequest<Playlist>('/playlist', { method: 'POST', json: payload })
}

export function updatePlaylist(uuid: string, payload: PlaylistPayload) {
  return apiRequest<Playlist>(`/playlist/${uuid}`, { method: 'PUT', json: payload })
}

export function deletePlaylist(uuid: string) {
  return apiRequest<void>(`/playlist/${uuid}`, { method: 'DELETE' })
}

export function setPlaylistTracks(uuid: string, paths: string[]) {
  return apiRequest<PlaylistDetails>(`/playlist/${uuid}/tracks`, {
    method: 'PUT',
    json: { paths },
  })
}
