import { beforeEach, describe, expect, it } from 'vitest'

import { clearAccessToken, setAccessToken } from '@/lib/access-token'
import { requireAccessToken } from '@/router'

describe('dashboard route guard', () => {
  beforeEach(() => clearAccessToken())

  it('redirects unauthenticated access to the login route', () => {
    expect(requireAccessToken).toThrow()
  })

  it('allows access when an access token exists in memory', () => {
    setAccessToken('access-token')

    expect(requireAccessToken).not.toThrow()
  })
})
