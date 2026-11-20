import { useNavigate } from '@tanstack/react-router'
import { Menu } from 'lucide-react'
import { type ReactNode, useState } from 'react'

import { logout as requestLogout } from '@/api/auth/auth-api'
import { DashboardSidebar } from '@/components/dashboard/dashboard-sidebar'
import { clearAccessToken } from '@/lib/access-token'

type AuthenticatedLayoutProps = {
  activeDestination: 'dashboard' | 'files'
  children: ReactNode
  description: string
  title: string
}

export function AuthenticatedLayout({
  activeDestination,
  children,
  description,
  title,
}: AuthenticatedLayoutProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const navigate = useNavigate()

  async function logout() {
    try {
      await requestLogout()
    } finally {
      clearAccessToken()
      setIsMenuOpen(false)
      await navigate({ to: '/' })
    }
  }

  return (
    <div className="min-h-svh bg-violet-50/50 lg:flex">
      <div className="hidden h-svh shrink-0 lg:sticky lg:top-0 lg:block">
        <DashboardSidebar
          activeDestination={activeDestination}
          onLogout={() => void logout()}
        />
      </div>

      {isMenuOpen && (
        <div className="fixed inset-0 z-50 lg:hidden" role="presentation">
          <button
            aria-label="Fechar menu"
            className="absolute inset-0 bg-violet-950/55 backdrop-blur-sm"
            onClick={() => setIsMenuOpen(false)}
            type="button"
          />
          <div className="relative h-full w-72" role="dialog" aria-modal="true">
            <DashboardSidebar
              activeDestination={activeDestination}
              mobile
              onClose={() => setIsMenuOpen(false)}
              onLogout={() => void logout()}
            />
          </div>
        </div>
      )}

      <main className="min-w-0 flex-1" inert={isMenuOpen ? true : undefined}>
        <header className="border-b border-violet-100 bg-white/85 backdrop-blur">
          <div className="mx-auto flex h-24 w-full max-w-[96rem] items-center gap-4 px-4 sm:px-8 lg:px-12 xl:px-14">
            <button
              aria-expanded={isMenuOpen}
              aria-label="Abrir menu"
              className="rounded-xl border border-violet-100 bg-white p-2.5 text-violet-800 shadow-sm transition hover:bg-violet-50 focus-visible:ring-2 focus-visible:ring-violet-300 lg:hidden"
              onClick={() => setIsMenuOpen(true)}
              type="button"
            >
              <Menu aria-hidden="true" className="size-5" />
            </button>
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-violet-950 sm:text-xl">
                {title}
              </h1>
              <p className="text-xs text-muted-foreground sm:text-sm">
                {description}
              </p>
            </div>
          </div>
        </header>

        {children}
      </main>
    </div>
  )
}
