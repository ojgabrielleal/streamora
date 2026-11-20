import { describe, expect, it } from 'vitest'

import {
  serviceStatusesQueryOptions,
  serviceStatusKeys,
} from '@/api/services/service-status-queries'

describe('serviceStatusesQueryOptions', () => {
  it('refreshes the status every thirty seconds', () => {
    const options = serviceStatusesQueryOptions()

    expect(options.queryKey).toEqual(serviceStatusKeys.all)
    expect(options.refetchInterval).toBe(30_000)
  })
})
