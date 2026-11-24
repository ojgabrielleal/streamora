import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { notifyLiquidsoapRestartNeeded } from '@/lib/liquidsoap-restart-prompt'
import {
  ArrowDown,
  ArrowLeft,
  ArrowUp,
  CalendarDays,
  ChevronRight,
  Disc3,
  FileAudio,
  Folder,
  ListMusic,
  Music2,
  Pencil,
  Plus,
  RefreshCw,
  Search,
  Sparkles,
  Trash2,
  X,
} from 'lucide-react'
import { useEffect, useMemo, useRef, useState } from 'react'

import { fileTreeQueryOptions } from '@/api/files/file-queries'
import type { FtpFileEntry } from '@/api/files/file-types'
import {
  createPlaylist,
  deletePlaylist,
  setPlaylistTracks,
  updatePlaylist,
} from '@/api/playlists/playlist-api'
import {
  playlistKeys,
  playlistQueryOptions,
} from '@/api/playlists/playlist-queries'
import type {
  Playlist,
  PlaylistPayload,
} from '@/api/playlists/playlist-types'

type PlaylistLibraryProps = {
  isError: boolean
  isPending: boolean
  onRetry: () => void
  playlists?: Playlist[]
}

type Filter = 'all' | 'base' | 'scheduled'

export function PlaylistLibrary({
  isError,
  isPending,
  onRetry,
  playlists = [],
}: PlaylistLibraryProps) {
  const [filter, setFilter] = useState<Filter>('all')
  const [search, setSearch] = useState('')
  const [editing, setEditing] = useState<Playlist | null | undefined>()
  const [studioPlaylist, setStudioPlaylist] = useState<Playlist | null>(null)
  const queryClient = useQueryClient()
  const deleteMutation = useMutation({
    mutationFn: deletePlaylist,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: playlistKeys.all }),
  })

  const visiblePlaylists = useMemo(() => {
    const normalizedSearch = search.trim().toLocaleLowerCase('pt-BR')

    return [...playlists]
      .filter((playlist) => {
        if (filter === 'base' && !playlist.isBase) return false
        if (filter === 'scheduled' && playlist.schedules.length === 0) return false
        return playlist.name.toLocaleLowerCase('pt-BR').includes(normalizedSearch)
      })
      .sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'))
  }, [filter, playlists, search])

  function requestDelete(playlist: Playlist) {
    const confirmed = window.confirm(
      `Excluir “${playlist.name}”? Os arquivos de mídia não serão apagados.`,
    )
    if (confirmed) deleteMutation.mutate(playlist.uuid)
  }

  return (
    <div className="mx-auto max-w-[96rem] px-4 py-8 sm:px-8 sm:py-10 lg:px-12 xl:px-14 xl:py-12">
      <section aria-label="Resumo das playlists" className="grid gap-4 sm:grid-cols-3">
        <Metric icon={Disc3} label="Playlists" value={playlists.length} />
        <Metric
          icon={Sparkles}
          label="No fallback base"
          value={playlists.filter((playlist) => playlist.isBase).length}
        />
        <Metric
          icon={CalendarDays}
          label="Com horários"
          value={playlists.filter((playlist) => playlist.schedules.length > 0).length}
        />
      </section>

      <section className="mt-6 overflow-hidden rounded-3xl border border-violet-100 bg-white shadow-sm" aria-labelledby="library-title">
        <header className="border-b border-violet-100 bg-linear-to-r from-violet-50/80 to-fuchsia-50/60 p-5 sm:p-7">
          <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-fuchsia-600">Sua coleção</p>
              <h2 className="mt-2 text-2xl font-semibold tracking-tight text-violet-950" id="library-title">Biblioteca de playlists</h2>
              <p className="mt-1 text-sm text-muted-foreground">Cada seleção é um universo sonoro pronto para entrar no ar.</p>
            </div>
            <button className="inline-flex h-11 items-center justify-center gap-2 rounded-xl bg-linear-to-r from-violet-700 to-fuchsia-600 px-5 text-sm font-semibold text-white shadow-lg shadow-violet-200 transition hover:from-violet-600 hover:to-fuchsia-500 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={() => setEditing(null)} type="button">
              <Plus aria-hidden="true" className="size-4" /> Nova playlist
            </button>
          </div>

          <div className="mt-6 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <label className="relative block md:max-w-sm md:flex-1">
              <span className="sr-only">Buscar playlists</span>
              <Search aria-hidden="true" className="absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-violet-400" />
              <input className="h-11 w-full rounded-xl border border-violet-100 bg-white pl-10 pr-4 text-sm outline-none transition focus-visible:border-violet-300 focus-visible:ring-2 focus-visible:ring-violet-200" onChange={(event) => setSearch(event.target.value)} placeholder="Buscar pelo nome" type="search" value={search} />
            </label>
            <div aria-label="Filtrar playlists" className="flex rounded-xl bg-violet-100/70 p-1" role="group">
              <FilterButton active={filter === 'all'} onClick={() => setFilter('all')}>Todas</FilterButton>
              <FilterButton active={filter === 'base'} onClick={() => setFilter('base')}>Base</FilterButton>
              <FilterButton active={filter === 'scheduled'} onClick={() => setFilter('scheduled')}>Com horários</FilterButton>
            </div>
          </div>
        </header>

        {deleteMutation.isError && <Notice kind="error">Não foi possível excluir a playlist. Verifique seus vínculos e tente novamente.</Notice>}
        {deleteMutation.isSuccess && <Notice kind="success">Playlist excluída. Seus arquivos de mídia continuam seguros.</Notice>}
        {isPending && <LibraryState message="Afinando a biblioteca..." />}
        {isError && (
          <div className="px-6 py-16 text-center" role="alert">
            <p className="text-sm text-red-800">Não foi possível carregar as playlists.</p>
            <button className="mt-4 inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold text-violet-700 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={onRetry} type="button"><RefreshCw aria-hidden="true" className="size-4" /> Tentar novamente</button>
          </div>
        )}
        {!isPending && !isError && playlists.length === 0 && <LibraryState message="Sua primeira playlist começa aqui. Crie uma seleção e escolha as faixas que dão voz à rádio." />}
        {!isPending && !isError && playlists.length > 0 && visiblePlaylists.length === 0 && <LibraryState message="Nenhuma playlist combina com esta busca ou filtro." />}
        {!isPending && !isError && visiblePlaylists.length > 0 && (
          <div className="grid gap-4 p-5 sm:grid-cols-2 sm:p-7 xl:grid-cols-3">
            {visiblePlaylists.map((playlist, index) => (
              <PlaylistCard
                index={index}
                key={playlist.uuid}
                onDelete={() => requestDelete(playlist)}
                onEdit={() => setEditing(playlist)}
                onOpen={() => setStudioPlaylist(playlist)}
                playlist={playlist}
              />
            ))}
          </div>
        )}
      </section>

      {editing !== undefined && <PlaylistForm onClose={() => setEditing(undefined)} playlist={editing} />}
      {studioPlaylist && <PlaylistStudio onClose={() => setStudioPlaylist(null)} playlist={studioPlaylist} />}
    </div>
  )
}

