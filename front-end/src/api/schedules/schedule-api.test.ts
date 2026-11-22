import { beforeEach, describe, expect, it, vi } from 'vitest'

import {
  createSchedule,
  deleteSchedule,
  getSchedule,
  getSchedules,
  updateSchedule,
} from '@/api/schedules/schedule-api'
import { apiRequest } from '@/lib/api-client'

vi.mock('@/lib/api-client', () => ({
  apiRequest: vi.fn(),
}))

const mockedApiRequest = vi.mocked(apiRequest)

describe('schedule-api', () => {
  beforeEach(() => {
    mockedApiRequest.mockReset()
  })

  it('lists schedules', async () => {
    mockedApiRequest.mockResolvedValueOnce([])

    await getSchedules()

    expect(mockedApiRequest).toHaveBeenCalledWith('/schedules')
  })

  it('gets a single schedule by uuid', async () => {
    mockedApiRequest.mockResolvedValueOnce({})

    await getSchedule('sched-1')

    expect(mockedApiRequest).toHaveBeenCalledWith('/schedules/sched-1')
  })

  it('creates a schedule', async () => {
    const payload = {
      playlistUuid: 'pl-1',
      dayOfWeek: 'MONDAY' as const,
      startTime: '08:00:00',
      endTime: '12:00:00',
    }
    mockedApiRequest.mockResolvedValueOnce({ uuid: 'sched-1', ...payload })

    await createSchedule(payload)

    expect(mockedApiRequest).toHaveBeenCalledWith('/schedules', {
      method: 'POST',
      json: payload,
    })
  })

  it('updates a schedule', async () => {
    const payload = {
      playlistUuid: 'pl-1',
      dayOfWeek: 'FRIDAY' as const,
      startTime: '14:00:00',
      endTime: '18:00:00',
    }
    mockedApiRequest.mockResolvedValueOnce({ uuid: 'sched-1', ...payload })

    await updateSchedule('sched-1', payload)

    expect(mockedApiRequest).toHaveBeenCalledWith('/schedules/sched-1', {
      method: 'PUT',
      json: payload,
    })
  })

  it('deletes a schedule', async () => {
    mockedApiRequest.mockResolvedValueOnce(undefined)

    await deleteSchedule('sched-1')

    expect(mockedApiRequest).toHaveBeenCalledWith('/schedules/sched-1', {
      method: 'DELETE',
    })
  })
})
