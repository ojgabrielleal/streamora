import { Headphones, Mic, Play } from 'lucide-react'

function getStreamName(streamUrl: string) {
  try {
    const pathname = new URL(streamUrl).pathname
    return pathname.split('/').filter(Boolean).at(-1) ?? 'stream.mp3'
  } catch {
    return 'stream.mp3'
  }
}

export function StreamOverview() {
  const streamUrl = import.meta.env.VITE_STREAM_URL?.trim()

  return (
    <section
      aria-labelledby="stream-overview-title"
      className="overflow-hidden rounded-2xl border border-violet-200 bg-violet-950 text-white shadow-lg shadow-violet-950/10"
    >
      <header className="bg-linear-to-r from-violet-700 to-fuchsia-600 px-5 py-4 sm:px-6">
        <h2 className="text-lg font-semibold" id="stream-overview-title">
          Transmissões
        </h2>
      </header>

      <div className="flex items-center justify-between gap-4 border-b border-white/8 px-5 py-4 text-sm font-semibold sm:px-6">
        <span>Transmissão Icecast</span>
        <span>Ouvintes</span>
      </div>

      {streamUrl ? (
        <div className="flex flex-col gap-4 bg-white/5 px-5 py-5 sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <div className="flex min-w-0 items-center gap-4">
            <a
              aria-label="Ouvir transmissão em uma nova aba"
              className="flex size-10 shrink-0 items-center justify-center rounded-full bg-white text-violet-800 transition hover:scale-105 focus-visible:ring-2 focus-visible:ring-fuchsia-300"
              href={streamUrl}
              rel="noreferrer"
              target="_blank"
            >
              <Play aria-hidden="true" className="ml-0.5 size-4 fill-current" />
            </a>
            <div className="min-w-0">
              <p className="font-semibold text-white">
                /{getStreamName(streamUrl)} (MP3)
              </p>
              <a
                className="mt-1 block truncate text-sm text-fuchsia-300 underline-offset-4 hover:underline focus-visible:underline"
                href={streamUrl}
                rel="noreferrer"
                target="_blank"
              >
                {streamUrl}
              </a>
            </div>
          </div>

          <span className="inline-flex shrink-0 items-center gap-2 self-end text-sm text-violet-200 sm:self-auto">
            <Headphones aria-hidden="true" className="size-4" />
            Não disponível
          </span>
        </div>
      ) : (
        <p className="bg-white/5 px-5 py-8 text-center text-sm text-violet-300 sm:px-6" role="status">
          Configure `VITE_STREAM_URL` para disponibilizar o acesso à
          transmissão.
        </p>
      )}
    </section>
  )
}
