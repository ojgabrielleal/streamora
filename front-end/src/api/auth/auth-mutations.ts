import { mutationOptions } from '@tanstack/react-query'

import { login } from '@/api/auth/auth-api'
import { setAccessToken } from '@/lib/access-token'

export function loginMutationOptions() {
  return mutationOptions({
    mutationFn: login,
    onSuccess: (tokens) => setAccessToken(tokens.accessToken),
  })
}
