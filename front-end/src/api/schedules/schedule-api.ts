import type { Schedule, SchedulePayload } from '@/api/schedules/schedule-types'
import { apiRequest } from '@/lib/api-client'

export function getSchedules() {
  return apiRequest<Schedule[]>('/schedules')
}

export function getSchedule(uuid: string) {
  return apiRequest<Schedule>(`/schedules/${uuid}`)
}

export function createSchedule(payload: SchedulePayload) {
  return apiRequest<Schedule>('/schedules', { method: 'POST', json: payload })
}

export function updateSchedule(uuid: string, payload: SchedulePayload) {
  return apiRequest<Schedule>(`/schedules/${uuid}`, {
    method: 'PUT',
    json: payload,
  })
}

export function deleteSchedule(uuid: string) {
  return apiRequest<void>(`/schedules/${uuid}`, { method: 'DELETE' })
}
