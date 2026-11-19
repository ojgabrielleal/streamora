import {
  ClipboardList,
  Headphones,
  Music2,
  Play,
  Radio,
  SkipForward,
} from 'lucide-react'

export function RadioOverview() {
  return (
    <section
      aria-labelledby="radio-overview-title"
      className="overflow-hidden rounded-2xl border border-violet-200 bg-violet-950 text-white shadow-xl shadow-violet-950/15"
    >
      <header className="flex flex-col gap-4 bg-linear-to-r from-violet-600 to-fuchsia-600 px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
        <div className="flex items-center gap-3">
          <span className="flex size-9 items-center justify-center rounded-full bg-white text-violet-700 shadow-sm">
            <Play aria-hidden="true" className="ml-0.5 size-4 fill-current" />
          </span>
          <div>
            <h2 className="text-lg font-semibold" id="radio-overview-title">
              Visão da rádio
            </h2>
            <p className="text-xs text-violet-100">Streamora · Auto DJ</p>
          </div>
        </div>

        <div className="flex items-center gap-4 text-sm text-violet-50">
          <span className="inline-flex items-center gap-2">
            <Headphones aria-hidden="true" className="size-4" />
            Ouvintes indisponíveis
          </span>
          <span
            aria-label="Programação automática"
            className="flex size-8 items-center justify-center rounded-lg bg-white/15"
            title="Programação automática"
          >
            <ClipboardList aria-hidden="true" className="size-4" />
          </span>
        </div>
      </header>

      <div className="grid gap-px bg-white/10 md:grid-cols-2">
        <article className="bg-violet-950 px-5 py-6 sm:px-6">
          <div className="mb-4 flex items-center gap-2 text-violet-100">
            <Music2 aria-hidden="true" className="size-5" />
            <h3 className="font-semibold">Tocando</h3>
          </div>

          <div className="flex items-center gap-4">
            <span className="flex size-16 shrink-0 items-center justify-center rounded-xl bg-linear-to-br from-violet-500/30 to-fuchsia-500/30 text-violet-200 ring-1 ring-white/10">
              <Radio aria-hidden="true" className="size-7" />
            </span>
            <div className="min-w-0">
              <p className="font-medium text-white">Informação em tempo real</p>
              <p className="mt-1 text-sm text-violet-300">
                Aguardando integração com a transmissão
              </p>
              <span className="mt-3 inline-flex rounded-md bg-white/10 px-2 py-1 text-xs font-medium text-violet-100">
                Auto DJ
              </span>
            </div>
          </div>
        </article>

        <article className="bg-violet-950 px-5 py-6 sm:px-6">
          <div className="mb-4 flex items-center gap-2 text-violet-100">
            <SkipForward aria-hidden="true" className="size-5" />
            <h3 className="font-semibold">A seguir</h3>
          </div>

          <div className="flex items-center gap-4">
            <span className="flex size-16 shrink-0 items-center justify-center rounded-xl bg-white/5 text-violet-300 ring-1 ring-white/10">
              <Music2 aria-hidden="true" className="size-7" />
            </span>
            <div className="min-w-0">
              <p className="font-medium text-white">Próxima faixa</p>
              <p className="mt-1 text-sm text-violet-300">
                Será exibida após a integração com o Liquidsoap
              </p>
              <span className="mt-3 inline-flex rounded-md bg-white/10 px-2 py-1 text-xs font-medium text-violet-100">
                Em breve
              </span>
            </div>
          </div>
        </article>
      </div>
    </section>
  )
}
