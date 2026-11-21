import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it } from 'vitest'

import type { Playlist } from '@/api/playlists/playlist-types'
import { PlaylistLibrary } from '@/components/playlists/playlist-library'

const playlists: Playlist[] = [
  {
    isBase: true,
    m3uFileName: 'aurora.m3u',
    name: 'Aurora brasileira',
    schedules: [{
      dayOfWeek: 'MONDAY',
      endTime: '09:00',
      startTime: '07:00',
      uuid: 'schedule-1',
    }],
    uuid: 'playlist-1',
    weight: 3,
  },
  {
    isBase: false,
    m3uFileName: null,
    name: 'Noite instrumental',
    schedules: [],
    uuid: 'playlist-2',
    weight: 1,
  },
]

describe('PlaylistLibrary', () => {
  it('renders real summary data and playlist actions', () => {
    const markup = renderLibrary(playlists, false, false)

    expect(markup).toContain('Biblioteca de playlists')
    expect(markup).toContain('Aurora brasileira')
    expect(markup).toContain('Noite instrumental')
    expect(markup).toContain('Fallback base')
    expect(markup).toContain('Peso 3')
    expect(markup).toContain('1 horário')
    expect(markup).toContain('Abrir estúdio')
    expect(markup).toContain('Nova playlist')
  })

  it('distinguishes loading, error and empty states', () => {
    expect(renderLibrary([], true, false)).toContain('Afinando a biblioteca...')
    expect(renderLibrary([], false, true)).toContain('Não foi possível carregar as playlists.')
    expect(renderLibrary([], false, false)).toContain('Sua primeira playlist começa aqui.')
  })
})

function renderLibrary(
  data: Playlist[],
  isPending: boolean,
  isError: boolean,
) {
  const queryClient = new QueryClient()

  return renderToStaticMarkup(
    <QueryClientProvider client={queryClient}>
      <PlaylistLibrary
        isError={isError}
        isPending={isPending}
        onRetry={() => undefined}
        playlists={data}
      />
    </QueryClientProvider>,
  )
}
