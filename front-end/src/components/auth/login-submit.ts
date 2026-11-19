import type { FormEvent } from 'react'

import type { AuthRequest } from '@/api/auth/auth-types'

type LoginSubmitter = {
  isPending: boolean
  mutate: (
    credentials: AuthRequest,
    options?: { onSuccess: () => void },
  ) => void
  reset: () => void
}

export function submitLogin(
  event: FormEvent<HTMLFormElement>,
  credentials: AuthRequest,
  loginSubmitter: LoginSubmitter,
  onSuccess?: () => void,
) {
  event.preventDefault()

  if (loginSubmitter.isPending) {
    return
  }

  loginSubmitter.reset()

  if (onSuccess) {
    loginSubmitter.mutate(credentials, { onSuccess })
    return
  }

  loginSubmitter.mutate(credentials)
}
