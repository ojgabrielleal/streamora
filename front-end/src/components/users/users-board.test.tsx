import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it, vi } from 'vitest'

import { UsersBoard } from '@/components/users/users-board'

function renderUsersBoard(props: Parameters<typeof UsersBoard>[0]) {
  const queryClient = new QueryClient()
  return renderToStaticMarkup(
    <QueryClientProvider client={queryClient}>
      <UsersBoard {...props} />
    </QueryClientProvider>,
  )
}

describe('UsersBoard', () => {
  it('renders loading state', () => {
    const markup = renderUsersBoard({
      isError: false,
      isPending: true,
      onRetry: vi.fn(),
    })

    expect(markup).toContain('Carregando usuários...')
  })

  it('renders error state with retry button', () => {
    const markup = renderUsersBoard({
      isError: true,
      isPending: false,
      onRetry: vi.fn(),
    })

    expect(markup).toContain('Não foi possível carregar os usuários.')
    expect(markup).toContain('Tentar novamente')
  })

  it('renders empty state when no users exist', () => {
    const markup = renderUsersBoard({
      isError: false,
      isPending: false,
      onRetry: vi.fn(),
      users: [],
    })

    expect(markup).toContain('Nenhum usuário cadastrado.')
  })

  it('renders users table with edit and delete actions', () => {
    const markup = renderUsersBoard({
      isError: false,
      isPending: false,
      onRetry: vi.fn(),
      users: [
        {
          uuid: '123',
          fullname: 'João Silva',
          nickname: 'João',
          username: 'joao.silva',
          role: 'STANDARD',
        },
      ],
    })

    expect(markup).toContain('João Silva')
    expect(markup).toContain('João')
    expect(markup).toContain('joao.silva')
    expect(markup).toContain('Editar João Silva')
    expect(markup).toContain('Excluir João Silva')
  })
})
