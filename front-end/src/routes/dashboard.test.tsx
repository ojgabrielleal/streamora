import { renderToStaticMarkup } from 'react-dom/server'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { clearAccessToken } from '@/lib/access-token'
import { DashboardPage } from '@/routes/dashboard'

const queryState = vi.hoisted(() => ({
  data: undefined as [] | undefined,
  isError: false,
  isPending: true,
}))

vi.mock('@tanstack/react-query', async (importOriginal) => ({
  ...(await importOriginal<typeof import('@tanstack/react-query')>()),
  useQuery: () => queryState,
}))

vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => vi.fn(),
}))

describe('DashboardPage', () => {
  beforeEach(() => {
    queryState.data = undefined
    queryState.isError = false
    queryState.isPending = true
    clearAccessToken()
  })

  it('announces playlist loading', () => {
    const markup = renderToStaticMarkup(<DashboardPage />)

    expect(markup).toContain('role="status"')
    expect(markup).toContain('Carregando resumo da programação...')
  })

  it('shows a safe query error', () => {
    queryState.isPending = false
    queryState.isError = true

    const markup = renderToStaticMarkup(<DashboardPage />)

    expect(markup).toContain('role="alert"')
    expect(markup).toContain('Não foi possível carregar a programação')
  })
})
