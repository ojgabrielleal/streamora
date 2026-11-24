import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  ChevronRight,
  FileAudio,
  FileQuestion,
  Folder,
  FolderInput,
  FolderPlus,
  MoreHorizontal,
  Pencil,
  RefreshCw,
  Search,
  Trash2,
} from 'lucide-react'
import { useMemo, useState } from 'react'

import {
  createDirectory,
  deleteEntry,
  moveEntry,
  renameEntry,
} from '@/api/files/file-api'
import { fileKeys } from '@/api/files/file-queries'
import type { FtpFileEntry } from '@/api/files/file-types'

type FileManagerProps = {
  entries?: FtpFileEntry[]
  isError: boolean
  isPending: boolean
  onRetry: () => void
}

type FileAction =
  | { kind: 'create'; path: string }
  | { kind: 'delete'; directory: boolean; path: string }
  | { kind: 'move'; directory: boolean; path: string; targetDirectory: string }
  | { kind: 'rename'; directory: boolean; newName: string; path: string }

export function FileManager({
  entries,
  isError,
  isPending,
  onRetry,
}: FileManagerProps) {
  const [directoryPath, setDirectoryPath] = useState<string[]>([])
  const [filter, setFilter] = useState('')
  const queryClient = useQueryClient()
  const currentEntries = findDirectory(entries ?? [], directoryPath)
  const visibleEntries = useMemo(() => {
    const normalizedFilter = filter.trim().toLocaleLowerCase('pt-BR')

    if (!normalizedFilter) return currentEntries

    return currentEntries.filter((entry) =>
      entry.name.toLocaleLowerCase('pt-BR').includes(normalizedFilter),
    )
  }, [currentEntries, filter])
  const mutation = useMutation({
    mutationFn: runAction,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: fileKeys.tree }),
  })

  function currentPath(name?: string) {
    return [...directoryPath, ...(name ? [name] : [])].join('/')
  }

  function requestCreateDirectory() {
    const name = window.prompt('Nome da nova pasta:')?.trim()
    if (!name) return

    mutation.mutate({ kind: 'create', path: currentPath(name) })
  }

  function requestRename(entry: FtpFileEntry) {
    const newName = window.prompt('Novo nome:', entry.name)?.trim()
    if (!newName || newName === entry.name) return

    mutation.mutate({
      kind: 'rename',
      directory: entry.type === 'DIRECTORY',
      newName,
      path: currentPath(entry.name),
    })
  }

  function requestMove(entry: FtpFileEntry) {
    const targetDirectory = window.prompt(
      'Pasta de destino (deixe vazio para a raiz):',
      '',
    )
    if (targetDirectory === null) return

    mutation.mutate({
      kind: 'move',
      directory: entry.type === 'DIRECTORY',
      path: currentPath(entry.name),
      targetDirectory: targetDirectory.trim(),
    })
  }

  function requestDelete(entry: FtpFileEntry) {
    if (!window.confirm(`Excluir “${entry.name}”? Esta ação não pode ser desfeita.`)) {
      return
    }

    mutation.mutate({
      kind: 'delete',
      directory: entry.type === 'DIRECTORY',
      path: currentPath(entry.name),
    })
  }

  return (
    <section
      aria-labelledby="file-manager-title"
      className="overflow-hidden rounded-2xl border border-violet-100 bg-white shadow-sm"
    >
      <header className="flex flex-col gap-4 border-b border-violet-100 px-5 py-5 sm:px-6 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h2 className="text-lg font-semibold text-violet-950" id="file-manager-title">
            Gerenciador de arquivos
          </h2>
          <p className="mt-1 text-sm text-muted-foreground">
            Organize o conteúdo compartilhado da sua rádio.
          </p>
        </div>
        <div className="flex flex-col gap-3 sm:flex-row">
          <label className="relative block">
            <span className="sr-only">Filtrar arquivos</span>
            <Search
              aria-hidden="true"
              className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-violet-400"
            />
            <input
              className="h-10 w-full rounded-xl border border-violet-100 bg-violet-50/50 pl-9 pr-3 text-sm outline-none transition focus-visible:border-violet-300 focus-visible:ring-2 focus-visible:ring-violet-200 sm:w-64"
              onChange={(event) => setFilter(event.target.value)}
              placeholder="Filtrar nesta pasta"
              type="search"
              value={filter}
            />
          </label>
          <button
            className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-linear-to-r from-violet-700 to-fuchsia-600 px-4 text-sm font-medium text-white shadow-sm transition hover:from-violet-600 hover:to-fuchsia-500 focus-visible:ring-2 focus-visible:ring-violet-300 disabled:opacity-60"
            disabled={mutation.isPending}
            onClick={requestCreateDirectory}
            type="button"
          >
            <FolderPlus aria-hidden="true" className="size-4" /> Nova pasta
          </button>
        </div>
      </header>

      <nav aria-label="Caminho atual" className="flex flex-wrap items-center gap-1 border-b border-violet-100 bg-violet-50/50 px-5 py-3 text-sm sm:px-6">
        <button
          className="rounded-md px-2 py-1 font-medium text-violet-700 hover:bg-violet-100 focus-visible:ring-2 focus-visible:ring-violet-300"
          onClick={() => setDirectoryPath([])}
          type="button"
        >
          Raiz
        </button>
        {directoryPath.map((segment, index) => (
          <span className="flex items-center gap-1" key={`${segment}-${index}`}>
            <ChevronRight aria-hidden="true" className="size-4 text-violet-300" />
            <button
              className="rounded-md px-2 py-1 text-violet-700 hover:bg-violet-100 focus-visible:ring-2 focus-visible:ring-violet-300"
              onClick={() => setDirectoryPath(directoryPath.slice(0, index + 1))}
              type="button"
            >
              {segment}
            </button>
          </span>
        ))}
      </nav>

      {mutation.isError && (
        <div className="border-b border-red-200 bg-red-50 px-5 py-3 text-sm text-red-800" role="alert">
          Não foi possível concluir a operação. Verifique o nome ou destino e tente novamente.
        </div>
      )}
      {mutation.isSuccess && (
        <p className="border-b border-emerald-200 bg-emerald-50 px-5 py-3 text-sm text-emerald-800" role="status">
          Operação concluída com sucesso.
        </p>
      )}
      {mutation.isPending && (
        <p className="sr-only" aria-live="polite">Atualizando arquivos...</p>
      )}

      {isPending && <FileState message="Carregando arquivos..." />}
      {isError && (
        <div className="px-5 py-12 text-center" role="alert">
          <p className="text-sm text-red-800">Não foi possível carregar os arquivos.</p>
          <button className="mt-4 inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-violet-700 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={onRetry} type="button">
            <RefreshCw aria-hidden="true" className="size-4" /> Tentar novamente
          </button>
        </div>
      )}
      {!isPending && !isError && currentEntries.length === 0 && (
        <FileState message="Esta pasta está vazia. Conecte-se por FTP ou crie uma nova pasta." />
      )}
      {!isPending && !isError && currentEntries.length > 0 && visibleEntries.length === 0 && (
        <FileState message="Nenhum item corresponde ao filtro informado." />
      )}
      {!isPending && !isError && visibleEntries.length > 0 && (
        <div className="overflow-x-auto">
          <table className="w-full min-w-[42rem] text-left text-sm">
            <thead className="bg-violet-50/70 text-xs uppercase tracking-wider text-violet-500">
              <tr>
                <th className="px-5 py-3 font-semibold sm:px-6">Nome</th>
                <th className="px-4 py-3 font-semibold">Formato</th>
                <th className="px-4 py-3 font-semibold">Tamanho</th>
                <th className="px-4 py-3 font-semibold">Última alteração</th>
                <th className="px-5 py-3 text-right font-semibold sm:px-6">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-violet-50">
              {visibleEntries.map((entry) => (
                <tr className="group hover:bg-violet-50/50" key={entry.name}>
                  <td className="px-5 py-4 sm:px-6">
                    <button
                      className="flex max-w-xs items-center gap-3 rounded-lg text-left font-medium text-violet-950 focus-visible:ring-2 focus-visible:ring-violet-300 disabled:cursor-default"
                      disabled={entry.type !== 'DIRECTORY'}
                      onClick={() => entry.type === 'DIRECTORY' && setDirectoryPath([...directoryPath, entry.name])}
                      type="button"
                    >
                      {entry.type === 'DIRECTORY' ? <Folder className="size-5 fill-violet-100 text-violet-600" /> : <FileIcon format={entry.format} />}
                      <span className="truncate">{entry.name}</span>
                    </button>
                  </td>
                  <td className="px-4 py-4 text-muted-foreground">{entry.type === 'DIRECTORY' ? 'Pasta' : (entry.format?.toUpperCase() ?? 'Arquivo')}</td>
                  <td className="px-4 py-4 text-muted-foreground">{formatSize(entry.size)}</td>
                  <td className="px-4 py-4 text-muted-foreground">{formatDate(entry.lastModified)}</td>
                  <td className="px-5 py-4 sm:px-6">
                    <div className="flex justify-end gap-1">
                      <ActionButton disabled={mutation.isPending} icon={<Pencil aria-hidden="true" className="size-3.5" />} label="Renomear" onClick={() => requestRename(entry)} />
                      <ActionButton disabled={mutation.isPending} icon={<FolderInput aria-hidden="true" className="size-3.5" />} label="Mover" onClick={() => requestMove(entry)} />
                      <ActionButton destructive disabled={mutation.isPending} icon={<Trash2 aria-hidden="true" className="size-3.5" />} label="Excluir" onClick={() => requestDelete(entry)} />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

async function runAction(action: FileAction) {
  switch (action.kind) {
    case 'create': return createDirectory(action.path)
    case 'delete': return deleteEntry(action.path, action.directory)
    case 'move': return moveEntry(action.path, action.targetDirectory, action.directory)
    case 'rename': return renameEntry(action.path, action.newName, action.directory)
  }
}

function findDirectory(entries: FtpFileEntry[], path: string[]) {
  return path.reduce<FtpFileEntry[]>((children, segment) => {
    return children.find((entry) => entry.type === 'DIRECTORY' && entry.name === segment)?.children ?? []
  }, entries)
}

function formatSize(size: number | null) {
  if (size === null) return '—'
  if (size < 1024) return `${size} B`
  if (size < 1024 ** 2) return `${(size / 1024).toFixed(1)} KB`
  if (size < 1024 ** 3) return `${(size / 1024 ** 2).toFixed(1)} MB`
  return `${(size / 1024 ** 3).toFixed(1)} GB`
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value))
}

function FileIcon({ format }: { format: string | null }) {
  const audioFormats = ['aac', 'flac', 'm4a', 'mp3', 'ogg', 'wav']
  return audioFormats.includes(format ?? '')
    ? <FileAudio aria-hidden="true" className="size-5 shrink-0 text-fuchsia-600" />
    : <FileQuestion aria-hidden="true" className="size-5 shrink-0 text-violet-500" />
}

function ActionButton({ destructive = false, disabled = false, icon, label, onClick }: { destructive?: boolean; disabled?: boolean; icon: React.ReactNode; label: string; onClick: () => void }) {
  return (
    <button
      className={`inline-flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-xs font-medium transition focus-visible:ring-2 focus-visible:ring-violet-300 ${destructive ? 'text-red-700 hover:bg-red-50' : 'text-violet-700 hover:bg-violet-100'}`}
      disabled={disabled}
      onClick={onClick}
      type="button"
    >
      {icon}
      {label}
    </button>
  )
}

function FileState({ message }: { message: string }) {
  return (
    <div className="px-5 py-14 text-center" role="status">
      <MoreHorizontal aria-hidden="true" className="mx-auto size-8 text-violet-300" />
      <p className="mt-3 text-sm text-muted-foreground">{message}</p>
    </div>
  )
}
