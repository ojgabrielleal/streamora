import { beforeEach, describe, expect, it } from 'vitest'

import { clearAccessToken, setAccessToken } from '@/lib/access-token'
import { redirectAuthenticatedUser, requireAccessToken } from '@/router'

describe('dashboard route guard', () => {
  beforeEach(() => clearAccessToken())

  it('redirects unauthenticated access to the login route', () => {
    expect(requireAccessToken).toThrow()
  })

  it('allows access when an access token exists in memory', () => {
    setAccessToken('access-token')

    expect(requireAccessToken).not.toThrow()
  })

  it('redirects an authenticated user away from the login route', () => {
    setAccessToken('access-token')

    expect(redirectAuthenticatedUser).toThrow()
  })

  it('allows the login route without an authenticated session', () => {
    expect(redirectAuthenticatedUser).not.toThrow()
  })
})
