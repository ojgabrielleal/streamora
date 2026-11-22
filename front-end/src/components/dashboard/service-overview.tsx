import { Mic, RadioTower, ServerCog } from 'lucide-react'

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
  liveBroadcastEnabled?: boolean
  isLiveBroadcastPending?: boolean
  isLiveBroadcastError?: boolean
  isUpdatingLiveBroadcast?: boolean
  onToggleLiveBroadcast?: (enabled: boolean) => void
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
  liveBroadcastEnabled,
  isLiveBroadcastPending = false,
  isLiveBroadcastError = false,
  isUpdatingLiveBroadcast = false,
  onToggleLiveBroadcast,
}: ServiceOverviewProps) {
  const liveBroadcastStatusText = isLiveBroadcastPending
    ? 'Verificando'
    : isLiveBroadcastError || liveBroadcastEnabled === undefined
      ? 'Indisponível'
      : liveBroadcastEnabled
        ? 'Habilitada'
        : 'Desabilitada'

  const liveBroadcastBadgeClass =
    isLiveBroadcastPending ||
    isLiveBroadcastError ||
    liveBroadcastEnabled === undefined
      ? 'bg-slate-100 text-slate-600'
      : liveBroadcastEnabled
        ? 'bg-emerald-100 text-emerald-800'
        : 'bg-red-100 text-red-800'

  const canToggleLiveBroadcast =
    !isLiveBroadcastPending &&
    !isLiveBroadcastError &&
    !isUpdatingLiveBroadcast &&
    liveBroadcastEnabled !== undefined &&
    Boolean(onToggleLiveBroadcast)

  return (
    <div className="space-y-8">
      <section
        aria-label="Controle de locução"
        className="overflow-hidden rounded-2xl border border-violet-200/80 bg-white shadow-sm"
      >
        <div className="flex items-center justify-between gap-4 px-5 py-4">
          <div className="flex min-w-0 items-center gap-3.5">
            <span className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-violet-100 text-violet-700">
              <Mic aria-hidden="true" className="size-5" />
            </span>
            <div className="min-w-0">
              <h2 className="text-base font-semibold text-violet-950">Locução</h2>
              <p className="text-xs text-muted-foreground">Transmissão ao vivo</p>
            </div>
          </div>

          <div className="flex shrink-0 items-center gap-2">
            <span
              aria-label={`Locução: ${liveBroadcastStatusText}`}
              className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${liveBroadcastBadgeClass}`}
              role="status"
            >
              {liveBroadcastStatusText}
            </span>
            {onToggleLiveBroadcast ? (
              <button
                className="rounded-full bg-violet-700 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-violet-800 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-violet-700 disabled:cursor-not-allowed disabled:opacity-50"
                disabled={!canToggleLiveBroadcast}
                onClick={() => onToggleLiveBroadcast(!liveBroadcastEnabled)}
                type="button"
              >
                {liveBroadcastEnabled ? 'Desabilitar' : 'Habilitar'}
              </button>
            ) : null}
          </div>
        </div>

        {liveBroadcastEnabled ? (
          <div className="border-t border-violet-100 bg-violet-50/60 px-5 py-3.5">
            <p className="text-xs text-violet-900">
              Host:{' '}
              <code className="font-semibold text-violet-700">
                {typeof window !== 'undefined' ? window.location.hostname : 'localhost'}
              </code>{' '}
              · Porta:{' '}
              <code className="font-semibold text-violet-700">
                {import.meta.env.VITE_LIQUIDSOAP_HARBOR_PORT?.trim() || '8005'}
              </code>{' '}
              · Mount:{' '}
              <code className="font-semibold text-violet-700">
                {import.meta.env.VITE_LIQUIDSOAP_HARBOR_MOUNT?.trim() || '/live'}
              </code>{' '}
              (use seu usuário e senha do sistema)
            </p>
          </div>
        ) : null}
      </section>

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
    </div>
  )
}