function PlaylistCard({ index, onDelete, onEdit, onOpen, playlist }: { index: number; onDelete: () => void; onEdit: () => void; onOpen: () => void; playlist: Playlist }) {
  const gradients = [
    'from-violet-700 via-violet-600 to-fuchsia-500',
    'from-fuchsia-700 via-purple-600 to-violet-500',
    'from-indigo-700 via-violet-600 to-pink-500',
  ]

  return (
    <article className="group overflow-hidden rounded-2xl border border-violet-100 bg-white transition hover:-translate-y-0.5 hover:shadow-xl hover:shadow-violet-100">
      <button className={`relative flex h-32 w-full items-end overflow-hidden bg-linear-to-br p-5 text-left text-white focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-white ${gradients[index % gradients.length]}`} onClick={onOpen} type="button">
        <Music2 aria-hidden="true" className="absolute -right-4 -top-5 size-28 rotate-12 text-white/10" />
        <span className="absolute left-5 top-4 rounded-full bg-white/15 px-2.5 py-1 text-[0.65rem] font-semibold uppercase tracking-wider backdrop-blur">{playlist.isBase ? 'Fallback base' : 'Seleção'}</span>
        <span className="relative line-clamp-2 text-xl font-semibold tracking-tight">{playlist.name}</span>
      </button>
      <div className="p-5">
        <div className="flex items-center gap-4 text-xs text-muted-foreground">
          <span className="inline-flex items-center gap-1.5"><CalendarDays className="size-3.5" /> {playlist.schedules.length} {playlist.schedules.length === 1 ? 'horário' : 'horários'}</span>
          <span className="inline-flex items-center gap-1.5"><Sparkles className="size-3.5" /> Peso {playlist.weight}</span>
        </div>
        <p className="mt-4 text-xs text-violet-500">{playlist.m3uFileName ? 'Repertório configurado' : 'Aguardando primeiras faixas'}</p>
        <div className="mt-5 flex items-center justify-between border-t border-violet-50 pt-4">
          <button className="rounded-lg px-2 py-1.5 text-sm font-semibold text-violet-700 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={onOpen} type="button">Abrir estúdio</button>
          <div className="flex gap-1">
            <IconButton label={`Editar ${playlist.name}`} onClick={onEdit}><Pencil className="size-4" /></IconButton>
            <IconButton destructive label={`Excluir ${playlist.name}`} onClick={onDelete}><Trash2 className="size-4" /></IconButton>
          </div>
        </div>
      </div>
    </article>
  )
}

