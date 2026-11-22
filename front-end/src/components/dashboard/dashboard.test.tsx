import { renderToStaticMarkup } from 'react-dom/server'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import type { Playlist } from '@/api/playlists/playlist-types'
import type { ServiceStatuses } from '@/api/services/service-status-types'
import { DashboardSidebar } from '@/components/dashboard/dashboard-sidebar'
import { ScheduleOverview } from '@/components/dashboard/playlist-overview'
import { RadioOverview } from '@/components/dashboard/radio-overview'
import { ServiceOverview } from '@/components/dashboard/service-overview'
import { StreamOverview } from '@/components/dashboard/stream-overview'

describe('dashboard components', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('renders the active destination, future destinations and logout', () => {
    const markup = renderToStaticMarkup(
      <DashboardSidebar activeDestination="dashboard" onLogout={vi.fn()} />,
    )

    expect(markup).toContain('aria-label="Navegação principal"')
    expect(markup).toContain('aria-current="page"')
    expect(markup).toContain('Dashboard')
    expect(markup).toContain('Playlists')
    expect(markup).toContain('Programação')
    expect(markup).toContain('Arquivos')
    expect(markup).toContain('Usuários')
    expect(markup).toContain('href="/programacao"')
    expect(markup).toContain('href="/playlists"')
    expect(markup).toContain('href="/usuarios"')
    expect(markup).toContain('Sair')
  })

  it('describes the radio without presenting unavailable telemetry', () => {
    const markup = renderToStaticMarkup(<RadioOverview />)

    expect(markup).toContain('Auto DJ')
    expect(markup).toContain('Streamora')
    expect(markup).toContain('Tocando')
    expect(markup).toContain('A seguir')
    expect(markup).toContain('Ouvintes indisponíveis')
    expect(markup).toContain('Aguardando integração com a transmissão')
    expect(markup).not.toContain('0 ouvintes')
  })

  it('shows the initial service verification', () => {
    const markup = renderToStaticMarkup(
      <ServiceOverview isError={false} isPending />,
    )

    expect(markup).toContain('Serviço de Transmissão')
    expect(markup).toContain('Icecast')
    expect(markup).toContain('Serviço de Auto DJ')
    expect(markup).toContain('Liquidsoap')
    expect(markup.match(/Verificando/g)).toHaveLength(4)
  })

  it('shows independent service statuses', () => {
    const statuses: ServiceStatuses = {
      icecast: 'ONLINE',
      liquidsoap: 'OFFLINE',
    }
    const markup = renderToStaticMarkup(
      <ServiceOverview isError={false} isPending={false} statuses={statuses} />,
    )

    expect(markup).toContain('Online')
    expect(markup).toContain('Offline')
    expect(markup).toContain('bg-emerald-600')
    expect(markup).toContain('bg-red-600')
    expect(markup).toContain('role="status"')
  })

  it('shows unavailable services when the API fails', () => {
    const markup = renderToStaticMarkup(
      <ServiceOverview isError isPending={false} />,
    )

    expect(markup.match(/Indisponível/g)).toHaveLength(4)
  })

  it('renders the configured Icecast transmission', () => {
    vi.stubEnv('VITE_STREAM_URL', 'http://localhost:8000/stream.mp3')

    const markup = renderToStaticMarkup(<StreamOverview />)

    expect(markup).toContain('Transmissões')
    expect(markup).toContain('Transmissão Icecast')
    expect(markup).toContain('/stream.mp3 (MP3)')
    expect(markup).toContain('http://localhost:8000/stream.mp3')
    expect(markup).toContain('Não disponível')
  })

  it('explains when the public stream URL is not configured', () => {
    vi.stubEnv('VITE_STREAM_URL', '')

    const markup = renderToStaticMarkup(<StreamOverview />)

    expect(markup).toContain('role="status"')
    expect(markup).toContain('Configure `VITE_STREAM_URL`')
  })

  it('renders the recurring playlist schedules', () => {
    const playlists: Playlist[] = [
      {
        uuid: 'playlist-1',
        name: 'Programação principal',
        m3uFileName: 'main.m3u',
        isBase: true,
        weight: 1,
        schedules: [
          {
            uuid: 'schedule-1',
            dayOfWeek: 'MONDAY',
            startTime: '08:00',
            endTime: '10:00',
          },
          {
            uuid: 'schedule-2',
            dayOfWeek: 'TUESDAY',
            startTime: '08:00',
            endTime: '10:00',
          },
        ],
      },
      {
        uuid: 'playlist-2',
        name: 'Especial',
        m3uFileName: null,
        isBase: false,
        weight: 2,
        schedules: [],
      },
    ]

    const scheduleMarkup = renderToStaticMarkup(
      <ScheduleOverview playlists={playlists} />,
    )

    expect(scheduleMarkup).toContain('Agendado')
    expect(scheduleMarkup).toContain('08:00 – 10:00')
    expect(scheduleMarkup).toContain('Segunda-feira')
    expect(scheduleMarkup).toContain('Terça-feira')
    expect(scheduleMarkup).toContain('Programação principal')
  })

  it('renders an accessible empty state', () => {
    const markup = renderToStaticMarkup(<ScheduleOverview playlists={[]} />)

    expect(markup).toContain('role="status"')
    expect(markup).toContain('Nenhum horário programado')
  })
})
