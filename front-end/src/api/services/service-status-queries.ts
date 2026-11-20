import { queryOptions } from '@tanstack/react-query'

import { getServiceStatuses } from '@/api/services/service-status-api'

export const serviceStatusKeys = {
  all: ['service-statuses'] as const,
}

export function serviceStatusesQueryOptions() {
  return queryOptions({
    queryKey: serviceStatusKeys.all,
    queryFn: getServiceStatuses,
    refetchInterval: 30_000,
  })
}