function PlaylistForm({ onClose, playlist }: { onClose: () => void; playlist: Playlist | null }) {
  const [name, setName] = useState(playlist?.name ?? '')
  const [isBase, setIsBase] = useState(playlist?.isBase ?? false)
  const [weight, setWeight] = useState(playlist?.weight ?? 1)
  const [submitted, setSubmitted] = useState(false)
  const queryClient = useQueryClient()
  const mutation = useMutation({
    mutationFn: (payload: PlaylistPayload) => playlist ? updatePlaylist(playlist.uuid, payload) : createPlaylist(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: playlistKeys.all })
      notifyLiquidsoapRestartNeeded()
      onClose()
    },
  })
  const invalidName = submitted && name.trim().length === 0
  const invalidWeight = submitted && (!Number.isInteger(weight) || weight < 1)

  function submit(event: React.FormEvent) {
    event.preventDefault()
    setSubmitted(true)
    if (!name.trim() || !Number.isInteger(weight) || weight < 1) return
    mutation.mutate({ name: name.trim(), isBase, weight, m3uFileName: playlist?.m3uFileName ?? null })
  }

  return (
    <Modal label={playlist ? 'Editar playlist' : 'Criar playlist'} onClose={onClose}>
      <form className="p-6 sm:p-8" onSubmit={submit}>
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-fuchsia-600">{playlist ? 'Ajustar seleção' : 'Nova atmosfera'}</p>
        <h2 className="mt-2 text-2xl font-semibold text-violet-950">{playlist ? 'Editar playlist' : 'Criar playlist'}</h2>
        <p className="mt-2 text-sm text-muted-foreground">Defina a identidade da seleção. As faixas entram no próximo passo, pelo estúdio.</p>
        <label className="mt-7 block text-sm font-semibold text-violet-950">Nome
          <input aria-invalid={invalidName} className="mt-2 h-11 w-full rounded-xl border border-violet-200 px-3 outline-none focus-visible:ring-2 focus-visible:ring-violet-300" onChange={(event) => setName(event.target.value)} placeholder="Ex.: Noites de jazz" value={name} />
        </label>
        {invalidName && <p className="mt-1 text-xs text-red-700">Informe um nome para a playlist.</p>}
        <label className="mt-5 block text-sm font-semibold text-violet-950">Peso no Auto DJ
          <input aria-invalid={invalidWeight} className="mt-2 h-11 w-full rounded-xl border border-violet-200 px-3 outline-none focus-visible:ring-2 focus-visible:ring-violet-300" min="1" onChange={(event) => setWeight(Number(event.target.value))} type="number" value={weight} />
        </label>
        <p className="mt-1 text-xs text-muted-foreground">O peso influencia a combinação com outras playlists base, sem garantir uma frequência exata.</p>
        {invalidWeight && <p className="mt-1 text-xs text-red-700">Use um número inteiro maior ou igual a 1.</p>}
        <label className="mt-6 flex cursor-pointer items-start gap-3 rounded-xl bg-violet-50 p-4">
          <input checked={isBase} className="mt-0.5 size-4 accent-violet-700" onChange={(event) => setIsBase(event.target.checked)} type="checkbox" />
          <span><span className="block text-sm font-semibold text-violet-950">Participa do fallback base</span><span className="mt-1 block text-xs text-muted-foreground">Permite que o Auto DJ combine esta seleção no fluxo contínuo.</span></span>
        </label>
        {mutation.isError && <p className="mt-4 text-sm text-red-700" role="alert">Não foi possível salvar a playlist. Revise os dados e tente novamente.</p>}
        <div className="mt-8 flex justify-end gap-3">
          <SecondaryButton onClick={onClose}>Cancelar</SecondaryButton>
          <PrimaryButton disabled={mutation.isPending}>{mutation.isPending ? 'Salvando...' : 'Salvar playlist'}</PrimaryButton>
        </div>
      </form>
    </Modal>
  )
}

