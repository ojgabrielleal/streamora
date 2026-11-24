import { apiRequest } from '@/lib/api-client'

export function applyLiquidsoapConfig() {
  return apiRequest<void>('/liquidsoap/apply', {
    method: 'POST',
  })
}
