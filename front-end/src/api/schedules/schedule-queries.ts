import { queryOptions } from '@tanstack/react-query'

import { getSchedule, getSchedules } from '@/api/schedules/schedule-api'

export const scheduleKeys = {
  all: ['schedules'] as const,
  detail: (uuid: string) => ['schedules', uuid] as const,
}

export function schedulesQueryOptions() {
  return queryOptions({
    queryKey: scheduleKeys.all,
    queryFn: getSchedules,
  })
}

export function scheduleQueryOptions(uuid: string) {
  return queryOptions({
    queryKey: scheduleKeys.detail(uuid),
    queryFn: () => getSchedule(uuid),
  })
}
