import {
  CalendarDays,
  Folder,
  LayoutDashboard,
  ListMusic,
  LogOut,
  X,
} from 'lucide-react'

type DashboardSidebarProps = {
  activeDestination: 'dashboard' | 'files' | 'playlists'
  mobile?: boolean
  onClose?: () => void
  onLogout: () => void
}

export function DashboardSidebar({
  activeDestination,
  mobile = false,
  onClose,
  onLogout,
}: DashboardSidebarProps) {
  return (
    <aside
      aria-label="Navegação principal"
      className="flex h-full w-72 flex-col bg-violet-950 px-4 py-5 text-violet-50 shadow-2xl shadow-violet-950/20"
    >
      <div className="flex items-center justify-between px-2">
        <img
          alt="Streamora"
          className="h-auto w-36 brightness-0 invert"
          src="/images/brand/logo.png"
        />
        {mobile && (
          <button
            aria-label="Fechar menu"
            className="rounded-lg p-2 text-violet-200 transition hover:bg-white/10 hover:text-white focus-visible:ring-2 focus-visible:ring-fuchsia-300"
            onClick={onClose}
            type="button"
          >
            <X aria-hidden="true" className="size-5" />
          </button>
        )}
      </div>

      <nav className="mt-10 flex-1" aria-label="Seções do sistema">
        <p className="px-3 text-xs font-semibold uppercase tracking-[0.18em] text-violet-300/70">
          Gerenciamento
        </p>
        <ul className="mt-3 space-y-1.5">
          <li>
            <a
              aria-current={activeDestination === 'playlists' ? 'page' : undefined}
              className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus-visible:ring-2 focus-visible:ring-fuchsia-300 ${activeDestination === 'playlists' ? 'bg-linear-to-r from-violet-600 to-fuchsia-600 text-white shadow-lg shadow-violet-950/25' : 'text-violet-200 hover:bg-white/10 hover:text-white'}`}
              href="/playlists"
              onClick={onClose}
            >
              <ListMusic aria-hidden="true" className="size-5" />
              Playlists
            </a>
          </li>
          <li>
            <a
              aria-current={activeDestination === 'dashboard' ? 'page' : undefined}
              className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus-visible:ring-2 focus-visible:ring-fuchsia-300 ${activeDestination === 'dashboard' ? 'bg-linear-to-r from-violet-600 to-fuchsia-600 text-white shadow-lg shadow-violet-950/25' : 'text-violet-200 hover:bg-white/10 hover:text-white'}`}
              href="/dashboard"
              onClick={onClose}
            >
              <LayoutDashboard aria-hidden="true" className="size-5" />
              Dashboard
            </a>
          </li>
          <li>
            <a
              aria-current={activeDestination === 'files' ? 'page' : undefined}
              className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus-visible:ring-2 focus-visible:ring-fuchsia-300 ${activeDestination === 'files' ? 'bg-linear-to-r from-violet-600 to-fuchsia-600 text-white shadow-lg shadow-violet-950/25' : 'text-violet-200 hover:bg-white/10 hover:text-white'}`}
              href="/arquivos"
              onClick={onClose}
            >
              <Folder aria-hidden="true" className="size-5" />
              Arquivos
            </a>
          </li>
          <li>
            <span aria-disabled="true" className="flex cursor-not-allowed items-center gap-3 rounded-xl px-3 py-3 text-sm text-violet-300/65">
              <CalendarDays aria-hidden="true" className="size-5" />
              <span className="flex-1">Programação</span>
              <span className="rounded-full bg-white/8 px-2 py-0.5 text-[0.625rem] font-semibold uppercase tracking-wide">Em breve</span>
            </span>
          </li>
        </ul>
      </nav>

      <div className="border-t border-white/10 pt-4">
        <button
          className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium text-violet-200 transition hover:bg-white/10 hover:text-white focus-visible:ring-2 focus-visible:ring-fuchsia-300"
          onClick={onLogout}
          type="button"
        >
          <LogOut aria-hidden="true" className="size-5" />
          Sair
        </button>
      </div>
    </aside>
  )
}
