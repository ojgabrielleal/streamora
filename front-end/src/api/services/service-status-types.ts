export type ServiceAvailabilityStatus = 'ONLINE' | 'OFFLINE'

export type ServiceStatuses = {
  icecast: ServiceAvailabilityStatus
  liquidsoap: ServiceAvailabilityStatus
}

export type TrackTelemetry = {
  title: string
  artist: string | null
  coverUrl?: string | null
}

export type RadioTelemetry = {
  listeners: number | null
  currentTrack: TrackTelemetry | null
  nextTrack: TrackTelemetry | null
}
