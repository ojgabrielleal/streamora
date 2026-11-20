import type { ServiceStatuses } from '@/api/services/service-status-types'
import { apiRequest } from '@/lib/api-client'

export function getServiceStatuses() {
  return apiRequest<ServiceStatuses>('/services/status')
}
