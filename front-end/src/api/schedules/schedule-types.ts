export type WeekDay =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY'

export type SchedulePlaylistSummary = {
  uuid: string
  name: string
  m3uFileName: string | null
  isBase: boolean
  weight: number
}

export type Schedule = {
  uuid: string
  playlist: SchedulePlaylistSummary
  dayOfWeek: WeekDay
  startTime: string
  endTime: string
}

export type SchedulePayload = {
  playlistUuid: string
  dayOfWeek: WeekDay
  startTime: string
  endTime: string
}
