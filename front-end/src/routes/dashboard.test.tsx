import { renderToStaticMarkup } from 'react-dom/server'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { clearAccessToken } from '@/lib/access-token'
import { DashboardPage } from '@/routes/dashboard'

const queryStates = vi.hoisted(() => ({
  playlists: {
    data: undefined as [] | undefined,
    isError: false,
    isPending: true,
  },
  services: {
    data: undefined as { icecast: 'ONLINE'; liquidsoap: 'OFFLINE' } | undefined,
    isError: false,
    isPending: true,
  },
}))

vi.mock('@tanstack/react-query', async (importOriginal) => ({
  ...(await importOriginal<typeof import('@tanstack/react-query')>()),
  useQuery: (options: { queryKey: readonly string[] }) =>
    options.queryKey[0] === 'service-statuses'
      ? queryStates.services
      : options.queryKey[0] === 'live-broadcast'
        ? { data: { enabled: true }, isError: false, isPending: false }
        : queryStates.playlists,
  useMutation: () => ({
    isPending: false,
    mutate: vi.fn(),
  }),
  useQueryClient: () => ({
    setQueryData: vi.fn(),
  }),
}))

vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => vi.fn(),
}))

describe('DashboardPage', () => {
  beforeEach(() => {
    queryStates.playlists.data = undefined
    queryStates.playlists.isError = false
    queryStates.playlists.isPending = true
    queryStates.services.data = undefined
    queryStates.services.isError = false
    queryStates.services.isPending = true
    clearAccessToken()
  })

  it('announces playlist loading', () => {
    const markup = renderToStaticMarkup(<DashboardPage />)

    expect(markup).toContain('role="status"')
    expect(markup).toContain('Carregando resumo da programação...')
  })

  it('shows a safe query error', () => {
    queryStates.playlists.isPending = false
    queryStates.playlists.isError = true

    const markup = renderToStaticMarkup(<DashboardPage />)

    expect(markup).toContain('role="alert"')
    expect(markup).toContain('Não foi possível carregar a programação')
  })

  it('shows monitored service statuses', () => {
    queryStates.services.data = { icecast: 'ONLINE', liquidsoap: 'OFFLINE' }
    queryStates.services.isPending = false

    const markup = renderToStaticMarkup(<DashboardPage />)

    expect(markup).toContain('Online')
    expect(markup).toContain('Offline')
  })
})
