import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it, vi } from 'vitest'

import type { Schedule } from '@/api/schedules/schedule-types'
import { ScheduleBoard } from '@/components/schedules/schedule-board'

function renderWithQueryClient(ui: React.ReactElement) {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  })
  return renderToStaticMarkup(
    <QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>,
  )
}

describe('ScheduleBoard', () => {
  it('renders loading state', () => {
    const markup = renderWithQueryClient(
      <ScheduleBoard
        isError={false}
        isPending={true}
        onRetry={vi.fn()}
        schedules={undefined}
      />,
    )

    expect(markup).toContain('Carregando grade de programação...')
  })

  it('renders error state with retry button', () => {
    const markup = renderWithQueryClient(
      <ScheduleBoard
        isError={true}
        isPending={false}
        onRetry={vi.fn()}
        schedules={undefined}
      />,
    )

    expect(markup).toContain('Não foi possível carregar a grade de programação.')
    expect(markup).toContain('Tentar novamente')
  })

  it('renders empty state when there are no schedules', () => {
    const markup = renderWithQueryClient(
      <ScheduleBoard
        isError={false}
        isPending={false}
        onRetry={vi.fn()}
        schedules={[]}
      />,
    )

    expect(markup).toContain('Nenhum horário cadastrado')
  })

  it('renders schedules grouped by day of week', () => {
    const schedules: Schedule[] = [
      {
        uuid: 'sched-1',
        dayOfWeek: 'MONDAY',
        startTime: '08:00:00',
        endTime: '12:00:00',
        playlist: {
          uuid: 'pl-1',
          name: 'Manhã Pop',
          m3uFileName: 'manha.m3u',
          isBase: false,
          weight: 10,
        },
      },
    ]

    const markup = renderWithQueryClient(
      <ScheduleBoard
        isError={false}
        isPending={false}
        onRetry={vi.fn()}
        schedules={schedules}
      />,
    )

    expect(markup).toContain('Segunda-feira')
    expect(markup).toContain('Manhã Pop')
    expect(markup).toContain('08:00 - 12:00')
  })
})
