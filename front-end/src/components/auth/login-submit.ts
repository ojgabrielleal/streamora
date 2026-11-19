import type { FormEvent } from 'react'

import type { AuthRequest } from '@/api/auth/auth-types'

type LoginSubmitter = {
  isPending: boolean
  mutate: (credentials: AuthRequest) => void
  reset: () => void
}

export function submitLogin(
  event: FormEvent<HTMLFormElement>,
  credentials: AuthRequest,
  loginSubmitter: LoginSubmitter,
) {
  event.preventDefault()

  if (loginSubmitter.isPending) {
    return
  }

  loginSubmitter.reset()
  loginSubmitter.mutate(credentials)
}
