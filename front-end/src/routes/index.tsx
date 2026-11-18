import { useMutation, useQuery } from '@tanstack/react-query'
import { Radio } from 'lucide-react'
import { useState } from 'react'

import { loginMutationOptions } from '@/api/auth/auth-mutations'
import { playlistsQueryOptions } from '@/api/playlists/playlist-queries'
import { Button } from '@/components/ui/button'

export function HomePage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const loginMutation = useMutation(loginMutationOptions())
  const isAuthenticated = loginMutation.isSuccess
  const playlistsQuery = useQuery({
    ...playlistsQueryOptions(),
    enabled: isAuthenticated,
  })

  return (
    <main className="flex min-h-svh items-center justify-center bg-muted/40 px-6 py-12">
      <section className="w-full max-w-xl rounded-2xl border bg-card p-8 shadow-sm">
        <div className="mx-auto mb-6 flex size-14 items-center justify-center rounded-full bg-primary text-primary-foreground">
          <Radio aria-hidden="true" className="size-7" />
        </div>

        <p className="mb-2 text-center text-sm font-medium tracking-widest text-muted-foreground uppercase">
          Painel web
        </p>
        <h1 className="text-center text-3xl font-semibold tracking-tight sm:text-4xl">
          Streamora
        </h1>
        <p className="mx-auto mt-4 max-w-md text-center text-balance text-muted-foreground">
          Validação temporária da integração com a API.
        </p>

        <form
          className="mt-8 space-y-4"
          onSubmit={(event) => {
            event.preventDefault()
            loginMutation.mutate({ username, password })
          }}
        >
          <label className="block space-y-2">
            <span className="text-sm font-medium">Usuário</span>
            <input
              className="w-full rounded-md border bg-background px-3 py-2 text-sm"
              onChange={(event) => setUsername(event.target.value)}
              required
              value={username}
            />
          </label>
          <label className="block space-y-2">
            <span className="text-sm font-medium">Senha</span>
            <input
              className="w-full rounded-md border bg-background px-3 py-2 text-sm"
              onChange={(event) => setPassword(event.target.value)}
              required
              type="password"
              value={password}
            />
          </label>
          <Button className="w-full" disabled={loginMutation.isPending} type="submit">
            {loginMutation.isPending ? 'Entrando...' : 'Validar integração'}
          </Button>
        </form>

        {loginMutation.isError && (
          <p className="mt-4 text-sm text-destructive" role="alert">
            Não foi possível autenticar na API.
          </p>
        )}

        {isAuthenticated && (
          <div className="mt-6 rounded-md border p-4" role="status">
            <p className="font-medium">Autenticação concluída.</p>
            {playlistsQuery.isPending && (
              <p className="mt-2 text-sm text-muted-foreground">
                Consultando playlists protegidas...
              </p>
            )}
            {playlistsQuery.isError && (
              <p className="mt-2 text-sm text-destructive">
                A consulta protegida falhou.
              </p>
            )}
            {playlistsQuery.isSuccess && (
              <p className="mt-2 text-sm text-muted-foreground">
                {playlistsQuery.data.length} playlist(s) encontrada(s).
              </p>
            )}
          </div>
        )}
      </section>
    </main>
  )
}
