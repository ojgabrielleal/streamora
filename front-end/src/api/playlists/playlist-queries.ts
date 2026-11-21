import { queryOptions } from '@tanstack/react-query'

import { getPlaylist, getPlaylists } from '@/api/playlists/playlist-api'

export const playlistKeys = {
  all: ['playlists'] as const,
  detail: (uuid: string) => ['playlists', uuid] as const,
}

export function playlistsQueryOptions() {
  return queryOptions({
    queryKey: playlistKeys.all,
    queryFn: getPlaylists,
  })
}

export function playlistQueryOptions(uuid: string) {
  return queryOptions({
    queryKey: playlistKeys.detail(uuid),
    queryFn: () => getPlaylist(uuid),
  })
}