function PlaylistStudio({ onClose, playlist }: { onClose: () => void; playlist: Playlist }) {
  const dialogRef = useRef<HTMLElement>(null)
  const detailsQuery = useQuery({ ...playlistQueryOptions(playlist.uuid) })
  const filesQuery = useQuery(fileTreeQueryOptions())
  const [tracks, setTracks] = useState<string[] | null>(null)
  const [directory, setDirectory] = useState<string[]>([])
  const queryClient = useQueryClient()
  const originalTracks = detailsQuery.data?.tracks ?? []
  const draft = tracks ?? originalTracks
  const dirty = tracks !== null && JSON.stringify(tracks) !== JSON.stringify(originalTracks)
  const saveMutation = useMutation({
    mutationFn: () => setPlaylistTracks(playlist.uuid, draft),
    onSuccess: async (details) => {
      queryClient.setQueryData(playlistKeys.detail(playlist.uuid), details)
      setTracks(null)
      await queryClient.invalidateQueries({ queryKey: playlistKeys.all })
      notifyLiquidsoapRestartNeeded()
    },
  })
  const currentFiles = findDirectory(filesQuery.data ?? [], directory)

  function close() {
    if (!dirty || window.confirm('Descartar as alterações ainda não salvas?')) onClose()
  }

  useDialogFocus(dialogRef, close)

  function addTrack(path: string) {
    if (!draft.includes(path)) setTracks([...draft, path])
  }

  function moveTrack(index: number, direction: -1 | 1) {
    const next = [...draft]
    const target = index + direction
    ;[next[index], next[target]] = [next[target], next[index]]
    setTracks(next)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-stretch justify-end bg-violet-950/55 backdrop-blur-sm" role="presentation">
      <section aria-label={`Estúdio de ${playlist.name}`} aria-modal="true" className="flex h-full w-full flex-col bg-violet-50 shadow-2xl lg:max-w-6xl" ref={dialogRef} role="dialog">
        <header className="flex items-center justify-between border-b border-violet-100 bg-white px-4 py-4 sm:px-7">
          <div className="flex min-w-0 items-center gap-3">
            <button aria-label="Fechar estúdio" className="rounded-xl p-2 text-violet-700 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={close} type="button"><ArrowLeft className="size-5" /></button>
            <div className="min-w-0"><p className="text-xs font-semibold uppercase tracking-wider text-fuchsia-600">Estúdio de faixas</p><h2 className="truncate text-lg font-semibold text-violet-950">{playlist.name}</h2></div>
          </div>
          <PrimaryButton disabled={!dirty || draft.length === 0 || saveMutation.isPending} onClick={() => saveMutation.mutate()}>{saveMutation.isPending ? 'Salvando...' : 'Salvar ordem'}</PrimaryButton>
        </header>

        {saveMutation.isError && <Notice kind="error">Não foi possível salvar as faixas. O rascunho foi preservado.</Notice>}
        {saveMutation.isSuccess && <Notice kind="success">Repertório atualizado com sucesso.</Notice>}
        {detailsQuery.isPending ? <LibraryState message="Abrindo a mesa de som..." /> : detailsQuery.isError ? <LibraryState message="Não foi possível carregar os detalhes desta playlist." /> : (
          <div className="grid min-h-0 flex-1 lg:grid-cols-[minmax(0,1.15fr)_minmax(20rem,0.85fr)]">
            <div className="min-h-0 overflow-y-auto p-5 sm:p-7">
              <div className="flex items-end justify-between"><div><h3 className="font-semibold text-violet-950">Ordem de reprodução</h3><p className="mt-1 text-sm text-muted-foreground">Use os controles para desenhar a sequência.</p></div><span className="rounded-full bg-violet-100 px-3 py-1 text-xs font-semibold text-violet-700">{draft.length} {draft.length === 1 ? 'faixa' : 'faixas'}</span></div>
              {draft.length === 0 && <div className="mt-6 rounded-2xl border border-dashed border-violet-200 bg-white p-10 text-center"><Music2 className="mx-auto size-9 text-violet-300" /><p className="mt-3 text-sm text-muted-foreground">Escolha ao menos uma mídia na coleção ao lado.</p></div>}
              <ol className="mt-5 space-y-2">
                {draft.map((track, index) => <li className="flex items-center gap-3 rounded-xl border border-violet-100 bg-white p-3 shadow-sm" key={track}><span className="w-7 text-center text-xs font-semibold text-violet-400">{String(index + 1).padStart(2, '0')}</span><FileAudio className="size-5 shrink-0 text-fuchsia-600" /><span className="min-w-0 flex-1"><span className="block truncate text-sm font-semibold text-violet-950">{track.split('/').at(-1)}</span><span className="block truncate text-xs text-muted-foreground">{track}</span></span><IconButton disabled={index === 0} label="Mover para cima" onClick={() => moveTrack(index, -1)}><ArrowUp className="size-4" /></IconButton><IconButton disabled={index === draft.length - 1} label="Mover para baixo" onClick={() => moveTrack(index, 1)}><ArrowDown className="size-4" /></IconButton><IconButton destructive label="Remover faixa" onClick={() => setTracks(draft.filter((item) => item !== track))}><X className="size-4" /></IconButton></li>)}
              </ol>
            </div>

            <aside className="min-h-0 overflow-y-auto border-t border-violet-100 bg-white p-5 sm:p-7 lg:border-l lg:border-t-0" aria-label="Coleção de mídias">
              <h3 className="font-semibold text-violet-950">Coleção de mídias</h3><p className="mt-1 text-sm text-muted-foreground">Navegue pelo storage e adicione arquivos.</p>
              <nav aria-label="Pasta de mídias atual" className="mt-5 flex flex-wrap items-center gap-1 text-xs"><button className="rounded-md px-2 py-1 font-semibold text-violet-700 hover:bg-violet-50" onClick={() => setDirectory([])} type="button">Raiz</button>{directory.map((segment, index) => <span className="flex items-center" key={`${segment}-${index}`}><ChevronRight className="size-3 text-violet-300" /><button className="rounded-md px-2 py-1 text-violet-700 hover:bg-violet-50" onClick={() => setDirectory(directory.slice(0, index + 1))} type="button">{segment}</button></span>)}</nav>
              {filesQuery.isPending && <p className="mt-8 text-sm text-muted-foreground" role="status">Carregando coleção...</p>}
              {filesQuery.isError && <p className="mt-8 text-sm text-red-700" role="alert">Não foi possível abrir a coleção de mídias.</p>}
              {!filesQuery.isPending && !filesQuery.isError && currentFiles.length === 0 && <p className="mt-8 text-sm text-muted-foreground">Esta pasta está vazia.</p>}
              <ul className="mt-4 space-y-2">{currentFiles.map((entry) => { const path = [...directory, entry.name].join('/'); const added = draft.includes(path); return <li key={entry.name}><button className="flex w-full items-center gap-3 rounded-xl border border-violet-100 p-3 text-left transition hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300 disabled:opacity-50" disabled={entry.type === 'FILE' && added} onClick={() => entry.type === 'DIRECTORY' ? setDirectory([...directory, entry.name]) : addTrack(path)} type="button">{entry.type === 'DIRECTORY' ? <Folder className="size-5 fill-violet-100 text-violet-600" /> : <FileAudio className="size-5 text-fuchsia-600" />}<span className="min-w-0 flex-1 truncate text-sm font-medium text-violet-950">{entry.name}</span><span className="text-xs text-violet-500">{entry.type === 'DIRECTORY' ? 'Abrir' : added ? 'Adicionada' : 'Adicionar'}</span></button></li> })}</ul>
            </aside>
          </div>
        )}
      </section>
    </div>
  )
}

