import type { Playlist } from '@/api/playlists/playlist-types'
import { apiRequest } from '@/lib/api-client'

export function getPlaylists() {
  return apiRequest<Playlist[]>('/playlist')
}
