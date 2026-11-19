import { useEffect, useState } from 'react'

import { refreshAccessToken } from '@/lib/api-client'

type SessionBootstrapProps = {
  children: React.ReactNode
}

export function SessionBootstrap({ children }: SessionBootstrapProps) {
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    refreshAccessToken()
      .catch(() => undefined)
      .finally(() => setIsReady(true))
  }, [])

  if (!isReady) {
    return (
      <main className="flex min-h-svh items-center justify-center" role="status">
        Restaurando sessão...
      </main>
    )
  }

  return children
}
