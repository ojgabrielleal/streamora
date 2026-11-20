import { useQuery } from '@tanstack/react-query'
import { playlistsQueryOptions } from '@/api/playlists/playlist-queries'
import { serviceStatusesQueryOptions } from '@/api/services/service-status-queries'
import { ScheduleOverview } from '@/components/dashboard/playlist-overview'
import { RadioOverview } from '@/components/dashboard/radio-overview'
import { ServiceOverview } from '@/components/dashboard/service-overview'
import { StreamOverview } from '@/components/dashboard/stream-overview'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'

export function DashboardPage() {
  const playlistsQuery = useQuery(playlistsQueryOptions())
  const serviceStatusesQuery = useQuery(serviceStatusesQueryOptions())

  return (
    <AuthenticatedLayout
      activeDestination="dashboard"
      description="Acompanhe a configuração da sua rádio."
      title="Visão geral"
    >
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
            <ServiceOverview
              isError={serviceStatusesQuery.isError}
              isPending={serviceStatusesQuery.isPending}
              statuses={serviceStatusesQuery.data}
            />
          </aside>
        </div>
    </AuthenticatedLayout>
  )
}
