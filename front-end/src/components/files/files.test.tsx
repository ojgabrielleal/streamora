import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it } from 'vitest'

import type { FtpFileEntry } from '@/api/files/file-types'
import { FileManager } from '@/components/files/file-manager'
import { FtpConnectionCard } from '@/components/files/ftp-connection-card'

describe('file components', () => {
  it('shows public connection data without exposing a password', () => {
    const markup = renderToStaticMarkup(
      <FtpConnectionCard
        connection={{ host: 'ftp.example.com', port: 21, username: 'radio' }}
        isError={false}
        isPending={false}
      />,
    )

    expect(markup).toContain('ftp.example.com')
    expect(markup).toContain('radio')
    expect(markup).toContain('Para conectar, use a mesma senha de acesso ao painel.')
    expect(markup).not.toContain('type="password"')
  })

  it('renders shared file metadata and management actions', () => {
    const entries: FtpFileEntry[] = [{
      children: [],
      format: 'mp3',
      lastModified: '2026-11-20T12:00:00Z',
      name: 'abertura.mp3',
      size: 2048,
      type: 'FILE',
    }]
    const queryClient = new QueryClient()
    const markup = renderToStaticMarkup(
      <QueryClientProvider client={queryClient}>
        <FileManager entries={entries} isError={false} isPending={false} onRetry={() => undefined} />
      </QueryClientProvider>,
    )

    expect(markup).toContain('abertura.mp3')
    expect(markup).toContain('MP3')
    expect(markup).toContain('2.0 KB')
    expect(markup).toContain('Renomear')
    expect(markup).toContain('Mover')
    expect(markup).toContain('Excluir')
  })

  it('distinguishes loading, errors and empty folders', () => {
    const queryClient = new QueryClient()
    const loadingMarkup = renderManager(queryClient, true, false)
    const errorMarkup = renderManager(queryClient, false, true)
    const emptyMarkup = renderManager(queryClient, false, false)

    expect(loadingMarkup).toContain('Carregando arquivos...')
    expect(errorMarkup).toContain('Não foi possível carregar os arquivos.')
    expect(emptyMarkup).toContain('Esta pasta está vazia.')
  })
})

function renderManager(queryClient: QueryClient, isPending: boolean, isError: boolean) {
  return renderToStaticMarkup(
    <QueryClientProvider client={queryClient}>
      <FileManager entries={[]} isError={isError} isPending={isPending} onRetry={() => undefined} />
    </QueryClientProvider>,
  )
}
