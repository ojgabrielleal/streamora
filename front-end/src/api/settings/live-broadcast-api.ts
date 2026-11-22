import { apiRequest } from '@/lib/api-client'

export type LiveBroadcastStatus = {
  enabled: boolean
}

export function getLiveBroadcastStatus() {
  return apiRequest<LiveBroadcastStatus>('/settings/live-broadcast')
}

export function updateLiveBroadcastStatus(enabled: boolean) {
  return apiRequest<LiveBroadcastStatus>('/settings/live-broadcast', {
    method: 'PUT',
    json: { enabled },
  })
}
