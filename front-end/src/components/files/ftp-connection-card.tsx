import { Check, Clipboard, KeyRound, Server } from 'lucide-react'
import { useState } from 'react'

import type { FtpConnection } from '@/api/files/file-types'

type FtpConnectionCardProps = {
  connection?: FtpConnection
  isError: boolean
  isPending: boolean
}

export function FtpConnectionCard({
  connection,
  isError,
  isPending,
}: FtpConnectionCardProps) {
  const [copiedField, setCopiedField] = useState<string | null>(null)

  async function copy(label: string, value: string) {
    await navigator.clipboard.writeText(value)
    setCopiedField(label)
    window.setTimeout(() => setCopiedField(null), 1800)
  }

  return (
    <section
      aria-labelledby="ftp-connection-title"
      className="overflow-hidden rounded-2xl border border-violet-200 bg-violet-950 text-white shadow-xl shadow-violet-950/15"
    >
      <header className="flex items-center gap-3 bg-linear-to-r from-violet-600 to-fuchsia-600 px-5 py-4 sm:px-6">
        <span className="flex size-10 items-center justify-center rounded-xl bg-white/15">
          <Server aria-hidden="true" className="size-5" />
        </span>
        <div>
          <h2 className="font-semibold" id="ftp-connection-title">
            Acesso FTP
          </h2>
          <p className="text-xs text-violet-100">
            Use estes dados no seu cliente FTP
          </p>
        </div>
      </header>

      {isPending && (
        <p className="px-6 py-8 text-sm text-violet-200" role="status">
          Carregando dados de conexão...
        </p>
      )}
      {isError && (
        <p className="px-6 py-8 text-sm text-red-200" role="alert">
          Não foi possível carregar os dados de conexão.
        </p>
      )}
      {connection && (
        <div>
          <dl className="grid gap-px bg-white/10 sm:grid-cols-3">
            <ConnectionField
              copied={copiedField === 'Host'}
              label="Host"
              onCopy={() => void copy('Host', connection.host)}
              value={connection.host}
            />
            <ConnectionField
              copied={copiedField === 'Porta'}
              label="Porta"
              onCopy={() => void copy('Porta', String(connection.port))}
              value={String(connection.port)}
            />
            <ConnectionField
              copied={copiedField === 'Usuário'}
              label="Usuário"
              onCopy={() => void copy('Usuário', connection.username)}
              value={connection.username}
            />
          </dl>
          <p className="flex items-center gap-2 border-t border-white/10 bg-violet-950 px-5 py-3 text-xs text-violet-200">
            <KeyRound aria-hidden="true" className="size-4 shrink-0" />
            Para conectar, use a mesma senha de acesso ao painel.
          </p>
        </div>
      )}
      <p aria-live="polite" className="sr-only">
        {copiedField ? `${copiedField} copiado.` : ''}
      </p>
    </section>
  )
}

function ConnectionField({
  copied,
  label,
  onCopy,
  value,
}: {
  copied: boolean
  label: string
  onCopy: () => void
  value: string
}) {
  return (
    <div className="bg-violet-950 px-5 py-5">
      <dt className="text-xs font-semibold uppercase tracking-wider text-violet-300">
        {label}
      </dt>
      <dd className="mt-2 flex items-center gap-2">
        <span className="min-w-0 flex-1 truncate text-sm font-medium" title={value}>
          {value}
        </span>
        <button
          aria-label={`Copiar ${label.toLowerCase()}`}
          className="rounded-lg p-2 text-violet-300 transition hover:bg-white/10 hover:text-white focus-visible:ring-2 focus-visible:ring-fuchsia-300"
          onClick={onCopy}
          type="button"
        >
          {copied ? (
            <Check aria-hidden="true" className="size-4 text-emerald-300" />
          ) : (
            <Clipboard aria-hidden="true" className="size-4" />
          )}
        </button>
      </dd>
    </div>
  )
}
