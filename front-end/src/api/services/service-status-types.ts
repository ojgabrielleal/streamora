export type ServiceAvailabilityStatus = 'ONLINE' | 'OFFLINE'

export type ServiceStatuses = {
  icecast: ServiceAvailabilityStatus
  liquidsoap: ServiceAvailabilityStatus
}
