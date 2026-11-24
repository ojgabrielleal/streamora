import { queryOptions } from '@tanstack/react-query'

import {
  getRadioTelemetry,
  getServiceStatuses,
} from '@/api/services/service-status-api'

export const serviceStatusKeys = {
  all: ['service-statuses'] as const,
  telemetry: ['radio-telemetry'] as const,
}

export function serviceStatusesQueryOptions() {
  return queryOptions({
    queryKey: serviceStatusKeys.all,
    queryFn: getServiceStatuses,
    refetchInterval: 30_000,
  })
}

export function radioTelemetryQueryOptions() {
  return queryOptions({
    queryKey: serviceStatusKeys.telemetry,
    queryFn: getRadioTelemetry,
    refetchInterval: 15_000,
  })
}
