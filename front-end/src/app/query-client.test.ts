import { describe, expect, it } from 'vitest'

import { shouldRetryRequest } from '@/app/query-client'
import { ApiError } from '@/lib/api-client'

describe('shouldRetryRequest', () => {
  it('does not retry client errors', () => {
    expect(shouldRetryRequest(0, new ApiError(401, undefined))).toBe(false)
    expect(shouldRetryRequest(0, new ApiError(422, undefined))).toBe(false)
  })

  it('limits retries for recoverable failures', () => {
    expect(shouldRetryRequest(0, new Error('Network failure'))).toBe(true)
    expect(shouldRetryRequest(2, new Error('Network failure'))).toBe(false)
  })
})
