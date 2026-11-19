import { RadioTower, ServerCog } from 'lucide-react'

const services = [
  {
    description: 'Icecast',
    icon: RadioTower,
    name: 'Serviço de Transmissão',
  },
  {
    description: 'Liquidsoap',
    icon: ServerCog,
    name: 'Serviço de Auto DJ',
  },
]

export function ServiceOverview() {
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
        {services.map(({ description, icon: Icon, name }) => (
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

            <span className="shrink-0 rounded-full border border-white/25 bg-violet-950/35 px-3 py-1.5 text-xs font-semibold shadow-sm">
              Não monitorado
            </span>
          </article>
        ))}
      </div>
    </section>
  )
}
