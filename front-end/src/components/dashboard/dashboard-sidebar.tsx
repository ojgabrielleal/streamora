import {
  CalendarDays,
  Folder,
  LayoutDashboard,
  ListMusic,
  LogOut,
  Users,
  X,
} from 'lucide-react'

type DashboardSidebarProps = {
  activeDestination: 'dashboard' | 'files' | 'playlists' | 'schedules' | 'users'
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
      <div className="flex items-center justify-between">
        <img
          alt="Streamora"
          className="-ml-2 h-auto w-44 brightness-0 invert"
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

      <nav className="mt-3 flex-1" aria-label="Seções do sistema">
        <p className="px-3 text-xs font-semibold uppercase tracking-[0.18em] text-violet-300/70">
          Gerenciamento
        </p>
        <ul className="mt-3 space-y-1.5">
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
              aria-current={activeDestination === 'schedules' ? 'page' : undefined}
              className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus-visible:ring-2 focus-visible:ring-fuchsia-300 ${activeDestination === 'schedules' ? 'bg-linear-to-r from-violet-600 to-fuchsia-600 text-white shadow-lg shadow-violet-950/25' : 'text-violet-200 hover:bg-white/10 hover:text-white'}`}
              href="/programacao"
              onClick={onClose}
            >
              <CalendarDays aria-hidden="true" className="size-5" />
              Programação
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
            <a
              aria-current={activeDestination === 'users' ? 'page' : undefined}
              className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-medium transition focus-visible:ring-2 focus-visible:ring-fuchsia-300 ${activeDestination === 'users' ? 'bg-linear-to-r from-violet-600 to-fuchsia-600 text-white shadow-lg shadow-violet-950/25' : 'text-violet-200 hover:bg-white/10 hover:text-white'}`}
              href="/usuarios"
              onClick={onClose}
            >
              <Users aria-hidden="true" className="size-5" />
              Usuários
            </a>
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
