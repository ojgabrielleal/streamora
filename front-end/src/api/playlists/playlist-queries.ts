import { queryOptions } from '@tanstack/react-query'

import { getPlaylists } from '@/api/playlists/playlist-api'

export const playlistKeys = {
  all: ['playlists'] as const,
}

export function playlistsQueryOptions() {
  return queryOptions({
    queryKey: playlistKeys.all,
    queryFn: getPlaylists,
  })
}
