import { useMutation } from '@tanstack/react-query'
import { useState } from 'react'

import { loginMutationOptions } from '@/api/auth/auth-mutations'
import { submitLogin } from '@/components/auth/login-submit'
import { Button } from '@/components/ui/button'

export function LoginForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const loginMutation = useMutation(loginMutationOptions())

  if (loginMutation.isSuccess) {
    return (
      <div
        className="rounded-xl border border-violet-200 bg-violet-50 px-5 py-4 text-center text-violet-950"
        role="status"
      >
        <p className="font-medium">Login realizado com sucesso.</p>
        <p className="mt-1 text-sm text-muted-foreground">
          Sua sessão está pronta para continuar.
        </p>
      </div>
    )
  }

  const credentials = { password, username }

  return (
    <form
      aria-busy={loginMutation.isPending}
      className="space-y-5"
      onSubmit={(event) => submitLogin(event, credentials, loginMutation)}
    >
      <div className="space-y-2">
        <label className="text-sm font-medium" htmlFor="username">
          Usuário
        </label>
        <input
          autoComplete="username"
          className="h-11 w-full rounded-lg border bg-background px-3 text-sm shadow-xs outline-none transition-[border-color,box-shadow] placeholder:text-muted-foreground hover:border-violet-200 focus-visible:border-violet-300 focus-visible:ring-2 focus-visible:ring-violet-200/40 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={loginMutation.isPending}
          id="username"
          name="username"
          onChange={(event) => setUsername(event.target.value)}
          required
          spellCheck={false}
          type="text"
          value={username}
        />
      </div>

      <div className="space-y-2">
        <label className="text-sm font-medium" htmlFor="password">
          Senha
        </label>
        <input
          autoComplete="current-password"
          className="h-11 w-full rounded-lg border bg-background px-3 text-sm shadow-xs outline-none transition-[border-color,box-shadow] placeholder:text-muted-foreground hover:border-violet-200 focus-visible:border-violet-300 focus-visible:ring-2 focus-visible:ring-violet-200/40 disabled:cursor-not-allowed disabled:opacity-60"
          disabled={loginMutation.isPending}
          id="password"
          name="password"
          onChange={(event) => setPassword(event.target.value)}
          required
          type="password"
          value={password}
        />
      </div>

      {loginMutation.isError && (
        <p className="text-sm text-destructive" role="alert">
          Não foi possível entrar. Verifique suas credenciais e tente novamente.
        </p>
      )}

      <Button
        className="h-11 w-full bg-linear-to-r from-violet-700 to-fuchsia-600 text-white shadow-md shadow-violet-950/15 hover:from-violet-600 hover:to-fuchsia-500"
        disabled={loginMutation.isPending}
        type="submit"
      >
        {loginMutation.isPending ? 'Entrando...' : 'Entrar'}
      </Button>

      <p aria-live="polite" className="sr-only" role="status">
        {loginMutation.isPending ? 'Autenticação em andamento.' : ''}
      </p>
    </form>
  )
}
