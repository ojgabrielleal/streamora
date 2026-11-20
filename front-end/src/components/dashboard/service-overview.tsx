import { RadioTower, ServerCog } from 'lucide-react'

import type {
  ServiceAvailabilityStatus,
  ServiceStatuses,
} from '@/api/services/service-status-types'

const services = [
  {
    description: 'Icecast',
    icon: RadioTower,
    id: 'icecast',
    name: 'Serviço de Transmissão',
  },
  {
    description: 'Liquidsoap',
    icon: ServerCog,
    id: 'liquidsoap',
    name: 'Serviço de Auto DJ',
  },
] as const

type ServiceOverviewProps = {
  isError: boolean
  isPending: boolean
  statuses?: ServiceStatuses
}

function statusLabel(
  status: ServiceAvailabilityStatus | undefined,
  isPending: boolean,
  isError: boolean,
) {
  if (isPending) {
    return 'Verificando'
  }

  if (isError || !status) {
    return 'Indisponível'
  }

  return status === 'ONLINE' ? 'Online' : 'Offline'
}

function statusBadgeClass(
  status: ServiceAvailabilityStatus | undefined,
  isPending: boolean,
  isError: boolean,
) {
  if (isPending || isError || !status) {
    return 'bg-violet-950/35'
  }

  return status === 'ONLINE'
    ? 'bg-emerald-600'
    : 'bg-red-600'
}

export function ServiceOverview({
  isError,
  isPending,
  statuses,
}: ServiceOverviewProps) {
  return (
    <section aria-labelledby="service-overview-title" className="space-y-4">
      <div>
        <h2 className="text-lg font-semibold text-violet-950" id="service-overview-title">
          Serviços da rádio
        </h2>
        <p className="mt-1 text-sm text-muted-foreground">
          Componentes responsáveis pela transmissão e programação.
        </p>
      </div>

      <div className="grid gap-4">
        {services.map(({ description, icon: Icon, id, name }) => (
          <article
            className="flex min-h-28 items-center justify-between gap-4 rounded-2xl bg-linear-to-r from-violet-700 to-fuchsia-600 px-5 py-5 text-white shadow-lg shadow-violet-950/15 sm:px-6"
            key={name}
          >
            <div className="flex min-w-0 items-center gap-4">
              <span className="hidden size-11 shrink-0 items-center justify-center rounded-xl bg-white/15 sm:flex">
                <Icon aria-hidden="true" className="size-5" />
              </span>
              <div className="min-w-0">
                <h3 className="text-base font-semibold sm:text-lg">{name}</h3>
                <p className="mt-0.5 text-sm text-violet-100 sm:text-base">
                  {description}
                </p>
              </div>
            </div>

            <span
              aria-label={`${name}: ${statusLabel(statuses?.[id], isPending, isError)}`}
              className={`shrink-0 rounded-full px-3 py-1.5 text-xs font-semibold shadow-sm ${statusBadgeClass(statuses?.[id], isPending, isError)}`}
              role="status"
            >
              {statusLabel(statuses?.[id], isPending, isError)}
            </span>
          </article>
        ))}
      </div>
    </section>
  )
}