function findDirectory(entries: FtpFileEntry[], path: string[]) {
  return path.reduce<FtpFileEntry[]>((children, segment) => children.find((entry) => entry.type === 'DIRECTORY' && entry.name === segment)?.children ?? [], entries)
}

function Metric({ icon: Icon, label, value }: { icon: typeof Disc3; label: string; value: number }) {
  return <div className="flex items-center gap-4 rounded-2xl border border-violet-100 bg-white p-5 shadow-sm"><span className="grid size-11 place-items-center rounded-xl bg-linear-to-br from-violet-100 to-fuchsia-100 text-violet-700"><Icon className="size-5" /></span><span><span className="block text-2xl font-semibold text-violet-950">{value}</span><span className="text-xs font-medium text-muted-foreground">{label}</span></span></div>
}

function FilterButton({ active, children, onClick }: { active: boolean; children: React.ReactNode; onClick: () => void }) {
  return <button aria-pressed={active} className={`rounded-lg px-3 py-2 text-xs font-semibold transition focus-visible:ring-2 focus-visible:ring-violet-300 ${active ? 'bg-white text-violet-800 shadow-sm' : 'text-violet-500 hover:text-violet-800'}`} onClick={onClick} type="button">{children}</button>
}

function Modal({ children, label, onClose }: { children: React.ReactNode; label: string; onClose: () => void }) {
  const dialogRef = useRef<HTMLElement>(null)
  useDialogFocus(dialogRef, onClose)

  return <div className="fixed inset-0 z-50 grid place-items-center overflow-y-auto bg-violet-950/55 p-4 backdrop-blur-sm" role="presentation"><section aria-label={label} aria-modal="true" className="relative w-full max-w-lg rounded-3xl bg-white shadow-2xl" ref={dialogRef} role="dialog"><button aria-label="Fechar" className="absolute right-5 top-5 rounded-lg p-2 text-violet-500 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={onClose} type="button"><X className="size-5" /></button>{children}</section></div>
}

