import { useQuery } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { Menu } from 'lucide-react'
import { useState } from 'react'

import { playlistsQueryOptions } from '@/api/playlists/playlist-queries'
import { DashboardSidebar } from '@/components/dashboard/dashboard-sidebar'
import { ScheduleOverview } from '@/components/dashboard/playlist-overview'
import { RadioOverview } from '@/components/dashboard/radio-overview'
import { ServiceOverview } from '@/components/dashboard/service-overview'
import { StreamOverview } from '@/components/dashboard/stream-overview'
import { clearAccessToken } from '@/lib/access-token'

export function DashboardPage() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const navigate = useNavigate()
  const playlistsQuery = useQuery(playlistsQueryOptions())

  function logout() {
    clearAccessToken()
    setIsMenuOpen(false)
    void navigate({ to: '/' })
  }

  return (
    <div className="min-h-svh bg-violet-50/50 lg:flex">
      <div className="hidden h-svh shrink-0 lg:sticky lg:top-0 lg:block">
        <DashboardSidebar onLogout={logout} />
      </div>

      {isMenuOpen && (
        <div className="fixed inset-0 z-50 lg:hidden" role="presentation">
          <button
            aria-label="Fechar menu"
            className="absolute inset-0 bg-violet-950/55 backdrop-blur-sm"
            onClick={() => setIsMenuOpen(false)}
            type="button"
          />
          <div className="relative h-full w-72" role="dialog" aria-modal="true">
            <DashboardSidebar
              mobile
              onClose={() => setIsMenuOpen(false)}
              onLogout={logout}
            />
          </div>
        </div>
      )}

      <main className="min-w-0 flex-1" inert={isMenuOpen ? true : undefined}>
        <header className="border-b border-violet-100 bg-white/85 backdrop-blur">
          <div className="mx-auto flex h-24 w-full max-w-[96rem] items-center gap-4 px-4 sm:px-8 lg:px-12 xl:px-14">
            <button
              aria-expanded={isMenuOpen}
              aria-label="Abrir menu"
              className="rounded-xl border border-violet-100 bg-white p-2.5 text-violet-800 shadow-sm transition hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300 lg:hidden"
              onClick={() => setIsMenuOpen(true)}
              type="button"
            >
              <Menu aria-hidden="true" className="size-5" />
            </button>
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-violet-950 sm:text-xl">
                Visão geral
              </h1>
              <p className="text-xs text-muted-foreground sm:text-sm">
                Acompanhe a configuração da sua rádio.
              </p>
            </div>
          </div>
        </header>

        <div className="mx-auto grid max-w-[96rem] items-start gap-8 px-4 py-8 sm:px-8 sm:py-10 lg:px-12 xl:grid-cols-[minmax(0,3fr)_minmax(22rem,2fr)] xl:px-14 xl:py-12">
          <div className="min-w-0 space-y-8">
            <RadioOverview />

            {playlistsQuery.isPending && (
              <div
                aria-live="polite"
                className="rounded-2xl border border-violet-100 bg-white px-6 py-10 text-center text-sm text-muted-foreground"
                role="status"
              >
                Carregando resumo da programação...
              </div>
            )}

            {playlistsQuery.isError && (
              <div
                className="rounded-2xl border border-red-200 bg-red-50 px-6 py-5 text-sm text-red-800"
                role="alert"
              >
                Não foi possível carregar a programação. Tente novamente mais
                tarde.
              </div>
            )}

            {playlistsQuery.data && (
              <ScheduleOverview playlists={playlistsQuery.data} />
            )}

            <StreamOverview />
          </div>

          <aside aria-label="Estado da rádio" className="min-w-0 space-y-8">
            <ServiceOverview />
          </aside>
        </div>
      </main>
    </div>
  )
}
