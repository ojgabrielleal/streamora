export type ScheduleSummary = {
  uuid: string
  dayOfWeek: string
  startTime: string
  endTime: string
}

export type Playlist = {
  uuid: string
  name: string
  m3uFileName: string | null
  isBase: boolean
  weight: number
  schedules: ScheduleSummary[]
}

export type PlaylistDetails = Playlist & {
  tracks: string[]
}

export type PlaylistPayload = {
  name: string
  m3uFileName: string | null
  isBase: boolean
  weight: number
}