function useDialogFocus(
  dialogRef: React.RefObject<HTMLElement | null>,
  onClose: () => void,
) {
  useEffect(() => {
    const previousFocus = document.activeElement as HTMLElement | null
    const dialog = dialogRef.current
    const selector = 'button:not(:disabled), input:not(:disabled), [href], [tabindex]:not([tabindex="-1"])'
    const focusable = () => [...(dialog?.querySelectorAll<HTMLElement>(selector) ?? [])]

    focusable()[0]?.focus()

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        event.preventDefault()
        onClose()
        return
      }
      if (event.key !== 'Tab') return

      const elements = focusable()
      const first = elements[0]
      const last = elements.at(-1)
      if (!first || !last) return
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault()
        last.focus()
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault()
        first.focus()
      }
    }

    document.addEventListener('keydown', handleKeyDown)
    return () => {
      document.removeEventListener('keydown', handleKeyDown)
      previousFocus?.focus()
    }
  }, [dialogRef, onClose])
}

function IconButton({ children, destructive = false, disabled = false, label, onClick }: { children: React.ReactNode; destructive?: boolean; disabled?: boolean; label: string; onClick: () => void }) {
  return <button aria-label={label} className={`rounded-lg p-2 transition focus-visible:ring-2 focus-visible:ring-violet-300 disabled:cursor-not-allowed disabled:opacity-30 ${destructive ? 'text-red-600 hover:bg-red-50' : 'text-violet-500 hover:bg-violet-50 hover:text-violet-800'}`} disabled={disabled} onClick={onClick} type="button">{children}</button>
}

function PrimaryButton({ children, disabled = false, onClick }: { children: React.ReactNode; disabled?: boolean; onClick?: () => void }) {
  return <button className="inline-flex h-10 items-center justify-center rounded-xl bg-linear-to-r from-violet-700 to-fuchsia-600 px-4 text-sm font-semibold text-white transition hover:from-violet-600 hover:to-fuchsia-500 focus-visible:ring-2 focus-visible:ring-violet-300 disabled:cursor-not-allowed disabled:opacity-50" disabled={disabled} onClick={onClick} type={onClick ? 'button' : 'submit'}>{children}</button>
}

function SecondaryButton({ children, onClick }: { children: React.ReactNode; onClick: () => void }) {
  return <button className="h-10 rounded-xl px-4 text-sm font-semibold text-violet-700 hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300" onClick={onClick} type="button">{children}</button>
}

function Notice({ children, kind }: { children: React.ReactNode; kind: 'error' | 'success' }) {
  return <p className={`border-b px-6 py-3 text-sm ${kind === 'error' ? 'border-red-200 bg-red-50 text-red-800' : 'border-emerald-200 bg-emerald-50 text-emerald-800'}`} role={kind === 'error' ? 'alert' : 'status'}>{children}</p>
}

function LibraryState({ message }: { message: string }) {
  return <div className="px-6 py-16 text-center" role="status"><ListMusic className="mx-auto size-10 text-violet-300" /><p className="mx-auto mt-4 max-w-md text-sm leading-6 text-muted-foreground">{message}</p></div>
}
