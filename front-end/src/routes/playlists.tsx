import { useQuery } from '@tanstack/react-query'

import { playlistsQueryOptions } from '@/api/playlists/playlist-queries'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { PlaylistLibrary } from '@/components/playlists/playlist-library'

export function PlaylistsPage() {
  const playlistsQuery = useQuery(playlistsQueryOptions())

  return (
    <AuthenticatedLayout
      activeDestination="playlists"
      description="Crie atmosferas, organize faixas e dê ritmo ao Auto DJ."
      title="Playlists"
    >
      <PlaylistLibrary
        isError={playlistsQuery.isError}
        isPending={playlistsQuery.isPending}
        onRetry={() => void playlistsQuery.refetch()}
        playlists={playlistsQuery.data}
      />
    </AuthenticatedLayout>
  )
}
